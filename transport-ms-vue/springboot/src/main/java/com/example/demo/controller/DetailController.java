package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.vo.Detail;
import com.example.demo.mapper.DetailMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detail")

public class DetailController {

    @Resource
    DetailMapper detailMapper;

    //更新某条线路的某个站点
    @GetMapping("/update")
    public Result<?> update(@RequestParam Integer routeId,@RequestParam Integer count,@RequestParam Integer stationId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        if(stationId==0){
            detailMapper.update(null,Wrappers.<Detail>lambdaUpdate().set(Detail::getStationId,null).eq(Detail::getRouteId,routeId).eq(Detail::getCount,count));
        }else{
            Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count));
            res.setStationId(stationId);
            detailMapper.updateById(res);
        }
        return Result.success();
    }

    //查询某线路是否包含该站点
    @GetMapping("/station")
    public Result<?> isHaveStation(@RequestParam Integer routeId,@RequestParam Integer stationId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getStationId,stationId));
        if(res == null){
            return Result.success();
        }else{
            return Result.error("-1","该线路已经包含该站点");
        }
    }

    //查询该线路所有站点
    @GetMapping("/all")
    public Result<?> findAll(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize,
                             @RequestParam Integer routeId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Page<Detail> detailPage = detailMapper.selectPage(new Page<>(pageNum, pageSize),wrapper.eq(Detail::getRouteId,routeId));
        return Result.success(detailPage);
    }

    //查询该线路所有站点
    @GetMapping("/stations")
    public Result<?> findStations(@RequestParam Integer routeId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        List<Detail> list = detailMapper.selectList(wrapper.eq(Detail::getRouteId,routeId));
        return Result.success(list);
    }

    //查询某条线路某个站点
    @GetMapping("/count")
    public Result<?> findCount(@RequestParam Integer routeId,@RequestParam Integer count){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count));
        if(res == null){
            return Result.error("-1","");
        }else{
            return Result.success(res.getStationId());
        }
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam Integer stationId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Page<Detail> detailPage = detailMapper.selectPage(new Page<>(pageNum, pageSize),wrapper.eq(Detail::getStationId,stationId));
        return Result.success(detailPage);
    }

    //查询上一站的id
    @GetMapping("/last")
    public Result<?> findLast(@RequestParam Integer routeId,@RequestParam Integer count){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count-1));
        if(res == null){
            return Result.error("-1","没有上一站");
        }else{
            return Result.success(res.getStationId());
        }
    }

    //查询下一站的id
    @GetMapping("/next")
    public Result<?> findNext(@RequestParam Integer routeId,@RequestParam Integer count){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count+1));
        if(res == null ){
            return Result.error("-1","终点站");
        }else{
            return Result.success(res.getStationId());
        }
    }

    //增加一站点
    @GetMapping("/add")
    public Result<?> add(@RequestParam Integer routeId,@RequestParam Integer count){
        //count是已经加1的值了
        //修改原来的最后一站
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        Detail res = detailMapper.selectOne(wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count-1));
        int endStation = res.getStationId();
        detailMapper.update(null,Wrappers.<Detail>lambdaUpdate().set(Detail::getStationId,null).eq(Detail::getId,res.getId()));

        Detail detail = new Detail();
        detail.setRouteId(routeId);
        detail.setCount(count);
        detail.setStationId(endStation);
        detailMapper.insert(detail);
        return Result.success();
    }

    //减少一个站点
    @GetMapping("/minus")
    public Result<?> minus(@RequestParam Integer routeId,@RequestParam Integer count){
        //count是已经减1的值了
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        LambdaQueryWrapper<Detail> wrapper1 = Wrappers.<Detail>lambdaQuery();
        LambdaQueryWrapper<Detail> wrapper2 = Wrappers.<Detail>lambdaQuery();
        wrapper1 = wrapper.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count+1);
        Detail res = detailMapper.selectOne(wrapper1);
        res.setCount(count);
        Detail res1 = detailMapper.selectOne(wrapper2.eq(Detail::getRouteId,routeId).eq(Detail::getCount,count));
        if(res1 != null){
            detailMapper.deleteById(res1.getId());
        }
        detailMapper.updateById(res);
        return Result.success();
    }

    //增加一条线路
    @GetMapping("/addroute")
    public Result<?> addRoute(@RequestParam String routeId1,@RequestParam String startStation1,
                              @RequestParam String endStation1,@RequestParam String sum1){
        int routeId = Integer.parseInt(routeId1);
        int startStation;
        int endStation;
        int sum;
        Detail detail = new Detail();

        if(StrUtil.isBlank(sum1)){
            sum = 2;
        }else{
            sum = Integer.parseInt(sum1);
        }

        if(StrUtil.isBlank(startStation1)){
            detail.setStationId(null);
        }else{
            startStation = Integer.parseInt(startStation1);
            detail.setStationId(startStation);
        }
        //插入起始站点
        detail.setRouteId(routeId);
        detail.setCount(1);
        detailMapper.insert(detail);
        //插入中间站点
        detail.setStationId(null);
        for(int i=2;i<sum;i++){
            detail = new Detail();
            detail.setRouteId(routeId);
            detail.setStationId(null);
            detail.setCount(i);
            detailMapper.insert(detail);
        }
        //插入终止站点
        Detail detail1 = new Detail();
        detail1.setCount(sum);
        detail1.setRouteId(routeId);
        if(StrUtil.isBlank(endStation1)){
            detail1.setStationId(null);
        }else{
            endStation = Integer.parseInt(endStation1);
            detail1.setStationId(endStation);
        }
        detailMapper.insert(detail1);
        return Result.success();
    }

    //更新线路
    @GetMapping("/updateroute")
    public Result<?> updateRoute(@RequestParam Integer routeId,@RequestParam Integer stationId1,@RequestParam Integer stationId2,@RequestParam Integer sum){
        if(sum < 2 ){
            return Result.error("-1","最少两站");
        }else{
            LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
            detailMapper.delete(wrapper.eq(Detail::getRouteId,routeId).gt(Detail::getCount,sum));
            LambdaQueryWrapper<Detail> wrapper1 = Wrappers.<Detail>lambdaQuery();
            Detail res = detailMapper.selectOne(wrapper1.eq(Detail::getRouteId,routeId).eq(Detail::getCount,sum));
            LambdaQueryWrapper<Detail> wrapper2 = Wrappers.<Detail>lambdaQuery();
            Detail res1 = detailMapper.selectOne(wrapper2.eq(Detail::getRouteId,routeId).eq(Detail::getCount,1));
            res1.setStationId(stationId1);
            detailMapper.updateById(res1);
            if(res == null){
                Detail detail = new Detail();
                detail.setRouteId(routeId);
                detail.setCount(sum);
                detail.setStationId(stationId2);
                detailMapper.insert(detail);
            }else{
                res.setStationId(stationId2);
                detailMapper.updateById(res);
            }
            return Result.success();
        }

    }

    //插入信息
    @GetMapping("/one")
    public Result<?> insertOne(@RequestParam Integer routeId,@RequestParam Integer count,@RequestParam Integer stationId){
        Detail detail = new Detail();
        detail.setRouteId(routeId);
        detail.setCount(count);
        detail.setStationId(stationId);
        detailMapper.insert(detail);
        return Result.success();
    }

    //删除一条线路
    @GetMapping("/delete")
    public Result<?> deleteRoute(@RequestParam Integer routeId){
        LambdaQueryWrapper<Detail> wrapper = Wrappers.<Detail>lambdaQuery();
        detailMapper.delete(wrapper.eq(Detail::getRouteId,routeId));
        return Result.success();
    }

    //一键删除
    @GetMapping("/deleteAll")
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
                detailMapper.deleteById(id[i]);
            }
        }catch (Exception e){
            return Result.error("-1","存在线路被分配工作，无法删除");
        }

        return Result.success();
    }
}
