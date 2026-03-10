package com.qms.campuscard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Merchant;

public interface MerchantService {

    Page<Merchant> pageList(long page, long size, String merchantName, Long typeId);

    Merchant create(Merchant merchant);

    Merchant getById(Long id);

    Merchant update(Merchant merchant);

    void deleteById(Long id);

    void deleteBatch(java.util.List<Long> ids);
}

