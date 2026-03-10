package com.qms.campuscard.controller;

import com.qms.campuscard.entity.CampusCard;
import com.qms.campuscard.service.CampusCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/card")
public class CampusCardController {

    private final CampusCardService campusCardService;

    public CampusCardController(CampusCardService campusCardService) {
        this.campusCardService = campusCardService;
    }

    public record OpenCardRequest(
            @NotBlank(message = "学号/工号不能为空") String userNo,
            @NotBlank(message = "用户类型不能为空") String userType
    ) {}

    // 开卡
    @PostMapping("/open")
    public Map<String, Object> open(@RequestBody @Valid OpenCardRequest req) {
        CampusCard card = campusCardService.openCard(req.userNo(), req.userType());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", card);
        return body;
    }

    // 卡信息
    @GetMapping("/{cardId}")
    public Map<String, Object> info(@PathVariable Long cardId) {
        CampusCard card = campusCardService.getById(cardId);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", card);
        return body;
    }

    public record CardIdRequest(@NotNull(message = "卡ID不能为空") Long cardId) {}

    // 挂失
    @PostMapping("/loss")
    public Map<String, Object> loss(@RequestBody @Valid CardIdRequest req) {
        campusCardService.loss(req.cardId());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }

    // 解挂
    @PostMapping("/unloss")
    public Map<String, Object> unloss(@RequestBody @Valid CardIdRequest req) {
        campusCardService.unloss(req.cardId());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }

    // 注销
    @PostMapping("/cancel")
    public Map<String, Object> cancel(@RequestBody @Valid CardIdRequest req) {
        campusCardService.cancel(req.cardId());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }
}

