package com.qms.campuscard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Merchant;
import com.qms.campuscard.service.MerchantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    @Value("${app.upload.merchant-dir:uploads/merchant}")
    private String uploadDir;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // 商户列表
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") long page,
                                    @RequestParam(defaultValue = "10") long size,
                                    @RequestParam(required = false) String merchantName,
                                    @RequestParam(required = false) Long typeId) {
        Page<Merchant> result = merchantService.pageList(page, size, merchantName, typeId);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", data);
        return body;
    }

    public record MerchantCreateRequest(
            @NotBlank(message = "商户名称不能为空") String merchantName,
            @NotNull(message = "商户类型不能为空") Long typeId,
            String location,
            String logo
    ) {}

    public record MerchantUpdateRequest(
            @NotNull(message = "ID不能为空") Long id,
            @NotBlank(message = "商户名称不能为空") String merchantName,
            @NotNull(message = "商户类型不能为空") Long typeId,
            String location,
            String logo,
            String status
    ) {}

    // 新增商户
    @PostMapping
    public Map<String, Object> create(@RequestBody @Valid MerchantCreateRequest req) {
        Merchant m = new Merchant();
        m.setName(req.merchantName());
        m.setTypeId(req.typeId());
        m.setLocation(req.location());
        m.setLogo(req.logo());

        Merchant created = merchantService.create(m);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", created);
        return body;
    }

    // 商户详情（方便前端编辑时回显）
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", merchant);
        return body;
    }

    // 修改商户
    @PutMapping
    public Map<String, Object> update(@RequestBody @Valid MerchantUpdateRequest req) {
        Merchant m = new Merchant();
        m.setId(req.id());
        m.setName(req.merchantName());
        m.setTypeId(req.typeId());
        m.setLocation(req.location());
        m.setLogo(req.logo());
        m.setStatus(req.status());

        Merchant updated = merchantService.update(m);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", updated);
        return body;
    }

    // 删除商户
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        merchantService.deleteById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }

    // 批量删除商户（既支持单个也支持多个） 
    @DeleteMapping
    public Map<String, Object> deleteBatch(@RequestBody Map<String, List<Long>> bodyReq) {
        List<Long> ids = bodyReq.get("ids");
        merchantService.deleteBatch(ids);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }

    // 上传商户 Logo
    @PostMapping("/upload-logo")
    public Map<String, Object> uploadLogo(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 计算保存路径
        String rootDir = System.getProperty("user.dir");
        File dir = new File(rootDir, uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建上传目录: " + dir.getAbsolutePath());
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = System.currentTimeMillis() + ext;
        File dest = new File(dir, filename);
        file.transferTo(dest);

        // 返回给前端的访问路径，符合接口文档示例
        String url = "/upload/merchant/" + filename;

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "上传成功");
        body.put("data", url);
        return body;
    }
}

