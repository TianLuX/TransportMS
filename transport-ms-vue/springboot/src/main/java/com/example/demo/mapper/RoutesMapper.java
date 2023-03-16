package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Routes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoutesMapper extends BaseMapper<Routes> {
//    @Select("SELECT routes.id AS id,route_id,s1.station_name AS startName, s2.station_name AS endName,sum " +
//            "FROM station AS s1,station AS s2,routes " +
//            "WHERE s1.id = start_station AND s2.id = end_station " +
//            "AND route_id = #{route_id} AND start_station = #{start_station} AND end_station = #{end_station} AND sum = #{sum}")
//    List<Routes>(@Param("route_id") String route_id,@Param("start_station") int start_station,
//    @Param("end_station") int end_station,@Param("sum") int sum);
}
