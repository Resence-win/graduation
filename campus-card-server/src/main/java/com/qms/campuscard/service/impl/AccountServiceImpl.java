package com.qms.campuscard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qms.campuscard.entity.Account;
import com.qms.campuscard.entity.ConsumeRecord;
import com.qms.campuscard.entity.RechargeRecord;
import com.qms.campuscard.mapper.AccountMapper;
import com.qms.campuscard.mapper.ConsumeRecordMapper;
import com.qms.campuscard.mapper.RechargeRecordMapper;
import com.qms.campuscard.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private ConsumeRecordMapper consumeRecordMapper;

    @Override
    public Account getByCardId(Long cardId) {
        return baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Account>()
                        .eq(Account::getCardId, cardId)
        );
    }

    @Override
    public BigDecimal getBalanceByCardId(Long cardId) {
        Account account = getByCardId(cardId);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    @Transactional
    @Override
    public boolean recharge(Long cardId, BigDecimal amount, String rechargeType, Long operatorId) {
        // 1. 获取账户信息
        Account account = getByCardId(cardId);
        if (account == null) {
            return false;
        }

        // 2. 更新余额
        account.setBalance(account.getBalance().add(amount));
        account.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(account);

        // 3. 记录充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setCardId(cardId);
        rechargeRecord.setAmount(amount);
        rechargeRecord.setRechargeType(rechargeType);
        rechargeRecord.setOperatorId(operatorId);
        rechargeRecord.setRechargeTime(LocalDateTime.now());
        rechargeRecordMapper.insert(rechargeRecord);

        return true;
    }

    @Transactional
    @Override
    public boolean consume(Long cardId, Long merchantId, BigDecimal amount) {
        // 1. 获取账户信息
        Account account = getByCardId(cardId);
        if (account == null) {
            return false;
        }

        // 2. 检查余额是否足够
        if (account.getBalance().compareTo(amount) < 0) {
            return false;
        }

        // 3. 扣减余额
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        account.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(account);

        // 4. 记录消费记录
        ConsumeRecord consumeRecord = new ConsumeRecord();
        consumeRecord.setCardId(cardId);
        consumeRecord.setMerchantId(merchantId);
        consumeRecord.setAmount(amount);
        consumeRecord.setBalanceAfter(newBalance);
        consumeRecord.setConsumeTime(LocalDateTime.now());
        consumeRecordMapper.insert(consumeRecord);

        return true;
    }
}