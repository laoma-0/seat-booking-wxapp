package com.seat.entity;

import java.time.LocalDateTime;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;



@Data 
@TableName("t_seat")
public class Seat {
    @TableId (type = IdType.AUTO)
    private Long seatId;
    private Long areaId;
    private String seatNo;
    private Integer hasSocket;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
