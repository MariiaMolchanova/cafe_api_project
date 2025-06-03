package com.molchanova.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jmx.export.annotation.ManagedResource;

@SpringBootApplication
@ManagedResource
public class CourseApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CourseApplication.class);
		// Explicitly enable JMX and admin features
		System.setProperty("spring.jmx.enabled", "true");
		System.setProperty("spring.application.admin.enabled", "true");
		app.run(args);
	}

}
