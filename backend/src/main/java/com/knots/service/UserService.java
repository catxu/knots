package com.knots.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knots.entity.User;
import com.knots.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ServiceImpl<UserMapper, User> implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username), false));
    }

    public Optional<User> findByOpenId(String openId) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId), false));
    }

    public User saveUser(User user) {
        if (user.getId() == null) {
            save(user);
        } else {
            updateById(user);
        }
        return user;
    }

    public boolean existsByUsername(String username) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0;
    }

    public User createOrUpdateWechatUser(String openId, String nickName, String avatarUrl) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId), false);
        if (user != null) {
            user.setNickName(nickName);
            user.setAvatarUrl(avatarUrl);
            updateById(user);
            return user;
        } else {
            User newUser = new User();
            newUser.setOpenId(openId);
            newUser.setNickName(nickName);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setUsername("wx_" + openId.substring(0, 8));
            newUser.setPassword(passwordEncoder.encode("wechat_user"));
            save(newUser);
            return newUser;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username), false);
        if (user == null) {
            throw new UsernameNotFoundException("用户 " + username + " 未找到");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
    
    public List<User> getAllUsers() {
        return list();
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }
    
    public User updateUser(User user) {
        updateById(user);
        return user;
    }
    
    public void deleteUser(Long id) {
        removeById(id);
    }
    
    public Page<User> getUsersPage(Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1L, pageable.getPageSize());
        IPage<User> result = page(mpPage, new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
}
