package com.qms.campuscard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("campus_card")
public class CampusCard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cardNo;

    private String ownerType;

    private Long ownerId;

    private String status;

    private LocalDateTime issueTime;
}

