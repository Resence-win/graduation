package com.qms.campuscard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qms.campuscard.entity.Student;
import com.qms.campuscard.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 学生列表
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") long page,
                                    @RequestParam(defaultValue = "10") long size,
                                    @RequestParam(required = false) String studentNo,
                                    @RequestParam(required = false) String name) {
        Page<Student> result = studentService.pageList(page, size, studentNo, name);

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

    public record StudentCreateRequest(
            @NotBlank(message = "学号不能为空") String studentNo,
            @NotBlank(message = "姓名不能为空") String name,
            String gender,
            String college,
            String major,
            String className,
            String phone
    ) {}

    public record StudentUpdateRequest(
            @NotNull(message = "ID不能为空") Long id,
            @NotBlank(message = "学号不能为空") String studentNo,
            @NotBlank(message = "姓名不能为空") String name,
            String gender,
            String college,
            String major,
            String className,
            String phone
    ) {}

    // 新增学生
    @PostMapping
    public Map<String, Object> create(@RequestBody @Valid StudentCreateRequest req) {
        Student s = new Student();
        s.setStudentNo(req.studentNo());
        s.setName(req.name());
        s.setGender(req.gender());
        s.setCollege(req.college());
        s.setMajor(req.major());
        s.setClassName(req.className());
        s.setPhone(req.phone());

        Student created = studentService.create(s);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", created);
        return body;
    }

    // 学生详情
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Student student = studentService.getById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", student);
        return body;
    }

    // 修改学生
    @PutMapping
    public Map<String, Object> update(@RequestBody @Valid StudentUpdateRequest req) {
        Student s = new Student();
        s.setId(req.id());
        s.setStudentNo(req.studentNo());
        s.setName(req.name());
        s.setGender(req.gender());
        s.setCollege(req.college());
        s.setMajor(req.major());
        s.setClassName(req.className());
        s.setPhone(req.phone());

        Student updated = studentService.update(s);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", updated);
        return body;
    }

    // 删除学生
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        studentService.deleteById(id);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "success");
        body.put("data", null);
        return body;
    }
}

