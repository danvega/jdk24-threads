package dev.danvega.threads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Virtual Threads demo.
 * This application demonstrates how virtual threads work with synchronized methods.
 * 
 * Virtual threads are enabled via the property configuration:
 * spring.threads.virtual.enabled=true
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}