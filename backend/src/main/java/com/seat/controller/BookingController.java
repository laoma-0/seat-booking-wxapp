package com.seat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;

import com.seat.mapper.BookingMapper;
import com.seat.entity.Booking;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingMapper bookingMapper;
     public BookingController(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }
    // 创建预约
    @PostMapping("/create")
    public Booking createBooking(@RequestBody Booking booking, HttpServletRequest req) {
        String userId = (String) req.getAttribute("openid");
        booking.setUserId(userId);
        booking.setStatus(0);
        bookingMapper.insert(booking);
        return booking;
    }

}


