package com.sharkio.backend;

import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.WorldRepository;
import com.sharkio.backend.service.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
