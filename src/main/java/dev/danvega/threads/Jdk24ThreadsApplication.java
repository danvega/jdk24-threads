package dev.danvega.threads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the JDK 24 Virtual Threads demo.
 * This application demonstrates how JDK 24 allows virtual threads to use
 * synchronized methods without pinning to platform threads during blocking operations.
 * 
 * Virtual threads are enabled via the property configuration:
 * spring.threads.virtual.enabled=true
 */
@SpringBootApplication
public class Jdk24ThreadsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Jdk24ThreadsApplication.class, args);
	}
}
