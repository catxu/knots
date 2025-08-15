package com.knots.service;

import com.knots.entity.User;
import com.knots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("用户 " + username + " 未找到");
        }
        return new org.springframework.security.core.userdetails.User(
                userOpt.get().getUsername(),
                userOpt.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userOpt.get().getRole().name()))
        );
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Page<User> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
