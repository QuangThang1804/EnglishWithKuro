package com.hus.englishapp.kuro;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class KuroApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuroApplication.class, args);
	}
//
//	@PostConstruct
//	public void init() {
//		// Đặt múi giờ mặc định của ứng dụng
//		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//		System.out.println("Múi giờ hiện tại: " + TimeZone.getDefault().getID());
//
//		// Lấy thời gian hiện tại theo múi giờ Hồ Chí Minh
//		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//		String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//		System.out.println("Giờ hiện tại (Asia/Ho_Chi_Minh): " + formattedTime);
//	}
}
