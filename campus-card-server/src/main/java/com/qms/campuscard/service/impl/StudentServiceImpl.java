package com.qms.campuscard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Student;
import com.qms.campuscard.mapper.StudentMapper;
import com.qms.campuscard.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public Page<Student> pageList(long page, long size, String studentNo, String name) {
        Page<Student> p = new Page<>(page, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(studentNo)) {
            wrapper.like(Student::getStudentNo, studentNo);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Student::getName, name);
        }
        wrapper.orderByDesc(Student::getId);
        return studentMapper.selectPage(p, wrapper);
    }

    @Override
    public Student create(Student student) {
        student.setId(null);
        studentMapper.insert(student);
        return student;
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Student update(Student student) {
        if (student.getId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        studentMapper.updateById(student);
        return studentMapper.selectById(student.getId());
    }

    @Override
    public void deleteById(Long id) {
        studentMapper.deleteById(id);
    }
}

