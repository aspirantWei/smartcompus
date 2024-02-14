package com.xiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.pojo.Admin;
import com.xiao.pojo.LoginForm;
import com.xiao.pojo.Student;
import com.xiao.pojo.Teacher;
import com.xiao.service.AdminService;
import com.xiao.service.StudentService;
import com.xiao.service.TeacherService;
import com.xiao.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "系统控制器")
@RestController
@RequestMapping("/sms/system")
public class SystemController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @ApiOperation("修改密码")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
           @ApiParam("token口令") @RequestHeader("token") String token,
           @ApiParam("旧密码")  @PathVariable("oldPwd") String oldPwd,
           @ApiParam("新密码") @PathVariable("newPwd") String newPwd
    ) {
        boolean expiration = JwtHelper.isExpiration(token);
        //token过期
        if (expiration) {
            return Result.fail().message("token失效!");
        }
        //通过token获取当前登录的用户id
        Long userId = JwtHelper.getUserId(token);
        //通过token获取当前登录的用户类型

        Integer userType = JwtHelper.getUserType(token);
        // 将明文密码转换为暗文
        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);
        //判断用户类型进行密码修改
        if (userType == 1) {
            QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
            adminQueryWrapper.eq("id", userId.intValue())
                    .eq("password", oldPwd);
            Admin admin = adminService.getOne(adminQueryWrapper);
            if (admin != null) {
                admin.setPassword(newPwd);
                adminService.saveOrUpdate(admin);
            } else {
                return Result.fail().message("原密码输入有误");
            }
        } else if (userType == 2) {
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("id", userId.intValue())
                    .eq("password", oldPwd);
            Student student = studentService.getOne(studentQueryWrapper);
            if (student != null) {
                student.setPassword(newPwd);
                studentService.saveOrUpdate(student);
            } else {
                return Result.fail().message("原密码输入有误");
            }
        } else if (userType == 3) {
            QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
            teacherQueryWrapper.eq("id", userId.intValue())
                    .eq("password", oldPwd);
            Teacher teacher = teacherService.getOne(teacherQueryWrapper);
            if (teacher != null) {
                teacher.setPassword(newPwd);
                teacherService.saveOrUpdate(teacher);
            } else {
                return Result.fail().message("原密码输入有误");
            }

        }
        return Result.ok();
    }

    @ApiOperation("头像上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("头像文件") @RequestPart("multipartFile") MultipartFile multipartFile
            ){
        //使用UUID随机生成文件名
        String  uuid = UUID.randomUUID().toString().replace("-","").toLowerCase();
        //生成新的文件名字
        String fileName = uuid.concat(multipartFile.getOriginalFilename());
        //生成文件的保存路径(实际生产环境这里会使用真正的文件存储服务器)
        String portraitPath = "D:/project/smartcompus/target/classes/public/upload/".concat(fileName);

        //保存文件
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String headerImg = "upload/"+fileName;
        return Result.ok(headerImg);
    }


    @ApiOperation("通过token获取用户信息")
    @GetMapping("/getInfo")
    public Result getUserInfoByToken(
            @ApiParam("token口令")@RequestHeader("token") String token
    ){
        //获取用户中请求的token
        //检查token是否过期 20h
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        // 解析token,获取用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        //准备一个Map集合用于存响应数据
        Map<String, Object> map = new HashMap<>();
        switch (userType){
            case 1:
               Admin admin = adminService.getAdminById(userId);
               map.put("user",admin);
               map.put("userType",1);
               break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("user",student);
                map.put("userType",2);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("user",teacher);
                map.put("userType",3);
                break;
        }
        return Result.ok(map);
    }



    @ApiOperation("登录请求验证")
    @PostMapping("/login")
    public Result login(
          @ApiParam("登录表单验证")  @RequestBody LoginForm loginForm, HttpServletRequest request
    ) {
        /*用户验证码验证*/
        //获取Session中的验证码和用户提交的验证码
        HttpSession session = request.getSession();
        String systemVerfiCode = (String) session.getAttribute("verfiCode");
        String loginVerifiCode = loginForm.getVerifiCode();
        if ("".equals(systemVerfiCode)) {
            //Session过期，验证码超时
            return Result.fail().message("验证码失效，请刷新后重试");
        }
        if (!loginVerifiCode.equals(loginVerifiCode)) {
            //验证码有误
            return Result.fail().message("验证码有误，请重新输入");
        }
        //验证码使用完毕，移除当前请求域中验证码
        session.removeAttribute("verfiCode");

        /*用户登录验证*/
        //准备一个Map集合，存放用户响应信息
        Map<String, Object> map = new HashMap<>();
        //根据用户身份，验证用户的登录信息
        // 根据用户身份,验证登录的用户信息
        switch (loginForm.getUserType()){
            case "1":  // 管理员身份
                try {
                    // 调用服务层登录方法,根据用户提交的LoginInfo信息,查询对应的Admin对象,找不到返回Null
                    Admin login = adminService.login(loginForm);
                    if (null != login) {
                        // 登录成功,将用户id和用户类型转换为token口令,作为信息响应给前端
                        map.put("token",JwtHelper.createToken(login.getId().longValue(), 1));
                    }else{
                        throw  new RuntimeException("用户名或者密码有误!");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    // 捕获异常,向用户响应错误信息
                    return Result.fail().message(e.getMessage());
                }

            case "2":// 学生身份
                try {
                    // 调用服务层登录方法,根据用户提交的LoginInfo信息,查询对应的Student对象,找不到返回Null
                    Student login = studentService.login(loginForm);
                    if (null != login) {
                        // 登录成功,将用户id和用户类型转换为token口令,作为信息响应给前端
                        map.put("token",JwtHelper.createToken(login.getId().longValue(), 2));
                    }else{
                        throw  new RuntimeException("用户名或者密码有误!");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    // 捕获异常,向用户响应错误信息
                    return Result.fail().message(e.getMessage());
                }
            case "3":// 教师身份
                // 调用服务层登录方法,根据用户提交的LoginInfo信息,查询对应的Teacher对象,找不到返回Null
                try {
                    // 调用服务层登录方法,根据用户提交的LoginInfo信息,查询对应的Student对象,找不到返回Null
                    Teacher login = teacherService.login(loginForm);
                    if (null != login) {
                        // 登录成功,将用户id和用户类型转换为token口令,作为信息响应给前端
                        map.put("token",JwtHelper.createToken(login.getId().longValue(), 3));
                    }else{
                        throw  new RuntimeException("用户名或者密码有误!");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    // 捕获异常,向用户响应错误信息
                    return Result.fail().message(e.getMessage());
                }


        }
        // 查无此用户,响应失败
        return Result.fail().message("查无此用户");
    }


    @ApiOperation("获取验证码图片")
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取验证码图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片验证码
        String verfiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入Session域中，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verfiCode",verfiCode);
        //将验证码响应给浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
