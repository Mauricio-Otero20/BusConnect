package com.example.busconnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(AbstractRepositoryTest.class)
@SpringBootTest
class BusConnectApplicationTests {

	@Test
	void contextLoads() {
	}

}
