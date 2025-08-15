package com.knots.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KnotsQueryForm extends PageableForm {
    private String keyword;
    private Long categoryId;
    private Boolean isPublished;
    
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    public boolean hasCategoryId() {
        return categoryId != null;
    }
    
    public boolean hasStatus() {
        return isPublished != null;
    }
}
