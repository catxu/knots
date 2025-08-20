package com.knots.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KnotsQueryForm extends PageableForm {
    private String keyword;
    private Long knotId;
    private Long categoryId;
    private Boolean isPublished;
}
