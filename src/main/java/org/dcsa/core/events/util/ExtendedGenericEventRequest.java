package org.dcsa.core.events.util;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.SneakyThrows;
import org.dcsa.core.events.model.*;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.extendedrequest.QueryField;
import org.dcsa.core.extendedrequest.QueryFields;
import org.dcsa.core.query.DBEntityAnalysis;
import org.dcsa.core.util.ReflectUtility;
import org.springframework.data.annotation.Transient;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.sql.Join;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.relational.core.sql.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private static final String TRANSPORT_DOCUMENT_TYPE_CODE_JSON_NAME = "transportDocumentTypeCode";
    private static final String VESSEL_IMO_NUMBER_JSON_NAME = "vesselIMONumber";

    private static final String SHIPMENT_TABLE_NAME = "shipment";
    private static final String SHIPMENT_TABLE_ID_COLUMN_NAME = "id";
    private static final String SHIPMENT_CARRIER_BOOKING_REFERENCE_JSON_NAME = "carrierBookingReference";
    private static final String SHIPMENT_BOOKING_REFERENCE_JSON_NAME = "bookingReference";

    private static final String SHIPPING_INSTRUCTION_TABLE_NAME = "shipping_instruction";
    private static final String SHIPPING_INSTRUCTION_TRANSPORT_DOCUMENT_TYPE_COLUMN_NAME = "transport_document_type";
    private static final String SHIPPING_INSTRUCTION_ID_COLUMN_NAME = "id";

    private static final String TRANSPORT_DOCUMENT_TABLE_NAME = "transport_document";
    private static final String TRANSPORT_DOCUMENT_TABLE_SHIPPING_INSTRUCTION_ID_COLUMN_NAME = "shipping_instruction_id";
    private static final String TRANSPORT_DOCUMENT_TABLE_TRANSPORT_DOCUMENT_REFERENCE_COLUMN_NAME = "transport_document_reference";

    private static final Set<String> JSON_FIELDS_REQUIRING_DISTINCT = Set.of(
            TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME,
            TRANSPORT_DOCUMENT_ID_JSON_NAME,
            TRANSPORT_DOCUMENT_TYPE_CODE_JSON_NAME,
            VESSEL_IMO_NUMBER_JSON_NAME
    );

    private static final String EVENT_TYPE_FIELD_NAME;
    private static final Map<String, Constructor<? extends Event>> NAME2CONSTRUCTOR;
    private static final Set<Class<? extends Event>> KNOWN_EVENT_CLASSES;

    static {
        JsonSubTypes jsonSubTypes = Event.class.getAnnotation(JsonSubTypes.class);
        JsonTypeInfo jsonTypeInfo = Event.class.getAnnotation(JsonTypeInfo.class);
        if (jsonSubTypes != null && jsonTypeInfo != null) {
            String property = jsonTypeInfo.property();
            // The discriminator value is on the Event class
            try {
                EVENT_TYPE_FIELD_NAME = ReflectUtility.transformFromFieldNameToColumnName(Event.class, property);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("Event MUST have the field " + property + " (listed in @JsonTypeInfo)");
            }
            NAME2CONSTRUCTOR = new HashMap<>();
            KNOWN_EVENT_CLASSES = new HashSet<>();
            for (JsonSubTypes.Type type : jsonSubTypes.value()) {
                String value = type.name();
                Class<?> rawClass = type.value();
                if (!Event.class.isAssignableFrom(rawClass)) {
                    throw new IllegalStateException(rawClass.getSimpleName()
                            + " (mentioned in JsonSubTypes of Event.class) was not a subclass of Event");
                }
                @SuppressWarnings({"unchecked"})
                Class<? extends Event> eventClass = (Class<? extends Event>)rawClass;
                KNOWN_EVENT_CLASSES.add(eventClass);
                try {
                    Constructor<? extends Event> constructor = eventClass.getDeclaredConstructor();
                    if (!Modifier.isPublic(constructor.getModifiers())) {
                        throw new IllegalStateException("The no-argument constructor for " + eventClass.getSimpleName()
                                + " is not public but it must be.  The class is listed as a subclass in"
                                + " @JsonSubTypes on the Event class.");
                    }
                    NAME2CONSTRUCTOR.put(value, constructor);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("The event subclass " + eventClass.getSimpleName()
                            + " MUST have a no-argument constructor.  The class is listed as a subclass in"
                            + " @JsonSubTypes on the Event class.");
                }

            }
        } else {
            throw new IllegalStateException("Event MUST have a @JsonSubTypes and @JsonTypeInfo");
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

    @SneakyThrows({NoSuchFieldException.class})
    protected DBEntityAnalysis.DBEntityAnalysisBuilder<Event> prepareDBEntityAnalysis() {
        DBEntityAnalysis.DBEntityAnalysisBuilder<Event> builder = super.prepareDBEntityAnalysis();
        Table eventTable = builder.getPrimaryModelTable();
        Set<String> seen = new HashSet<>();
        boolean includesShipmentEvents = false;
        boolean needsTransportCall = false;


        for (Class<?> clazz : modelSubClasses) {
            Class<?> currentClass = clazz;
            if (ShipmentEvent.class.isAssignableFrom(currentClass)) {
                includesShipmentEvents = true;
            } else if (TransportEvent.class.isAssignableFrom(currentClass) || EquipmentEvent.class.isAssignableFrom(currentClass) || OperationsEvent.class.isAssignableFrom(currentClass)) {
                needsTransportCall = true;
            } else {
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
                    QueryField queryField = QueryFields.queryFieldFromField(Event.class, field, clazz, eventTable, true);
                    if (seen.add(queryField.getJsonName())) {
                        builder = builder.registerQueryField(queryField);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        }

        builder.registerQueryFieldAlias(SHIPMENT_CARRIER_BOOKING_REFERENCE_JSON_NAME, SHIPMENT_BOOKING_REFERENCE_JSON_NAME);

        if (includesShipmentEvents) {
            builder = queryParametersForShipmentEvents(builder, eventTable);
        }
        if (needsTransportCall) {
            builder = queryParameterForTransportCall(builder);
        }
        return builder;
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

                /* TODO: Support import voyage number
                // Go back to Transport Call to add import voyage
                .onTable(TransportCall.class)
                .chainJoin(Voyage.class)
                .onFieldEqualsThen("importVoyageID", "id")
                .registerQueryFieldFromField("carrierVoyageNumber")
                 */

                // Go back to Transport Call
                .onTable(TransportCall.class)
                .chainJoin(Voyage.class)
                .onFieldEqualsThen("exportVoyageID", "id")
                .registerQueryFieldFromFieldThen("carrierVoyageNumber")


                .chainJoin(Service.class)
                .onEqualsThen("serviceID", "id")
                .registerQueryFieldFromField("carrierServiceCode");
    }

    private DBEntityAnalysis.DBEntityAnalysisBuilder<Event> queryParametersForShipmentEvents(DBEntityAnalysis.DBEntityAnalysisBuilder<Event> builder, Table eventTable) throws NoSuchFieldException {
        // FIXME - this is incorrect (should join with a table based on the value of documentTypeCode)
        String shipmentEventdocumentIdColumn = ReflectUtility.transformFromFieldNameToColumnName(ShipmentEvent.class, "documentID");
        Table shippingInstructionsTable = Table.create(SHIPPING_INSTRUCTION_TABLE_NAME);
        Table transportDocumentTable = Table.create(TRANSPORT_DOCUMENT_TABLE_NAME);
        return builder
                .join(Join.JoinType.JOIN, eventTable, shippingInstructionsTable)
                .onEqualsThen(shipmentEventdocumentIdColumn, SHIPPING_INSTRUCTION_ID_COLUMN_NAME)
                .registerQueryFieldThen(
                        SqlIdentifier.unquoted(SHIPPING_INSTRUCTION_TRANSPORT_DOCUMENT_TYPE_COLUMN_NAME),
                        TRANSPORT_DOCUMENT_TYPE_CODE_JSON_NAME,
                        String.class
                )
                .chainJoin(transportDocumentTable)
                .onEqualsThen(SHIPPING_INSTRUCTION_ID_COLUMN_NAME, TRANSPORT_DOCUMENT_TABLE_SHIPPING_INSTRUCTION_ID_COLUMN_NAME)
                .registerQueryFieldThen(
                        SqlIdentifier.unquoted(TRANSPORT_DOCUMENT_TABLE_TRANSPORT_DOCUMENT_REFERENCE_COLUMN_NAME),
                        TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME,
                        String.class
                ).registerQueryFieldAlias(TRANSPORT_DOCUMENT_REFERENCE_JSON_NAME, TRANSPORT_DOCUMENT_ID_JSON_NAME);
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

    /**
     * A method to look at the database row and via reflection determine the type of SubEvent to create. It will look
     * at the Event class and extract the discriminator value from the row and create a new instance of the SubClass
     * @param row the Database row containing the object to create
     * @param meta the Database metadata about the row
     * @return a Subclassed event (TransportEvent, EquipmentEvent or ShipmentEvent) corresponding to the discriminator
     */
    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    @Override
    public Event getModelClassInstance(Row row, RowMetadata meta) {
        Object value = row.get(EVENT_TYPE_FIELD_NAME);
        Constructor<? extends Event> constructor;
        if (value instanceof String) {
            constructor = NAME2CONSTRUCTOR.get(value);
        } else {
            constructor = null;
        }
        if (constructor == null) {
            throw new IllegalStateException("Unknown Event type (field: " + EVENT_TYPE_FIELD_NAME + "), got value: "
                    + value);
        }
        return constructor.newInstance();
    }
}
