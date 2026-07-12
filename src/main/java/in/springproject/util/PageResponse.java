package in.springproject.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper for paginated API responses containing data and pagination metadata.
 * Use {@link #from(Page)} to convert any Spring Data {@link Page} into this DTO.
 *
 * @param <T> the type of the content items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** The list of items in the current page. */
    private List<T> content;

    /** Zero-based page index. */
    private int pageNumber;

    /** Number of items per page. */
    private int pageSize;

    /** Total number of items across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** Whether this is the last page. */
    private boolean last;

    /** Whether this is the first page. */
    private boolean first;

    /**
     * Converts a Spring Data {@link Page} into a {@link PageResponse}.
     *
     * @param page the Spring Data page to convert
     * @param <T>  the type of content items
     * @return a fully populated PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
