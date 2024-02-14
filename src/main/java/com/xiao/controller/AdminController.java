package com.xiao.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.pojo.Admin;
import com.xiao.service.AdminService;
import com.xiao.util.MD5;
import com.xiao.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理员控制器")
@RestController
@RequestMapping("/sms/adminController")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @ApiOperation("添加或修改Admin信息")
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(
            @ApiParam("JSON转后端admin数据模型")     @RequestBody Admin admin
    ){
        if (!Strings.isEmpty(admin.getPassword())) {
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        return Result.ok();
    }

    @ApiOperation("删除一个或多个管理员信息")
    @DeleteMapping("/deleteAdmin")
    public Result deleteAdmin(
            @ApiParam("多个管理员id的JSON")    @RequestBody  List<Integer> ids
    ){
        adminService.removeByIds(ids);
       return Result.ok();
    }


    @ApiOperation("分页获取所有Admin信息带条件")
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAllAdmins(
            @ApiParam("分页查询页码数") @PathVariable("pageNo")  Integer pageNo,
            @ApiParam("分页查询页大小")   @PathVariable("pageSize")     Integer pageSize,
            @ApiParam("查询条件") String adminName
    ){
        Page<Admin> pageParam = new Page<>(pageNo,pageSize);
       IPage<Admin> iPage =  adminService.getAdmins(pageParam,adminName);
        return Result.ok(iPage);
    }
}
