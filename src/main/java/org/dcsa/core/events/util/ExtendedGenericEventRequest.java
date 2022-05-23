package org.dcsa.core.events.util;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.dcsa.core.events.model.*;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.extendedrequest.QueryField;
import org.dcsa.core.extendedrequest.QueryFields;
import org.dcsa.core.query.DBEntityAnalysis;
import org.dcsa.skernel.model.Location;
import org.dcsa.skernel.model.Vessel;
import org.springframework.data.annotation.Transient;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.sql.Join;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.core.sql.TableLike;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to handle the fact that an Event can have multiple subEvents (Transport, Equipment, Shipment). Used primarily
 * when converting between JSON property names, POJO field names and DB column names. Also handles BillOfLading filtering
 * as this requires a Join with the Shipment table
 */
public class ExtendedGenericEventRequest extends ExtendedRequest<Event> {

    private static final String EVENT_TRANSPORT_CALL_ID_COLUMN_NAME = "transport_call_id";
    private static final String TRANSPORT_CALL_ID_COLUMN_NAME = "id";

    private static final String TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME = "transportDocumentReference";
    private static final String TRANSPORT_DOCUMENT_ID_JSON_NAME = "transportDocumentID";
    private static final String VESSEL_IMO_NUMBER_JSON_NAME = "vesselIMONumber";

    private static final TableLike EVENT_DOCUMENT_REFERENCE_TABLE = Table.create("event_document_reference");
    private static final SqlIdentifier DOCUMENT_ID_COLUMN = SqlIdentifier.unquoted("document_id");
    private static final SqlIdentifier LINK_TYPE_COLUMN = SqlIdentifier.unquoted("link_type");
    private static final SqlIdentifier CARRIER_BOOKING_REFERENCE_COLUMN = SqlIdentifier.unquoted("carrier_booking_reference");
    private static final SqlIdentifier CARRIER_BOOKING_REQUEST_REFERENCE_COLUMN = SqlIdentifier.unquoted("carrier_booking_request_reference");
    private static final SqlIdentifier TRANSPORT_DOCUMENT_REFERENCE_COLUMN = SqlIdentifier.unquoted("transport_document_reference");
    private static final SqlIdentifier DOCUMENT_REFERENCE_TYPE_COLUMN = SqlIdentifier.unquoted("document_reference_type");

    private static final String TRANSPORT_CALL_ID_COLUMN = "transport_call_id";
    private static final String CARRIER_BOOKING_REFERENCE_JSON_NAME = "carrierBookingReference";
    private static final String BOOKING_REFERENCE_JSON_NAME = "bookingReference";
    private static final String CARRIER_BOOKING_REQUEST_REFERENCE_JSON_NAME = "carrierBookingRequestReference";
    private static final String DOCUMENT_REFERENCE_TYPE_JSON_NAME = "documentReferenceType";

    private static final Set<String> JSON_FIELDS_REQUIRING_DISTINCT = Set.of(
            TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME,
            TRANSPORT_DOCUMENT_ID_JSON_NAME,
            VESSEL_IMO_NUMBER_JSON_NAME,
            CARRIER_BOOKING_REFERENCE_JSON_NAME,
            CARRIER_BOOKING_REQUEST_REFERENCE_JSON_NAME
    );

    private static final Set<Class<? extends Event>> KNOWN_EVENT_CLASSES;

    static {
        JsonSubTypes jsonSubTypes = Event.class.getAnnotation(JsonSubTypes.class);
        if (jsonSubTypes != null) {
            KNOWN_EVENT_CLASSES = new HashSet<>();
            for (JsonSubTypes.Type type : jsonSubTypes.value()) {
                Class<?> rawClass = type.value();
                if (!Event.class.isAssignableFrom(rawClass)) {
                    throw new IllegalStateException(rawClass.getSimpleName()
                            + " (mentioned in JsonSubTypes of Event.class) was not a subclass of Event");
                }
                @SuppressWarnings({"unchecked"})
                Class<? extends Event> eventClass = (Class<? extends Event>)rawClass;
                KNOWN_EVENT_CLASSES.add(eventClass);
            }
        } else {
            throw new IllegalStateException("Event MUST have a @JsonSubTypes");
        }
    }

    private final Iterable<Class<? extends Event>> modelSubClasses;

    public ExtendedGenericEventRequest(ExtendedParameters extendedParameters, R2dbcDialect r2dbcDialect) {
        this(extendedParameters, r2dbcDialect, KNOWN_EVENT_CLASSES);
    }

    public ExtendedGenericEventRequest(ExtendedParameters extendedParameters, R2dbcDialect r2dbcDialect, Iterable<Class<? extends Event>> modelSubClasses) {
        super(extendedParameters, r2dbcDialect, Event.class);
        this.modelSubClasses = modelSubClasses;
        for (Class<?> clazz : modelSubClasses) {
            if (!KNOWN_EVENT_CLASSES.contains(clazz)) {
                throw new IllegalStateException("Unsupported class " + clazz.getSimpleName()
                        + " - it is not registered in @JsonSubTypes of Event");
            }
        }
    }

