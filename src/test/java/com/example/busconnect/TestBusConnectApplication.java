package com.example.busconnect;

import org.springframework.boot.SpringApplication;

public class TestBusConnectApplication {

	public static void main(String[] args) {
		SpringApplication.from(BusConnectApplication::main).with(AbstractRepositoryTest.class).run(args);
	}

}
