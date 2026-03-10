package com.qms.campuscard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consume_record")
public class ConsumeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cardId;

    private Long merchantId;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private LocalDateTime consumeTime;
}