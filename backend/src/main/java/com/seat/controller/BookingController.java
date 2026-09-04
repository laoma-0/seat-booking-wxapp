package com.seat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/my")
    public List<BookingVO>  myBookings(HttpServletRequest req) {
       String userId = (String) req.getAttribute("openid");
        return bookingMapper.selectMyBookings(userId);
    }


}


