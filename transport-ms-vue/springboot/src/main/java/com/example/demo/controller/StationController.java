package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Station;
import com.example.demo.mapper.StationMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    StationMapper stationMapper;

    //根据id查站点名称
    @GetMapping("/name")
    public Result<?> getName(@RequestParam Integer sid){
        LambdaQueryWrapper<Station> wrapper = Wrappers.<Station>lambdaQuery();
        Station res = stationMapper.selectOne(wrapper.eq(Station::getId,sid));
        return Result.success(res.getStationName());
    }

    //根据id查站点
    @GetMapping("/inf")
    public Result<?> getStation(@RequestParam Integer sid){
        LambdaQueryWrapper<Station> wrapper = Wrappers.<Station>lambdaQuery();
        Station res = stationMapper.selectOne(wrapper.eq(Station::getId,sid));
        return Result.success(res);
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
        try{
            for (int i=0 ; i < id.length;i++){
                stationMapper.deleteById(id[i]);
            }
        }catch (Exception e){
            return Result.error("-1","存在站点在线路中，无法删除");
        }
        return Result.success();
    }

    //新增
    @PostMapping
    public Result<?> save(@RequestBody Station station) throws UnsupportedEncodingException {
        //站台名称重复
        String stationName = URLDecoder.decode(station.getStationName(),"utf-8");
        Station res = stationMapper.selectOne(Wrappers.<Station>lambdaQuery()
                .eq(Station::getStationName,stationName));
        if(res != null){
            return Result.error("-1","站台名称重复");
        }
        station.setStationName(URLDecoder.decode(station.getStationName(),"utf-8"));
        stationMapper.insert(station);
        return Result.success();
    }

    //编辑
    @PutMapping
    public Result<?> update(@RequestBody Station station) throws UnsupportedEncodingException {
        //站台名称重复
        station.setStationName(URLDecoder.decode(station.getStationName(),"utf-8"));
        stationMapper.updateById(station);
        return Result.success();
    }

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(defaultValue = "") String stationName) throws UnsupportedEncodingException {
        if(StrUtil.isNotBlank(stationName)){
            stationName = URLDecoder.decode(stationName,"utf-8");
        }
        LambdaQueryWrapper<Station> wrapper = Wrappers.<Station>lambdaQuery();
        if(StrUtil.isNotBlank(stationName)){
            wrapper.like(Station::getStationName,stationName);
        }
        Page<Station> stationPage = stationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(stationPage);
    }

    //地图查询
    @GetMapping("/map")
    public Result<?> mapFind(@RequestParam(defaultValue = "") String stationName) throws UnsupportedEncodingException {
        if(StrUtil.isNotBlank(stationName)){
            stationName = URLDecoder.decode(stationName,"utf-8");
        }
        LambdaQueryWrapper<Station> wrapper = Wrappers.<Station>lambdaQuery();
        if(StrUtil.isNotBlank(stationName)){
            wrapper.like(Station::getStationName,stationName).ne(Station::getLang,"").ne(Station::getLa,"");
        }else{
            wrapper.ne(Station::getLang,"").ne(Station::getLa,"");
        }
        List<Station> list = stationMapper.selectList(wrapper);
        return Result.success(list);
    }

    //初始化下拉框
    @GetMapping("/initial")
    public Result<?> initial(){
        LambdaQueryWrapper<Station> wrapper = Wrappers.<Station>lambdaQuery();
        List<Station> list = stationMapper.selectList(wrapper.ne(Station::getLang,"").ne(Station::getLa,""));
        return Result.success(list);
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable long id){
        try {
            stationMapper.deleteById(id);
        }catch (Exception e){
            return Result.error("-1","该站点在线路中，无法删除");
        }
        return Result.success();
    }
}
