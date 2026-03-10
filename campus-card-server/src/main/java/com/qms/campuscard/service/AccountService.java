package com.qms.campuscard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qms.campuscard.entity.Account;

import java.math.BigDecimal;

public interface AccountService extends IService<Account> {

    /**
     * 根据校园卡ID获取账户信息
     */
    Account getByCardId(Long cardId);

    /**
     * 根据校园卡ID获取余额
     */
    BigDecimal getBalanceByCardId(Long cardId);

    /**
     * 充值操作
     */
    boolean recharge(Long cardId, BigDecimal amount, String rechargeType, Long operatorId);

    /**
     * 消费操作
     */
    boolean consume(Long cardId, Long merchantId, BigDecimal amount);
}