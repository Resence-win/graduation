package com.qms.campuscard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Account;
import com.qms.campuscard.entity.ConsumeRecord;
import com.qms.campuscard.entity.RechargeRecord;
import com.qms.campuscard.mapper.ConsumeRecordMapper;
import com.qms.campuscard.mapper.RechargeRecordMapper;
import com.qms.campuscard.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private ConsumeRecordMapper consumeRecordMapper;

    /**
     * 获取账户信息
     */
    @GetMapping("/account/{cardId}")
    public Map<String, Object> getAccount(@PathVariable Long cardId) {
        Map<String, Object> result = new HashMap<>();
        Account account = accountService.getByCardId(cardId);
        if (account != null) {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", account);
        } else {
            result.put("code", 1);
            result.put("msg", "账户不存在");
        }
        return result;
    }

    /**
     * 查询余额
     */
    @GetMapping("/account/balance/{cardId}")
    public Map<String, Object> getBalance(@PathVariable Long cardId) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal balance = accountService.getBalanceByCardId(cardId);
        result.put("code", 0);
        result.put("msg", "success");
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("balance", balance);
        result.put("data", data);
        return result;
    }

    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Map<String, Object> recharge(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查参数是否存在
            if (!request.containsKey("cardId")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少cardId");
                return result;
            }
            if (!request.containsKey("amount")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少amount");
                return result;
            }
            if (!request.containsKey("rechargeType")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少rechargeType");
                return result;
            }

            // 解析参数
            Long cardId = Long.parseLong(request.get("cardId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String rechargeType = request.get("rechargeType").toString();
            Long operatorId = request.get("operatorId") != null ? Long.parseLong(request.get("operatorId").toString()) : null;

            // 检查参数有效性
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                result.put("code", 1);
                result.put("msg", "参数错误：充值金额必须大于0");
                return result;
            }

            boolean success = accountService.recharge(cardId, amount, rechargeType, operatorId);
            if (success) {
                result.put("code", 0);
                result.put("msg", "充值成功");
            } else {
                result.put("code", 1);
                result.put("msg", "充值失败，账户不存在");
            }
        } catch (NumberFormatException e) {
            result.put("code", 1);
            result.put("msg", "参数错误：数字格式不正确");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "参数错误：" + e.getMessage());
        }
        return result;
    }

    /**
     * 充值记录
     */
    @GetMapping("/recharge/list")
    public Map<String, Object> getRechargeList(
            @RequestParam(required = false) Long cardId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        Page<RechargeRecord> pageInfo = new Page<>(page, size);
        IPage<RechargeRecord> records = rechargeRecordMapper.selectPage(
                pageInfo,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RechargeRecord>()
                        .eq(cardId != null, RechargeRecord::getCardId, cardId)
                        .orderByDesc(RechargeRecord::getRechargeTime)
        );
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", records);
        return result;
    }

    /**
     * 消费
     */
    @PostMapping("/consume")
    public Map<String, Object> consume(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查参数是否存在
            if (!request.containsKey("cardId")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少cardId");
                return result;
            }
            if (!request.containsKey("merchantId")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少merchantId");
                return result;
            }
            if (!request.containsKey("amount")) {
                result.put("code", 1);
                result.put("msg", "参数错误：缺少amount");
                return result;
            }

            // 解析参数
            Long cardId = Long.parseLong(request.get("cardId").toString());
            Long merchantId = Long.parseLong(request.get("merchantId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            // 检查参数有效性
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                result.put("code", 1);
                result.put("msg", "参数错误：消费金额必须大于0");
                return result;
            }

            boolean success = accountService.consume(cardId, merchantId, amount);
            if (success) {
                result.put("code", 0);
                result.put("msg", "消费成功");
            } else {
                result.put("code", 1);
                result.put("msg", "消费失败，余额不足或账户不存在");
            }
        } catch (NumberFormatException e) {
            result.put("code", 1);
            result.put("msg", "参数错误：数字格式不正确");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "参数错误：" + e.getMessage());
        }
        return result;
    }

    /**
     * 消费记录
     */
    @GetMapping("/consume/list")
    public Map<String, Object> getConsumeList(
            @RequestParam(required = false) Long cardId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        Page<ConsumeRecord> pageInfo = new Page<>(page, size);
        IPage<ConsumeRecord> records = consumeRecordMapper.selectPage(
                pageInfo,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ConsumeRecord>()
                        .eq(cardId != null, ConsumeRecord::getCardId, cardId)
                        .eq(merchantId != null, ConsumeRecord::getMerchantId, merchantId)
                        .orderByDesc(ConsumeRecord::getConsumeTime)
        );
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", records);
        return result;
    }
}