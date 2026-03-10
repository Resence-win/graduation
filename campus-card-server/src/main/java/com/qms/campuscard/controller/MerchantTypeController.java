package com.qms.campuscard.controller;

import com.qms.campuscard.entity.MerchantType;
import com.qms.campuscard.service.MerchantTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/type")
public class MerchantTypeController {

    private final MerchantTypeService merchantTypeService;

    public MerchantTypeController(MerchantTypeService merchantTypeService) {
        this.merchantTypeService = merchantTypeService;
    }

    // 商户类型列表
    @GetMapping("/list")
    public Map<String, Object> list() {
        List<MerchantType> list = merchantTypeService.listAll();

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", list);
        return body;
    }

    public record MerchantTypeCreateRequest(
            @NotBlank(message = "类型名称不能为空") String typeName,
            String description
    ) {}

    // 新增商户类型
    @PostMapping
    public Map<String, Object> create(@RequestBody @Valid MerchantTypeCreateRequest req) {
        MerchantType mt = new MerchantType();
        mt.setTypeName(req.typeName());
        mt.setDescription(req.description());

        MerchantType created = merchantTypeService.create(mt);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", created);
        return body;
    }
}

