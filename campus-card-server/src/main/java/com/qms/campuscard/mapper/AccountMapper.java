package com.qms.campuscard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qms.campuscard.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}

