package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination metadata for paginated API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMetadata {

    /**
     * Current page number (0-indexed)
     */
    private int currentPage;

    /**
     * Number of items per page
     */
    private int pageSize;

    /**
     * Total number of items across all pages
     */
    private long totalElements;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the first page
     */
    private boolean first;

    /**
     * Whether this is the last page
     */
    private boolean last;

    /**
     * Whether there is a next page
     */
    private boolean hasNext;

    /**
     * Whether there is a previous page
     */
    private boolean hasPrevious;

    /**
     * Create PageMetadata from Spring Data Page object
     */
    public static PageMetadata from(org.springframework.data.domain.Page<?> page) {
        return PageMetadata.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
