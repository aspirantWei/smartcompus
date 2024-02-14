package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.mapper.AdminMapper;
import com.xiao.pojo.Admin;
import com.xiao.pojo.LoginForm;
import com.xiao.service.AdminService;
import com.xiao.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("adminServiceImpl")
@Transactional
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    /**
     * 超级管理员登录
     * @param loginForm
     * @return
     */
    @Override
    public Admin login(LoginForm loginForm) {
        //创建QuerryWrapper对象
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        //连接查询条件
        queryWrapper.eq("name",loginForm.getUsername());
        //转换成密文进行查询
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public  Admin getAdminById(Long userId) {
        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("id",userId);
        return baseMapper.selectOne(adminQueryWrapper);
    }

    @Override
    public IPage<Admin> getAdmins(Page<Admin> pageParam, String adminName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(adminName)) {
            queryWrapper.like("name",adminName);
        }
        queryWrapper.orderByAsc("id");
        Page<Admin> adminPage = baseMapper.selectPage(pageParam, queryWrapper);
        return adminPage;
    }
}