    protected DBEntityAnalysis.DBEntityAnalysisBuilder<Event> prepareDBEntityAnalysis() {
        DBEntityAnalysis.DBEntityAnalysisBuilder<Event> builder = super.prepareDBEntityAnalysis();
        TableLike eventTable = builder.getPrimaryModelTable();
        Set<String> seen = new HashSet<>();
        boolean needsTransportCall = false;


        for (Class<?> clazz : modelSubClasses) {
            Class<?> currentClass = clazz;
            if (TransportEvent.class.isAssignableFrom(currentClass) || EquipmentEvent.class.isAssignableFrom(currentClass) || OperationsEvent.class.isAssignableFrom(currentClass)) {
                needsTransportCall = true;
            } else if (!ShipmentEvent.class.isAssignableFrom(currentClass)){
                throw new IllegalStateException("Unsupported event subclass: " + currentClass.getSimpleName());
            }
            while (currentClass != Event.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    /* Keep JsonIgnore because e.g. transportCallID is JsonIgnore because we do not show it.
                     * However, we do need the transportCallID field to be known as we need it for looking up
                     * the TransportCall afterwards.
                     */
                    if (field.isSynthetic() || Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Transient.class)) {
                        continue;
                    }
                    QueryField queryField = QueryFields.queryFieldFromFieldWithSelectPrefix(field, eventTable, true, "");
                    if (seen.add(queryField.getJsonName())) {
                        builder = builder.registerQueryField(queryField);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        }

        if (needsTransportCall) {
            builder = queryParameterForTransportCall(builder);
        }
        return builder.onTable(Event.class)
          .chainJoin(EVENT_DOCUMENT_REFERENCE_TABLE)
          // This complex join is read as:
          //   (X AND Y) OR (X AND Z)
          // (because of how the underlying API works)
          .onEquals(LINK_TYPE_COLUMN, LINK_TYPE_COLUMN)
          .and().onEquals(TRANSPORT_CALL_ID_COLUMN, TRANSPORT_CALL_ID_COLUMN)
          .or()
          .onEquals(LINK_TYPE_COLUMN, LINK_TYPE_COLUMN)
          .and().onEquals(DOCUMENT_ID_COLUMN, DOCUMENT_ID_COLUMN)
          .endCondition()
          .registerQueryField(CARRIER_BOOKING_REFERENCE_COLUMN, CARRIER_BOOKING_REFERENCE_JSON_NAME, String.class)
          .registerQueryFieldAlias(CARRIER_BOOKING_REFERENCE_JSON_NAME, BOOKING_REFERENCE_JSON_NAME)
          .registerQueryField(CARRIER_BOOKING_REQUEST_REFERENCE_COLUMN, CARRIER_BOOKING_REQUEST_REFERENCE_JSON_NAME, String.class)
          .registerQueryField(TRANSPORT_DOCUMENT_REFERENCE_COLUMN, TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME, String.class)
          .registerQueryField(DOCUMENT_REFERENCE_TYPE_COLUMN, DOCUMENT_REFERENCE_TYPE_JSON_NAME, TransportDocumentType.class)
          .finishTable();
    }

    private DBEntityAnalysis.DBEntityAnalysisBuilder<Event> queryParameterForTransportCall(DBEntityAnalysis.DBEntityAnalysisBuilder<Event> builder) {
        Class<?> primaryModel = builder.getPrimaryModelClass();

        return builder
                .join(Join.JoinType.LEFT_OUTER_JOIN, primaryModel, TransportCall.class)
                // Use SQL column names here because Event.class does not have a transport call id (it is a "hack" in the view)
                .onEqualsThen(EVENT_TRANSPORT_CALL_ID_COLUMN_NAME, TRANSPORT_CALL_ID_COLUMN_NAME)
                .chainJoin(Vessel.class)
                .onFieldEqualsThen("vesselID", "id")
                .registerQueryFieldFromField("vesselIMONumber")
                // TODO: add filter for UNLocationCode

                // Go back to Transport Call
                .onTable(TransportCall.class)
                .chainJoin(Location.class, "unLocationCode")
                .onFieldEqualsThen("locationID", "id")
                .registerQueryFieldFromField("unLocationCode")
                .registerQueryFieldAlias("UNLocationCode", "unLocationCode")

                // Go back to Transport Call
                .onTable(TransportCall.class)
                .chainJoin(Voyage.class, "export_voyage")
                .onFieldEqualsThen("exportVoyageID", "id")
                .registerQueryFieldFromField("carrierVoyageNumber")
                .registerQueryFieldAlias("carrierVoyageNumber", "exportVoyageNumber")

                 // Go back to the Transport Call
                .onTable(TransportCall.class)
                .chainJoin(Voyage.class, "import_voyage")
                .onFieldEqualsThen("importVoyageID", "id")
                .registerQueryFieldFromField("carrierVoyageNumber", "importVoyage.")
                .registerQueryFieldAlias("importVoyage.carrierVoyageNumber", "importVoyageNumber")

                .chainJoin(Service.class)
                .onFieldEqualsThen("serviceID", "id")
                .registerQueryFieldFromField("carrierServiceCode")
                .finishTable();
    }

    @Override
    protected void markQueryFieldInUse(QueryField fieldInUse) {
        super.markQueryFieldInUse(fieldInUse);
        if (JSON_FIELDS_REQUIRING_DISTINCT.contains(fieldInUse.getJsonName())) {
            this.selectDistinct = true;
        }
    }

    @Override
    public boolean ignoreUnknownProperties() {
        // Always ignore unknown properties when using Event class (the properties are on the sub classes)
        return true;
    }
}
