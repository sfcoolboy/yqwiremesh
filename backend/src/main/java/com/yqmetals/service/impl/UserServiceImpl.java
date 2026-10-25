package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.common.enums.UserRole;
import com.yqmetals.entity.User;
import com.yqmetals.mapper.UserMapper;
import com.yqmetals.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return getOneOpt(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public List<User> findAllActive() {
        return list(new LambdaQueryWrapper<User>()
                .eq(User::getActive, true)
                .eq(User::getDeleted, false));
    }

    @Override
    public User register(String username, String password, String displayName, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRole(UserRole.valueOf(role));
        user.setActive(true);
        save(user);
        return user;
    }
}
