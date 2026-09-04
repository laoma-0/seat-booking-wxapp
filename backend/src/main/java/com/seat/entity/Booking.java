package com.seat.entity;

import java.time.LocalTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.time.LocalDate;

import lombok.Data;

@Data
@TableName("t_booking")
public class Booking {
    @TableId(type = IdType.AUTO)
    private Long bookingId;

    private String userId;

    private Long seatId;

    private Long areaId;

    private Integer status;

    private LocalDate bookDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
