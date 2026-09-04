package com.seat.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class BookingVO {
    private Long bookingId;
    private Long seatId;
    private Long areaId;
    private Integer status;
    private LocalDate bookDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createTime;
    private String seatNo;     // 来自 t_seat
    private String areaName;   // 来自 t_area
}
