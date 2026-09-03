package com.seat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_area")
public class Area {
    @TableId(type = IdType.AUTO)
    private Long areaId;
    private Long venueId;
    private String areaName;
    private String floor;
}
