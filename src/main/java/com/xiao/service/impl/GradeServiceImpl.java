package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.mapper.GradeMapper;
import com.xiao.pojo.Grade;
import com.xiao.service.GradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Transactional
@Service("gradeServiceImpl")
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Override
    public IPage<Grade> getGradeByOpr(Page<Grade> pageParam, String gradeName) {
        //设置查询条件
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        //设置分页规则
        if (!StringUtils.isEmpty(gradeName)) {
            queryWrapper.like("name",gradeName);

        }
        //设置排序规则
        queryWrapper.orderByAsc("name");
        queryWrapper.orderByDesc("id");

        //分页查询数据
        Page<Grade> page = baseMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public List<Grade> getGrades() {
        List<Grade> grades = baseMapper.selectList(null);
        return grades;
    }
}
