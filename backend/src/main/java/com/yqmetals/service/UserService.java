package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService extends IService<User> {
    Optional<User> findByUsername(String username);
    List<User> findAllActive();
    User register(String username, String password, String displayName, String employeeNumber,
                  String workEmail, String role, Boolean active);
    void deletePhysically(Long id);
    void updateUser(User user);
    void resetPassword(Long id, String newPassword);
}
