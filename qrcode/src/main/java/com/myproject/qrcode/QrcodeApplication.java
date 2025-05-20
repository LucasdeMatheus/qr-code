package com.myproject.qrcode;

import com.myproject.qrcode.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrcodeApplication {
	public static void main(String[] args) {
		SpringApplication.run(QrcodeApplication.class, args);
	}

}
