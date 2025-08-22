package com.knots.web.form;

import com.oak.root.web.form.BaseForm;

public class LoginForm extends BaseForm {
    // 登录请求DTO
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
