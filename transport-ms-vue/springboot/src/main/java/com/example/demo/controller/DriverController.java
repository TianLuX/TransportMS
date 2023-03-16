package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Driver;
import com.example.demo.mapper.DriverMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


@RestController
@RequestMapping("/driver")
public class DriverController {

    @Resource
    DriverMapper driverMapper;

    //查询所有审核通过的司机
    @GetMapping("/initial")
    public Result<?> initial(){
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        List<Driver> list = driverMapper.selectList(wrapper.eq(Driver::getStatus,"已审核"));
        return Result.success(list);
    }

    //登录
    @GetMapping("/login")
    public Result<?> login(@RequestParam(defaultValue = "") String driverName,@RequestParam(defaultValue = "") String password){
        Driver res = driverMapper.selectOne(Wrappers.<Driver>lambdaQuery()
                .eq(Driver::getDriverName,driverName)
                .eq(Driver::getPassword,password));
        if(res == null){
            return Result.error("-1","用户名或密码错误");
        }
        return Result.success();
    }

    //修改密码
    @GetMapping("/repass")
    public Result<?> repass(@RequestParam String driverName,@RequestParam String password) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        driver.setPassword(password);
        driverMapper.update(driver,wrapper.eq(Driver::getDriverName,driverName));
        return Result.success();
    }

    //根据线路名称查线路
    @GetMapping("/find")
    public Result<?> findById(@RequestParam Integer driverId){
        Driver driver = driverMapper.selectById(driverId);
        return Result.success(driver);
    }

    //新增
    @PostMapping("/register")
    public Result<?> register(@RequestBody Driver driver) throws UnsupportedEncodingException {
        String driverName = URLDecoder.decode(driver.getDriverName(),"utf-8");
        driver.setDriverName(driverName);
        String sex = URLDecoder.decode(driver.getSex(),"utf-8");
        driver.setSex(sex);
        driver.setStatus("未审核");
        Driver res = driverMapper.selectOne(Wrappers.<Driver>lambdaQuery()
                .eq(Driver::getDriverName,driver.getDriverName()));
        if(res != null){
            return Result.error("-1","用户名重复");
        }
        if(driver.getPassword() == null){
            driver.setPassword("123456");
        }
        driverMapper.insert(driver);
        return Result.success();
    }


    //新增
    @PostMapping
    public Result<?> save(@RequestBody Driver driver){
        if(driver.getPassword() == null){
            driver.setPassword("123456");
        }
        driverMapper.insert(driver);
        return Result.success();
    }

    //编辑
    @PutMapping
    public Result<?> update(@RequestBody Driver driver){
        driverMapper.updateById(driver);
        return Result.success();
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable long id){
        try{
            driverMapper.deleteById(id);
        }catch (Exception e){
            return Result.error("-1","该司机已经被分配工作，无法删除");
        }
        return Result.success();
    }

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(defaultValue = "") String driverName,
                              @RequestParam(defaultValue = "") String sex,
                              @RequestParam(defaultValue = "") String status) throws UnsupportedEncodingException {
        if(StrUtil.isNotBlank(driverName)){
            driverName = URLDecoder.decode(driverName,"utf-8");
        }
        if(StrUtil.isNotBlank(sex)){
            sex = URLDecoder.decode(sex,"utf-8");
        }
        if(StrUtil.isNotBlank(status)){
            status = URLDecoder.decode(status,"utf-8");
        }
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        if(StrUtil.isNotBlank(driverName)&&StrUtil.isBlank(sex)&&StrUtil.isBlank(status)){
            wrapper.like(Driver::getDriverName,driverName);
        }else if(StrUtil.isNotBlank(driverName)&&StrUtil.isBlank(sex)){
            wrapper.like(Driver::getDriverName,driverName).eq(Driver::getStatus,status);
        }else if(StrUtil.isNotBlank(driverName)&&StrUtil.isBlank(status)){
            wrapper.like(Driver::getDriverName,driverName).eq(Driver::getSex,sex);
        }else if(StrUtil.isNotBlank(driverName)){
            wrapper.like(Driver::getDriverName,driverName).eq(Driver::getSex,sex).eq(Driver::getStatus,status);
        }else if(StrUtil.isBlank(driverName)&&StrUtil.isNotBlank(sex)&&StrUtil.isNotBlank(status)){
            wrapper.eq(Driver::getSex,sex).eq(Driver::getStatus,status);
        }else if(StrUtil.isBlank(driverName)&&StrUtil.isNotBlank(status)){
            wrapper.eq(Driver::getStatus,status);
        }else if(StrUtil.isBlank(driverName)&&StrUtil.isNotBlank(sex)){
            wrapper.eq(Driver::getSex,sex);
        }
        Page<Driver> userPage = driverMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(userPage);
    }


    //快速查询
    @GetMapping("/fast")
    public Result<?> fast(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String driverName,
                              @RequestParam(defaultValue = "") String sex) throws UnsupportedEncodingException {
        if(StrUtil.isNotBlank(driverName)){
            driverName = URLDecoder.decode(driverName,"utf-8");
        }
        if(StrUtil.isNotBlank(sex)){
            sex = URLDecoder.decode(sex,"utf-8");
        }
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        if(StrUtil.isNotBlank(driverName)&&StrUtil.isBlank(sex)){
            wrapper.like(Driver::getDriverName,driverName);
        }else if(StrUtil.isNotBlank(driverName)&&StrUtil.isNotBlank(sex)){
            wrapper.like(Driver::getDriverName,driverName).eq(Driver::getSex,sex);
        }else if(StrUtil.isBlank(driverName)&&StrUtil.isNotBlank(sex)){
            wrapper.eq(Driver::getSex,sex);
        }
        Page<Driver> userPage = driverMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(userPage);
    }

    //查询驾照
    @GetMapping("/findLicense")
    public Result<?> findLicense(@RequestParam(defaultValue = "") String driverName) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        return Result.success(driver);
    }

    //查询id
    @GetMapping("/findId")
    public Result<?> findId(@RequestParam(defaultValue = "") String driverName) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        return Result.success(driver);
    }

    //更新驾照
    @GetMapping("/updateLicense")
    public Result<?> updateLicense(@RequestParam(defaultValue = "") String driverName,@RequestParam(defaultValue = "") String url) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        driver.setUrl(url);
        driverMapper.updateById(driver);
        return Result.success();
    }

    //更新信息
    @GetMapping("/updateInf")
    public Result<?> updateInf(@RequestParam(defaultValue = "") String driverName,@RequestParam(defaultValue = "未知") String sex, @RequestParam(defaultValue = "") String time) throws UnsupportedEncodingException, ParseException {
        //获取到日期
        time = URLDecoder.decode(time,"utf-8");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
        Date date = simpleDateFormat.parse(time);
        //日期转换少一天
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 把日期往后增加一天,整数  往后推,负数往前移动
        calendar.add(calendar.DATE, 1);
        // 这个时间就是日期往后推一天的结果
        date=calendar.getTime();

        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        driver.setSex(URLDecoder.decode(sex,"utf-8"));
        driver.setTime(date);
        driver.setStatus("未审核");
        driverMapper.updateById(driver);
        return Result.success();
    }

    //审核通过
    @GetMapping("/past")
    public Result<?> past(@RequestParam(defaultValue = "") String driverName,@RequestParam(defaultValue = "") String status) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Driver> wrapper = Wrappers.<Driver>lambdaQuery();
        Driver driver = driverMapper.selectOne(wrapper.eq(Driver::getDriverName,driverName));
        driver.setStatus(URLDecoder.decode(status,"utf-8"));
        driverMapper.updateById(driver);
        return Result.success();
    }

}
