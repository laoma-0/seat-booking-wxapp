package com.seat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seat.entity.Area;
import com.seat.vo.AreaVO;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    /**
     * 各阅览区在指定时段的余位统计。
     * 占用判定与 SeatMapper 保持一致：t_booking 中存在 status IN (0,1) 且时段重叠的记录即视为占用。
     */
    @Select("SELECT a.*, " +
            "(SELECT COUNT(*) FROM t_seat s " +
            "  WHERE s.area_id = a.area_id AND s.status <> 2) AS total_seats, " +
            "(SELECT COUNT(*) FROM t_seat s " +
            "  WHERE s.area_id = a.area_id AND s.status <> 2 " +
            "    AND NOT EXISTS (SELECT 1 FROM t_booking b " +
            "        WHERE b.seat_id = s.seat_id " +
            "          AND b.book_date = #{bookDate} " +
            "          AND b.start_time < #{endTime} " +
            "          AND b.end_time   > #{startTime} " +
            "          AND b.status IN (0, 1))) AS available_seats " +
            "FROM t_area a " +
            "ORDER BY a.area_id")
    List<AreaVO> selectAreaStats(@Param("bookDate") String bookDate,
                                 @Param("startTime") String startTime,
                                 @Param("endTime") String endTime);
}
