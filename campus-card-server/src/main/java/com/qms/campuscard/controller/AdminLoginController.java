package com.qms.campuscard.controller;

import com.qms.campuscard.entity.AdminUser;
import com.qms.campuscard.service.AdminUserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminLoginController {

    private final AdminUserService adminUserService;

    public AdminLoginController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空") String username,
            @NotBlank(message = "密码不能为空") String password
    ) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Validated LoginRequest request) {
        AdminUser admin = adminUserService.findByUsername(request.username());
        Map<String, Object> body = new HashMap<>();

        if (admin == null || !admin.getPassword().equals(request.password())) {
            body.put("code", 4001);
            body.put("msg", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("name", admin.getName());
        data.put("role", admin.getRole());
        // 这里先返回一个简单的占位 token，后续可以接入 JWT
        data.put("token", "mock-token");

        body.put("code", 0);
        body.put("msg", "登录成功");
        body.put("data", data);

        return ResponseEntity.ok(body);
    }
}

