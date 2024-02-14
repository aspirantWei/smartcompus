package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.mapper.ClazzMapper;
import com.xiao.pojo.Clazz;
import com.xiao.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("clazzServiceImpl")
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
    @Override
    public IPage<Clazz> getClazzsByOpr(Page<Clazz> pageParam, Clazz clazz) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        if(clazz!=null){
            //年级名称条件
            String gradeName = clazz.getGradeName();
            if (!StringUtils.isEmpty(gradeName)) {
                queryWrapper.like("grade_name",gradeName);
            }
            //班级名称条件
            String clazzName = clazz.getName();
            if (!StringUtils.isEmpty(clazzName)) {
                queryWrapper.like("name",gradeName);
            }
            queryWrapper.orderByDesc("id");
            queryWrapper.orderByAsc("name");
        }
        Page<Clazz> clazzPage = baseMapper.selectPage(pageParam, queryWrapper);
        return clazzPage;
    }

    @Override
    public List<Clazz> getClazzs() {
       return baseMapper.selectList(null);
    }
}
