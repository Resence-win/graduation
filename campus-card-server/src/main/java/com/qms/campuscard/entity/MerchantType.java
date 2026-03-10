package com.qms.campuscard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("merchant_type")
public class MerchantType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String typeName;

    private String description;
}

