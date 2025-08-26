package com.knots.dto;

import com.knots.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String openId; // 微信openId
    private String nickName; // 微信昵称
    private String avatarUrl; // 微信头像
    private User.UserRole role; // 用户角色
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
