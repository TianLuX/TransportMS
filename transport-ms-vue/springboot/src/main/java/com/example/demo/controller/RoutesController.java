package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Routes;
import com.example.demo.mapper.RoutesMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/routes")
public class RoutesController {

    @Resource
    RoutesMapper routesMapper;

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
                routesMapper.deleteById(id[i]);
            }
        }catch (Exception e){
            return Result.error("-1","存在线路被分配工作，无法删除");
        }

        return Result.success();
    }

    //根据线路名称查询线路总站数量
    @GetMapping("/sum")
    public Result<?> sum(@RequestParam Integer routeId){
        Routes routes = routesMapper.selectById(routeId);
        return Result.success(routes.getSum());
    }

    //根据线路名称查线路
    @GetMapping("/find")
    public Result<?> findById(@RequestParam Integer routeId){
        Routes routes = routesMapper.selectById(routeId);
        return Result.success(routes);
    }

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(defaultValue = "") String routeId,
                              @RequestParam String startStation,
                              @RequestParam String endStation,
                              @RequestParam String sum) throws UnsupportedEncodingException {
        LambdaQueryWrapper<Routes> wrapper = Wrappers.<Routes>lambdaQuery();
        LambdaQueryWrapper<Routes> wrapper1 = Wrappers.<Routes>lambdaQuery();
        LambdaQueryWrapper<Routes> wrapper2 = Wrappers.<Routes>lambdaQuery();
        LambdaQueryWrapper<Routes> wrapper3 = Wrappers.<Routes>lambdaQuery();
        LambdaQueryWrapper<Routes> wrapper4 = Wrappers.<Routes>lambdaQuery();

        if(StrUtil.isNotBlank(routeId)){
            routeId = URLDecoder.decode(routeId,"utf-8");
            wrapper1 = wrapper.like(Routes::getRouteId,routeId);
        }else{
            wrapper1 = wrapper;
        }

        if(StrUtil.isNotBlank(startStation)){
            wrapper2 = wrapper1.eq(Routes::getStartStation,Integer.parseInt(startStation));
        }else{
            wrapper2 = wrapper1;
        }

        if(StrUtil.isNotBlank(endStation)){
            wrapper3 = wrapper2.eq(Routes::getEndStation,Integer.parseInt(endStation));
        }else{
            wrapper3 = wrapper2;
        }

        if(StrUtil.isNotBlank(sum)){
            wrapper4 = wrapper3.eq(Routes::getSum,Integer.parseInt(sum));
        }else{
            wrapper4 = wrapper3;
        }

        Page<Routes> routesPage = routesMapper.selectPage(new Page<>(pageNum, pageSize), wrapper4);
        return Result.success(routesPage);
    }

    //新增
    @PostMapping
    public Result<?> save(@RequestBody Routes routes) throws UnsupportedEncodingException {
        //线路号重复
        String routeId = URLDecoder.decode(routes.getRouteId(),"utf-8");
        Routes res = routesMapper.selectOne(Wrappers.<Routes>lambdaQuery()
                   .eq(Routes::getRouteId,routeId));
        if(res != null){
            return Result.error("-1","线路号重复");
        }
        routes.setRouteId(routeId);
        if(routes.getSum()==null){
            routes.setSum(2);
        }
        routesMapper.insert(routes);
        return Result.success(routes.getId());
    }

    //地图新增
    @GetMapping("/map")
    public Result<?> insertMap(@RequestParam(defaultValue = "") String routeId, @RequestParam Integer startStation,
                               @RequestParam Integer endStation, @RequestParam Integer sum) throws UnsupportedEncodingException {
        String routeId1 = URLDecoder.decode(routeId,"utf-8");
        Routes res = routesMapper.selectOne(Wrappers.<Routes>lambdaQuery()
                .eq(Routes::getRouteId,routeId1));
        if(res != null){
            return Result.error("-1","线路号重复");
        }else{
            Routes routes = new Routes();
            routes.setRouteId(routeId);
            routes.setStartStation(startStation);
            routes.setEndStation(endStation);
            routes.setSum(sum);
            routesMapper.insert(routes);
            return Result.success(routes.getId());
        }
    }

    //编辑
    @PutMapping
    public Result<?> update(@RequestBody Routes routes) throws UnsupportedEncodingException{
        //线路号重复
        String routeId = URLDecoder.decode(routes.getRouteId(),"utf-8");
        routes.setRouteId(routeId);
        routesMapper.updateById(routes);
        return Result.success(routes);
    }

    //在线路详情页中编辑
    @GetMapping("/detail")
    public Result<?> updateDetail(@RequestParam Integer routeId,@RequestParam Integer count,@RequestParam Integer stationId){
        Routes res = routesMapper.selectOne(Wrappers.<Routes>lambdaQuery().eq(Routes::getId,routeId));
        if(count == 1){
            if(stationId==0){
                return Result.error("-1","线路要有始发站");
            }else{
                res.setStartStation(stationId);
                routesMapper.updateById(res);
            }
        }else if(count == res.getSum()){
            if(stationId==0){
                return Result.error("-1","线路要有终点站");
            }else{
                res.setEndStation(stationId);
                routesMapper.updateById(res);
            }
        }
        return Result.success();
    }

    //增加一站
    @GetMapping("/add")
    public Result<?> add(@RequestParam Integer routeId){
        Routes routes = routesMapper.selectById(routeId);
        int sum = routes.getSum();
        routes.setSum(sum+1);
        routesMapper.updateById(routes);
        return Result.success(sum+1);
    }

    //减少一站
    @GetMapping("/minus")
    public Result<?> minus(@RequestParam Integer routeId){
        Routes routes = routesMapper.selectById(routeId);
        int sum = routes.getSum();
        if(sum==2){
            return Result.error("-1","一条线路最少有两站");
        }else{
            routes.setSum(sum-1);
            routesMapper.updateById(routes);
            return Result.success(sum-1);
        }
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable long id){
        try {
            routesMapper.deleteById(id);
        }catch (Exception e){
            return Result.error("-1","该线路被分配工作，无法删除");
        }
        return Result.success();
    }

    //初始化下拉框
    @GetMapping("/initial")
    public Result<?> initial(){
        LambdaQueryWrapper<Routes> wrapper = Wrappers.<Routes>lambdaQuery();
        List<Routes> list = routesMapper.selectList(wrapper.ne(Routes::getRouteId,""));
        return Result.success(list);
    }

}
