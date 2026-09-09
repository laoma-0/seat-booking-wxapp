package com.seat.vo;

import com.seat.entity.Area;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 阅览区 + 余位统计（查询结果载体，不对应任何表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AreaVO extends Area {
    /** 可用座位总数（排除损坏 status=2） */
    private Long totalSeats;
    /** 指定时段仍可预约的座位数 */
    private Long availableSeats;
}
