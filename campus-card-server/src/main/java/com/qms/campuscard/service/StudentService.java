package com.qms.campuscard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Student;

public interface StudentService {

    Page<Student> pageList(long page, long size, String studentNo, String name);

    Student create(Student student);

    Student getById(Long id);

    Student update(Student student);

    void deleteById(Long id);
}

