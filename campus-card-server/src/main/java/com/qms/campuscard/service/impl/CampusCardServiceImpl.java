package com.qms.campuscard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qms.campuscard.entity.Account;
import com.qms.campuscard.entity.CampusCard;
import com.qms.campuscard.entity.LossRecord;
import com.qms.campuscard.entity.Student;
import com.qms.campuscard.entity.Teacher;
import com.qms.campuscard.mapper.AccountMapper;
import com.qms.campuscard.mapper.CampusCardMapper;
import com.qms.campuscard.mapper.LossRecordMapper;
import com.qms.campuscard.mapper.StudentMapper;
import com.qms.campuscard.mapper.TeacherMapper;
import com.qms.campuscard.service.CampusCardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CampusCardServiceImpl implements CampusCardService {

    private final CampusCardMapper campusCardMapper;
    private final AccountMapper accountMapper;
    private final LossRecordMapper lossRecordMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    public CampusCardServiceImpl(CampusCardMapper campusCardMapper,
                                 AccountMapper accountMapper,
                                 LossRecordMapper lossRecordMapper,
                                 StudentMapper studentMapper,
                                 TeacherMapper teacherMapper) {
        this.campusCardMapper = campusCardMapper;
        this.accountMapper = accountMapper;
        this.lossRecordMapper = lossRecordMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public CampusCard openCard(String userNo, String userType) {
        Long ownerId;
        if ("student".equalsIgnoreCase(userType)) {
            Student stu = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, userNo));
            if (stu == null) {
                throw new IllegalArgumentException("学号不存在: " + userNo);
            }
            ownerId = stu.getId();
        } else if ("teacher".equalsIgnoreCase(userType)) {
            Teacher t = teacherMapper.selectOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getTeacherNo, userNo));
            if (t == null) {
                throw new IllegalArgumentException("工号不存在: " + userNo);
            }
            ownerId = t.getId();
        } else {
            throw new IllegalArgumentException("不支持的用户类型: " + userType);
        }

        // 生成简单卡号：前缀 + 时间戳 + 学/工号
        String cardNo = "CC" + System.currentTimeMillis() + userNo;

        CampusCard card = new CampusCard();
        card.setCardNo(cardNo);
        card.setOwnerType(userType);
        card.setOwnerId(ownerId);
        card.setStatus("normal");
        card.setIssueTime(LocalDateTime.now());
        campusCardMapper.insert(card);

        Account account = new Account();
        account.setCardId(card.getId());
        account.setBalance(BigDecimal.ZERO);
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.insert(account);

        return card;
    }

    @Override
    public CampusCard getById(Long cardId) {
        return campusCardMapper.selectById(cardId);
    }

    @Override
    public void loss(Long cardId) {
        CampusCard card = campusCardMapper.selectById(cardId);
        if (card == null) {
            return;
        }
        card.setStatus("lost");
        campusCardMapper.updateById(card);

        LossRecord record = new LossRecord();
        record.setCardId(cardId);
        record.setLossTime(LocalDateTime.now());
        record.setStatus("lost");
        lossRecordMapper.insert(record);
    }

    @Override
    public void unloss(Long cardId) {
        CampusCard card = campusCardMapper.selectById(cardId);
        if (card == null) {
            return;
        }
        card.setStatus("normal");
        campusCardMapper.updateById(card);

        // 更新最近一条挂失记录为已恢复
        LossRecord last = lossRecordMapper.selectOne(
                new LambdaQueryWrapper<LossRecord>()
                        .eq(LossRecord::getCardId, cardId)
                        .orderByDesc(LossRecord::getId)
                        .last("limit 1")
        );
        if (last != null && "lost".equals(last.getStatus())) {
            last.setStatus("recovered");
            last.setRecoverTime(LocalDateTime.now());
            lossRecordMapper.updateById(last);
        }
    }

    @Override
    public void cancel(Long cardId) {
        CampusCard card = campusCardMapper.selectById(cardId);
        if (card == null) {
            return;
        }
        card.setStatus("disabled");
        campusCardMapper.updateById(card);
    }
}

