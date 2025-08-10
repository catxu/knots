package com.knots.service;

import com.knots.entity.User;
import com.knots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public User createOrUpdateWechatUser(String openId, String nickName, String avatarUrl) {
        Optional<User> existingUser = userRepository.findByOpenId(openId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setNickName(nickName);
            user.setAvatarUrl(avatarUrl);
            return userRepository.save(user);
        } else {
            User newUser = new User();
            newUser.setOpenId(openId);
            newUser.setNickName(nickName);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setUsername("wx_" + openId.substring(0, 8));
            newUser.setPassword(passwordEncoder.encode("wechat_user"));
            return userRepository.save(newUser);
        }
    }
}
