package com.example.autofixrepaircost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AutofixRepairCostApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutofixRepairCostApplication.class, args);
	}

}
