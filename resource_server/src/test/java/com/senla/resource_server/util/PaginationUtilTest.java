package com.senla.resource_server.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.senla.resource_server.exception.IllegalArgumentException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaginationUtilTest {

    @InjectMocks
    private PaginationUtil paginationUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paginationUtil, "defaultSize", 10);
        ReflectionTestUtils.setField(paginationUtil, "maxSize", 100);
    }

    @Test
    void calculate_WhenPageAndSizeNull_ShouldUseDefaults() {
        PaginationUtil.Pagination result = paginationUtil.calculate(null, null);

        assertEquals(10, result.limit());
        assertEquals(0, result.offset());
    }

    @Test
    void calculate_WhenPageNull_ShouldUseOffsetZero() {
        PaginationUtil.Pagination result = paginationUtil.calculate(null, 20);

        assertEquals(20, result.limit());
        assertEquals(0, result.offset());
    }

    @Test
    void calculate_WhenSizeNull_ShouldUseDefaultSize() {
        PaginationUtil.Pagination result = paginationUtil.calculate(2, null);

        assertEquals(10, result.limit());
        assertEquals(10, result.offset());
    }

    @Test
    void calculate_WhenValidPageAndSize_ShouldCalculateCorrectly() {
        PaginationUtil.Pagination result = paginationUtil.calculate(3, 15);

        assertEquals(15, result.limit());
        assertEquals(30, result.offset());
    }

    @Test
    void calculate_WhenSizeExceedsMax_ShouldUseMaxSize() {
        PaginationUtil.Pagination result = paginationUtil.calculate(1, 150);

        assertEquals(100, result.limit());
        assertEquals(0, result.offset());
    }

    @Test
    void calculate_WhenSizeZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            paginationUtil.calculate(1, 0);
        });
    }

    @Test
    void calculate_WhenSizeNegative_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            paginationUtil.calculate(1, -5);
        });
    }

    @Test
    void calculate_WhenPageZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            paginationUtil.calculate(0, 10);
        });
    }

    @Test
    void calculate_WhenPageNegative_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            paginationUtil.calculate(-2, 10);
        });
    }

    @Test
    void calculate_WhenLargePageNumber_ShouldHandleLongOffset() {
        PaginationUtil.Pagination result = paginationUtil.calculate(100000, 50);

        assertEquals(50, result.limit());
        assertEquals(4999950L, result.offset());
    }

    @Test
    void paginationRecord_ShouldHaveCorrectValues() {
        PaginationUtil.Pagination pagination = new PaginationUtil.Pagination(10, 20);

        assertEquals(10, pagination.limit());
        assertEquals(20, pagination.offset());
    }
}