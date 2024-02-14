package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.mapper.StudentMapper;
import com.xiao.pojo.LoginForm;
import com.xiao.pojo.Student;
import com.xiao.service.StudentService;
import com.xiao.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("studentServiceImpl")
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    /**
     * 学生登录方法
     * @return
     */
    @Override
    public Student login(LoginForm loginForm) {
        //创建QuerryWrapper对象
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        //连接查询条件
        queryWrapper.eq("name",loginForm.getUsername());
        //转换成密文进行查询
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Student student = baseMapper.selectOne(queryWrapper);
        return student;

    }

    @Override
    public Student getStudentById(Long userId) {
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.eq("id",userId);
        return baseMapper.selectOne(studentQueryWrapper);

    }

    @Override
    public IPage<Student> getStudentByOpr(Page<Student> pageParam, Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (student != null) {
            if (student.getClazzName() != null) {
                queryWrapper.eq("clazz_name", student.getClazzName());
                if (student.getName() != null) {
                    queryWrapper.like("name", student.getName());
                }
            }
            queryWrapper.orderByDesc("id");

        }
        //分页查询数据
        Page<Student> studentPage = baseMapper.selectPage(pageParam, queryWrapper);
        return studentPage;
    }




}
