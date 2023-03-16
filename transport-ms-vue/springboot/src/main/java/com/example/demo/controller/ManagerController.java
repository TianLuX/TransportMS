package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Manager;
import com.example.demo.mapper.ManagerMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Resource
    ManagerMapper managerMapper;

    //登录
    @GetMapping("/login")
    public Result<?> login(@RequestParam(defaultValue = "") String managerName,@RequestParam(defaultValue = "") String password){
        Manager res = managerMapper.selectOne(Wrappers.<Manager>lambdaQuery()
                .eq(Manager::getManagerName,managerName)
                .eq(Manager::getPassword,password));
        if(res == null){
            return Result.error("-1","用户名或密码错误");
        }
        return Result.success();
    }

    //修改密码
    @GetMapping("/repass")
    public Result<?> repass(@RequestParam String managerName,@RequestParam String password) throws UnsupportedEncodingException {
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setPassword(password);
        LambdaQueryWrapper<Manager> wrapper = Wrappers.<Manager>lambdaQuery();
        managerMapper.update(manager,wrapper.eq(Manager::getManagerName,managerName));
        return Result.success();
    }
}
