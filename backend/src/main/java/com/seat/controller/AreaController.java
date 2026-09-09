package com.seat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seat.mapper.AreaMapper;

@RestController
@RequestMapping("/api/area")
public class AreaController {
   @Autowired
   private AreaMapper areaMapper;

   /**
    * 阅览区列表。传入 bookDate/startTime/endTime 时附带该时段余位统计，否则只返回基础信息。
    */
   @GetMapping("/list")
   public Object list(@RequestParam(required = false) String bookDate,
                      @RequestParam(required = false) String startTime,
                      @RequestParam(required = false) String endTime) {
      if (bookDate != null && startTime != null && endTime != null) {
         return areaMapper.selectAreaStats(bookDate, startTime, endTime);
      }
      return areaMapper.selectList(null);
   }
}
