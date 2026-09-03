package com.seat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seat.mapper.AreaMapper;

@RestController 
@RequestMapping ("/api/area")
public class AreaController {
   @Autowired 
   private AreaMapper areaMapper;

   @GetMapping ("/list")
    public Object list() {
         return areaMapper.selectList(null);
    }
}
