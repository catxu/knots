package com.knots.web.form;

import com.oak.root.web.form.BasePageableForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnotQueryForm extends BasePageableForm {
    private String keyword;
    private Long knotId;
    private Long categoryId;
    private Boolean isPublished;
}
