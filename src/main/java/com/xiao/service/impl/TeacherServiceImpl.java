package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.mapper.TeacherMapper;
import com.xiao.pojo.LoginForm;
import com.xiao.pojo.Teacher;
import com.xiao.service.TeacherService;
import com.xiao.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
@Service("teacherServiceImpl")
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    /**
     * 教师登录方法
     * @param loginForm
     * @return
     */
    @Override
    public Teacher login(LoginForm loginForm) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Teacher teacher = baseMapper.selectOne(queryWrapper);
        return teacher;
    }

    @Override
    public Teacher getTeacherById(Long userId) {
        QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
        teacherQueryWrapper.eq("id",userId);
        return baseMapper.selectOne(teacherQueryWrapper);
    }

    @Override
    public IPage<Teacher> getTeachersByOpr(Page<Teacher> pageParam, Teacher teacher) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if(teacher != null){
            //班级名称条件
            String clazzName = teacher.getClazzName();
            if (!StringUtils.isEmpty(clazzName)) {
                queryWrapper.eq("clazz_name",clazzName);
            }
            //教师名称条件
            String teacherName = teacher.getName();
            if(!StringUtils.isEmpty(teacherName)){
                queryWrapper.like("name",teacherName);
            }
            queryWrapper.orderByDesc("id");
            queryWrapper.orderByAsc("name");
        }

        IPage<Teacher> teacherPage = baseMapper.selectPage(pageParam, queryWrapper);

        return teacherPage;
    }
}
