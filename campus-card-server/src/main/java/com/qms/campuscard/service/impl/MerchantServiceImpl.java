package com.qms.campuscard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Merchant;
import com.qms.campuscard.mapper.MerchantMapper;
import com.qms.campuscard.service.MerchantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;

    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @Override
    public Page<Merchant> pageList(long page, long size, String merchantName, Long typeId) {
        Page<Merchant> p = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(merchantName)) {
            wrapper.like(Merchant::getName, merchantName);
        }
        if (typeId != null) {
            wrapper.eq(Merchant::getTypeId, typeId);
        }
        wrapper.orderByDesc(Merchant::getId);
        return merchantMapper.selectPage(p, wrapper);
    }

    @Override
    public Merchant create(Merchant merchant) {
        merchant.setId(null);
        merchantMapper.insert(merchant);
        return merchant;
    }

    @Override
    public Merchant getById(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public Merchant update(Merchant merchant) {
        if (merchant.getId() == null) {
            throw new IllegalArgumentException("商户ID不能为空");
        }
        merchantMapper.updateById(merchant);
        return merchantMapper.selectById(merchant.getId());
    }

    @Override
    public void deleteById(Long id) {
        merchantMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        merchantMapper.deleteBatchIds(ids);
    }
}

