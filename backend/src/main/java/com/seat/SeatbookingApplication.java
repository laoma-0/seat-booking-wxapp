package com.seat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan ("com.seat.mapper")
public class SeatbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatbookingApplication.class, args);
	}

}
