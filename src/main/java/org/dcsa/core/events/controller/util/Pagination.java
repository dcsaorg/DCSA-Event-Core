package org.dcsa.core.events.controller.util;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.exception.CreateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
public class Pagination {

	private final Sort defaultSort;

	public PageRequest createPageRequest(int limit, String cursor, String[] sortParam) {
		Sort sort = parseSort(sortParam);
		PageRequest pageRequest;
		if(cursor == null) {
			pageRequest = PageRequest.of(0, limit, sort);
		} else {
			pageRequest = parseCursor(cursor);
		}
		return pageRequest;
	}

	public MultiValueMap<String, String> setPaginationHeaders(Page page) {
		MultiValueMap<String, String> paginationHeaders = new LinkedMultiValueMap<>();

		paginationHeaders.add("Current-Page", formatCursor(page.getNumber(), page.getSize(), page.getSort()));
		if(page.hasNext()){
			paginationHeaders.add("Next-Page", formatCursor(page.getNumber()+1, page.getSize(), page.getSort()));
			paginationHeaders.add("Last-Page", formatCursor(page.getTotalPages()-1, page.getSize(), page.getSort()));
		}

		return paginationHeaders;
	}

	//ToDo add the encryption based obfuscation
	protected String formatCursor(int page, int size, Sort sort) {
		StringBuilder sb = new StringBuilder();
		sb.append("page=").append(page);
		sb.append("&size=").append(size);
		sb.append("&sort=").append(sort);
		return Base64.getUrlEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
	}

	protected PageRequest parseCursor(String cursor) {

		try {
			String decodedCursor = new String(Base64.getUrlDecoder().decode(cursor));
			String[] decodedCursurItems = decodedCursor.split("&");

			int page = Integer.parseInt(decodedCursurItems[0].split("=")[1]);
			int size = Integer.parseInt(decodedCursurItems[1].split("=")[1]);
			String[] sortParam = decodedCursurItems[2].split("=")[1].split(",");
			return PageRequest.of(page, size, parseSort(sortParam));

		} catch (Exception exception) {
			throw new CreateException("Malformed cursor");
		}

	}

	public Sort parseSort(String[] sort) {

		if(sort == null || sort.length == 0) {
			return defaultSort;
		}

		List<Sort.Order> orderList = new ArrayList<>();
		for (String sortItem : sort) {
			String[] sortComponents = sortItem.split(":");
			if(sortComponents.length <2) {
				orderList.add(new Sort.Order(Sort.Direction.DESC, sortComponents[0]));
			} else {
				orderList.add(new Sort.Order(replaceOrderStringThroughDirection(sortComponents[1]), sortComponents[0]));
			}

		}
		return Sort.by(orderList);
	}

	private static Sort.Direction replaceOrderStringThroughDirection(String sortDirection) {
		if (sortDirection.trim().equalsIgnoreCase("DESC")) {
			return Sort.Direction.DESC;
		} else {
			return Sort.Direction.ASC;
		}
	}
}
