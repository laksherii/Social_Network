package com.senla.resource_server.util;

import com.senla.resource_server.exception.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {

    @Value("${app.pagination.default.size}")
    private int defaultSize;

    @Value("${app.pagination.max.size}")
    private int maxSize;

    public Pagination calculate(Integer page, Integer size) {
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        if (size != null && size > maxSize) {
            size = maxSize;
        }
        int limit = (size == null) ? defaultSize : size;

        if (page != null && page <= 0) {
            throw new IllegalArgumentException("Page number must be positive");
        }
        long offset = (page == null) ? 0 : (long) (page - 1) * limit;

        return new Pagination(limit, offset);
    }

    public record Pagination(int limit, long offset) {}
}
