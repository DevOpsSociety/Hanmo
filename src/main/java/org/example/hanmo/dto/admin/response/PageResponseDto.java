package org.example.hanmo.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private boolean first;
    private boolean last;

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }
}
