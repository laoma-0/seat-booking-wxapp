package com.seat.controller;

import java.util.List;


import org.springframework.web.bind.annotation.*;


import com.seat.entity.Seat;
import com.seat.mapper.SeatMapper;

@RestController
@RequestMapping ("/api/seat")
public class SeatController {
       private final SeatMapper seatMapper;
       public SeatController(SeatMapper seatMapper) {
              this.seatMapper = seatMapper;
       }
       @GetMapping ("/list")
       public List<Seat> list(@RequestParam  Long areaId) {
              List<Seat> seats = seatMapper.selectByAreaId(areaId);
              return seats;
       }
}
