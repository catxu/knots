package com.knots.web.form;

import com.oak.root.web.form.BasePageableForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryForm extends BasePageableForm {
    private String keyword;
}
