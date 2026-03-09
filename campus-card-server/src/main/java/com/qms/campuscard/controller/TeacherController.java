package com.qms.campuscard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Teacher;
import com.qms.campuscard.service.TeacherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // 教师列表
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") long page,
                                    @RequestParam(defaultValue = "10") long size,
                                    @RequestParam(required = false) String teacherNo,
                                    @RequestParam(required = false) String name) {
        Page<Teacher> result = teacherService.pageList(page, size, teacherNo, name);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", data);
        return body;
    }

    public record TeacherCreateRequest(
            @NotBlank(message = "教工号不能为空") String teacherNo,
            @NotBlank(message = "姓名不能为空") String name,
            String college,
            String phone
    ) {}

    public record TeacherUpdateRequest(
            @NotNull(message = "ID不能为空") Long id,
            @NotBlank(message = "教工号不能为空") String teacherNo,
            @NotBlank(message = "姓名不能为空") String name,
            String college,
            String phone
    ) {}

    // 新增教师
    @PostMapping
    public Map<String, Object> create(@RequestBody @Valid TeacherCreateRequest req) {
        Teacher t = new Teacher();
        t.setTeacherNo(req.teacherNo());
        t.setName(req.name());
        t.setCollege(req.college());
        t.setPhone(req.phone());

        Teacher created = teacherService.create(t);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", created);
        return body;
    }

    // 教师详情
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", teacher);
        return body;
    }

    // 修改教师
    @PutMapping
    public Map<String, Object> update(@RequestBody @Valid TeacherUpdateRequest req) {
        Teacher t = new Teacher();
        t.setId(req.id());
        t.setTeacherNo(req.teacherNo());
        t.setName(req.name());
        t.setCollege(req.college());
        t.setPhone(req.phone());

        Teacher updated = teacherService.update(t);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", updated);
        return body;
    }

    // 删除教师
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        teacherService.deleteById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }
}

