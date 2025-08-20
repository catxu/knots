package com.knots.dto;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

    public PageResponse(PageInfo<T> page) {
        this.content = page.getList();
        this.pageNumber = page.getPageNum();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotal();
        this.totalPages = page.getPages();
        this.hasNext = page.isHasNextPage();
        this.hasPrevious = page.isHasPreviousPage();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }

    public static <T> PageResponse<T> of(PageInfo<T> page) {
        return new PageResponse<>(page);
    }

    public static <T, R> PageResponse<R> of(Page<T> page, Function<T, R> converter) {
        PageResponse<R> response = (PageResponse<R>) PageResponse.of(page);
        response.setContent(page.getContent().stream().map(converter).collect(Collectors.toList()));
        return response;
    }
}
