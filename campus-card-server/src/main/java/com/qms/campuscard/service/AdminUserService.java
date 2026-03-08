package com.qms.campuscard.service;

import com.qms.campuscard.entity.AdminUser;

public interface AdminUserService {

    /**
     * 根据用户名查询管理员
     */
    AdminUser findByUsername(String username);
}

