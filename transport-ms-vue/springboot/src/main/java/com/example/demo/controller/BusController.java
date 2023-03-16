package com.example.demo.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Bus;
import com.example.demo.mapper.BusMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bus")
public class BusController {

    @Resource
    BusMapper busMapper;

    //初始化车辆下拉框
    @GetMapping("/initial")
    public Result<?> initial(){
        LambdaQueryWrapper<Bus> wrapper = Wrappers.<Bus>lambdaQuery();
        List<Bus> list = busMapper.selectList(wrapper);
        return Result.success(list);
    }

    //根据线路名称查线路
    @GetMapping("/find")
    public Result<?> findById(@RequestParam Integer busId){
        Bus bus = busMapper.selectById(busId);
        return Result.success(bus);
    }

    //一键删除
    @PostMapping("/deleteAll")
    public Result<?> deleteAll(@RequestBody Map<String,Object> params){
        //获取传回来的id
        String ids = params.get("ids").toString();
        //通过逗号分隔字符串
        String[] id = ids.split(",");
        if(id.length==0){
            Result.error("-1","没有选中数据");
        }
        try {
            for (int i=0 ; i < id.length;i++){
                busMapper.deleteById(id[i]);
            }
        }catch (Exception e){
            return Result.error("-1","存在被分配工作的车辆，无法删除");
        }
        return Result.success();
    }

    //新增
    @PostMapping
    public Result<?> save(@RequestBody Bus bus) throws UnsupportedEncodingException {
        //车牌号重复
        String carNumber = URLDecoder.decode(bus.getCarNumber(),"utf-8");
        Bus res = busMapper.selectOne(Wrappers.<Bus>lambdaQuery()
                .eq(Bus::getCarNumber,carNumber));
        if(res != null){
            return Result.error("-1","车牌号重复");
        }
        //新增
        if(bus.getInformation() == null){
            bus.setInformation("");
        }else{
            bus.setInformation(URLDecoder.decode(bus.getInformation(),"utf-8"));
        }
        bus.setCarNumber(URLDecoder.decode(bus.getCarNumber(),"utf-8"));
        busMapper.insert(bus);
        return Result.success();
    }

    //编辑
    @PutMapping
    public Result<?> update(@RequestBody Bus bus) throws UnsupportedEncodingException{
        if(bus.getInformation() == null){
            bus.setInformation("");
        }else{
            bus.setInformation(URLDecoder.decode(bus.getInformation(),"utf-8"));
        }
        bus.setCarNumber(URLDecoder.decode(bus.getCarNumber(),"utf-8"));
        busMapper.updateById(bus);
        return Result.success();
    }

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(defaultValue = "") String carNumber,
                              @RequestParam(defaultValue = "") String information) throws UnsupportedEncodingException {
        if(StrUtil.isNotBlank(carNumber)){
            carNumber = URLDecoder.decode(carNumber,"utf-8");
        }
        if(StrUtil.isNotBlank(information)){
            information = URLDecoder.decode(information,"utf-8");
        }

        LambdaQueryWrapper<Bus> wrapper = Wrappers.<Bus>lambdaQuery();
        if(StrUtil.isNotBlank(carNumber)&&StrUtil.isNotBlank(information)){
            wrapper.like(Bus::getCarNumber,carNumber).like(Bus::getInformation,information);
        }else if(StrUtil.isNotBlank(carNumber)&&StrUtil.isBlank(information)){
            wrapper.like(Bus::getCarNumber,carNumber);
        }else if(StrUtil.isBlank(carNumber)&&StrUtil.isNotBlank(information)){
            wrapper.like(Bus::getInformation,information);
        }

        Page<Bus> busPage = busMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(busPage);
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable long id){
        try{
            busMapper.deleteById(id);
        }catch (Exception e){
            Result.error("-1","该车辆被分配工作，无法删除");
        }
        return Result.success();
    }


}
