package com.qms.campuscard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Teacher;
import com.qms.campuscard.mapper.TeacherMapper;
import com.qms.campuscard.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public Page<Teacher> pageList(long page, long size, String teacherNo, String name) {
        Page<Teacher> p = new Page<>(page, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(teacherNo)) {
            wrapper.like(Teacher::getTeacherNo, teacherNo);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Teacher::getName, name);
        }
        wrapper.orderByDesc(Teacher::getId);
        return teacherMapper.selectPage(p, wrapper);
    }

    @Override
    public Teacher create(Teacher teacher) {
        teacher.setId(null);
        teacherMapper.insert(teacher);
        return teacher;
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.selectById(id);
    }

    @Override
    public Teacher update(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        teacherMapper.updateById(teacher);
        return teacherMapper.selectById(teacher.getId());
    }

    @Override
    public void deleteById(Long id) {
        teacherMapper.deleteById(id);
    }
}

