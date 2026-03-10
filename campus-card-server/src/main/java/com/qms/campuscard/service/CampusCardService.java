package com.qms.campuscard.service;

import com.qms.campuscard.entity.CampusCard;

public interface CampusCardService {

    /**
     * 根据学号/工号开卡
     * @param userNo   学号或工号
     * @param userType student / teacher
     */
    CampusCard openCard(String userNo, String userType);

    CampusCard getById(Long cardId);

    void loss(Long cardId);

    void unloss(Long cardId);

    void cancel(Long cardId);
}

