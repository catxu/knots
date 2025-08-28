package com.knots.web.form;

import lombok.Data;

@Data
public class CategoryCreateForm {
    private String name;
    private String description;
    private int sortOrder;
}
