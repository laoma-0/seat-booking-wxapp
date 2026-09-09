package com.seat.controller;

import java.util.List;


import org.springframework.web.bind.annotation.*;


import com.seat.entity.Seat;
import com.seat.mapper.SeatMapper;
import com.seat.vo.SeatVO;

@RestController
@RequestMapping ("/api/seat")
public class SeatController {
       // 座位Mapper
       private final SeatMapper seatMapper;
       public SeatController(SeatMapper seatMapper) {
              this.seatMapper = seatMapper;
       }
       // 查询指定区域的所有座位；若传入时段参数则附带该时段占用情况
       @GetMapping ("/list")
       public Object list(@RequestParam Long areaId,
                          @RequestParam(required = false) String bookDate,
                          @RequestParam(required = false) String startTime,
                          @RequestParam(required = false) String endTime) {
              if (bookDate != null && startTime != null && endTime != null) {
                     return seatMapper.selectByAreaId(areaId, bookDate, startTime, endTime);
              }
              return seatMapper.selectSeatsByAreaId(areaId);
       }

}
