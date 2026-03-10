package com.qms.campuscard.service;

import com.qms.campuscard.entity.MerchantType;

import java.util.List;

public interface MerchantTypeService {

    List<MerchantType> listAll();

    MerchantType create(MerchantType merchantType);
}

