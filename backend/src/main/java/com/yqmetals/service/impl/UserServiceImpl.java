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
                .eq(User::getDeleted, false)
                .orderByAsc(User::getId));
    }

    @Override
    public User register(String username, String password, String displayName, String employeeNumber,
                         String workEmail, String role, Boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setEmployeeNumber(employeeNumber);
        user.setWorkEmail(workEmail);
        user.setRole(UserRole.valueOf(role));
        user.setActive(active == null || active);
        save(user);
        return user;
    }

    @Override
    public void deletePhysically(Long id) {
        baseMapper.releaseInquiries(id);
        baseMapper.deletePhysically(id);
    }

    @Override
    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }
}
