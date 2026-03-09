package com.qms.campuscard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Teacher;

public interface TeacherService {

    Page<Teacher> pageList(long page, long size, String teacherNo, String name);

    Teacher create(Teacher teacher);

    Teacher getById(Long id);

    Teacher update(Teacher teacher);

    void deleteById(Long id);
}

