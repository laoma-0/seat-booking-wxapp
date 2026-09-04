package com.seat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import com.seat.mapper.SeatMapper;
import com.seat.vo.BookingVO;
import com.seat.mapper.BookingMapper;
import com.seat.entity.Booking;
import com.seat.entity.Seat;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private SeatMapper seatMapper;
     public BookingController(BookingMapper bookingMapper, SeatMapper seatMapper) {
        this.bookingMapper = bookingMapper;
        this.seatMapper = seatMapper;
    }
    // 创建预约
   @PostMapping("/create")
    public String createBooking(@RequestBody Booking booking, HttpServletRequest req) {
       String userId = (String) req.getAttribute("openid");
       booking.setUserId(userId);

       Seat seat = seatMapper.selectById(booking.getSeatId());
       if (seat == null || seat.getStatus() != 0) {
          return "座位不可预约";
        }
      booking.setStatus(0);
      try {
        bookingMapper.insert(booking);
        return "预约成功";
      } catch (DuplicateKeyException e) {
        return "该时段已被预约";
      }

    }
    // 获取我的预约
    @GetMapping("/my")
    public List<BookingVO>  myBookings(HttpServletRequest req) {
       String userId = (String) req.getAttribute("openid");
        return bookingMapper.selectMyBookings(userId);
    }
    //签到预约
    @PostMapping("/sign")
    public String signBooking(@RequestBody Booking booking, HttpServletRequest req) {
       String userId = (String) req.getAttribute("openid");
       Booking dbBooking = bookingMapper.selectById(booking.getBookingId());
       if(dbBooking == null) {
          return "预约不存在";
        }
        if(!userId.equals(dbBooking.getUserId())) {
          return "您不是该预约的用户,无权限签到";
        }
        if(dbBooking.getStatus() != 0) {
          return "该预约无需签到";
        }
        //时间校验
        LocalDateTime now = LocalDateTime.now();
       LocalDateTime start = LocalDateTime.of(dbBooking.getBookDate(), dbBooking.getStartTime())
                                   .minusMinutes(30);   // 开始前 30 分钟开放签到

        LocalDateTime end=LocalDateTime.of(dbBooking.getBookDate(), dbBooking.getEndTime());
       
        if(now.isBefore(start) ) {
          return "预约时间未到,无法签到";
        }
        if(now.isAfter(end)) {
          return "预约时间已过期,无法签到";
        }
       dbBooking.setStatus(1);
       bookingMapper.updateById(dbBooking);
       try {
        return "签到成功";
      } catch (Exception e) {
        return "签到失败";
      }
    }
    @PostMapping("/cancel")
    public String cancelBooking(@RequestBody Booking booking, HttpServletRequest req) {
       String userId = (String) req.getAttribute("openid");
       Booking dbBooking = bookingMapper.selectById(booking.getBookingId());
       if(dbBooking == null) {
          return "预约不存在";
        }
        if(!userId.equals(dbBooking.getUserId())) {
          return "您不是该预约的用户,无权限取消";
        }
        if(dbBooking.getStatus() != 0) {
          return "该预约无需取消";
        }
        //时间校验
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(dbBooking.getBookDate(), dbBooking.getStartTime());
         if(now.isAfter(start)) {
          return "预约时间已开始,无法取消";
        }
        //更新预约状态
        dbBooking.setStatus(4);
        dbBooking.setUpdateTime(LocalDateTime.now());  
        bookingMapper.updateById(dbBooking);
        try {
          return "取消成功";
        } catch (Exception e) {
          return "取消失败";
        }
    }


}


