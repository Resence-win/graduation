package com.qms.campuscard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loss_record")
public class LossRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cardId;

    private LocalDateTime lossTime;

    private LocalDateTime recoverTime;

    private String status;
}

