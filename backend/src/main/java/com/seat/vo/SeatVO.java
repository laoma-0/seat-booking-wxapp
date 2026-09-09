package com.seat.vo;

import com.seat.entity.Seat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 座位 + 该时段占用情况 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeatVO extends Seat {
    /** 所选时段是否已被预约：true = 不可选 */
    private Boolean occupied;
}
