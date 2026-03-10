package com.qms.campuscard.service.impl;

import com.qms.campuscard.entity.MerchantType;
import com.qms.campuscard.mapper.MerchantTypeMapper;
import com.qms.campuscard.service.MerchantTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantTypeServiceImpl implements MerchantTypeService {

    private final MerchantTypeMapper merchantTypeMapper;

    public MerchantTypeServiceImpl(MerchantTypeMapper merchantTypeMapper) {
        this.merchantTypeMapper = merchantTypeMapper;
    }

    @Override
    public List<MerchantType> listAll() {
        return merchantTypeMapper.selectList(null);
    }

    @Override
    public MerchantType create(MerchantType merchantType) {
        merchantType.setId(null);
        merchantTypeMapper.insert(merchantType);
        return merchantType;
    }
}

