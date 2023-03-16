package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.vo.Work;
import com.example.demo.mapper.WorkMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/work")
public class WorkController {

    @Resource
    WorkMapper workMapper;

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam String driverId,
                              @RequestParam String busId,
                              @RequestParam String routeId){
        int dId;
        int bId;
        int rId;
        LambdaQueryWrapper<Work> wrapper = Wrappers.<Work>lambdaQuery();
        LambdaQueryWrapper<Work> wrapper1 = Wrappers.<Work>lambdaQuery();
        LambdaQueryWrapper<Work> wrapper2 = Wrappers.<Work>lambdaQuery();
        LambdaQueryWrapper<Work> wrapper3 = Wrappers.<Work>lambdaQuery();
        if(StrUtil.isNotBlank(driverId)){
            dId = Integer.parseInt(driverId);
            wrapper1 = wrapper.eq(Work::getDriverId,dId);
        }else{
            wrapper1 = wrapper;
        }

        if(StrUtil.isNotBlank(busId)){
            bId = Integer.parseInt(busId);
            wrapper2 = wrapper1.eq(Work::getBusId,bId);
        }else {
            wrapper2 = wrapper1;
        }

        if(StrUtil.isNotBlank(routeId)){
            rId = Integer.parseInt(routeId);
            wrapper3 = wrapper2.eq(Work::getRouteId,rId);
        }else{
            wrapper3 = wrapper2;
        }
        Page<Work> workPage = workMapper.selectPage(new Page<>(pageNum, pageSize),wrapper3);
        return Result.success(workPage);
    }

    //查询司机工作
    @GetMapping("/myWork")
    public Result<?> myWork(@RequestParam String driverId){
        int dId = Integer.parseInt(driverId);
        LambdaQueryWrapper<Work> wrapper = Wrappers.<Work>lambdaQuery();
        Work res = workMapper.selectOne(wrapper.eq(Work::getDriverId,dId));
        return Result.success(res);
    }

    //根据司机id查询是否已经分配工作
    @GetMapping("/driver")
    public Result<?> driverIsWork(@RequestParam String driverId){
        int dId = Integer.parseInt(driverId);
        LambdaQueryWrapper<Work> wrapper = Wrappers.<Work>lambdaQuery();
        Work res = workMapper.selectOne(wrapper.eq(Work::getDriverId,dId));
        if(res == null){
            return Result.success();
        }else{
            return Result.error("-1","已分配工作");
        }
    }

    //根据车辆id查询是否已经分配工作
    @GetMapping("/bus")
    public Result<?> busIsWork(@RequestParam String busId){
        int bId = Integer.parseInt(busId);
        LambdaQueryWrapper<Work> wrapper = Wrappers.<Work>lambdaQuery();
        Work res = workMapper.selectOne(wrapper.eq(Work::getBusId,bId));
        if(res == null){
            return Result.success();
        }else{
            return Result.error("-1","已分配工作");
        }
    }

    //分配工作
    @GetMapping("/save")
    public Result<?> save(@RequestParam String driverId,@RequestParam String busId,@RequestParam String routeId){
        if(StrUtil.isBlank(driverId)){
            return Result.error("-1","请选择司机");
        }else if(StrUtil.isBlank(busId)){
            return Result.error("-1","请选择车辆");
        }else if(StrUtil.isBlank(routeId)){
            return Result.error("-1","请选择线路");
        }else{
            Work work = new Work();
            work.setDriverId(Integer.parseInt(driverId));
            work.setBusId(Integer.parseInt(busId));
            work.setRouteId(Integer.parseInt(routeId));
            workMapper.insert(work);
            return Result.success();
        }
    }

    //编辑工作
    @PutMapping
    public Result<?> update(@RequestBody Work work){
        workMapper.updateById(work);
        return Result.success();
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable long id){
        workMapper.deleteById(id);
        return Result.success();
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
        for (int i=0 ; i < id.length;i++){
            workMapper.deleteById(id[i]);
        }
        return Result.success();
    }
}
