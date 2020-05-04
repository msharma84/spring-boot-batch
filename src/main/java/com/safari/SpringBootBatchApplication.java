/***
 * 
 *  Spring Boot Main class for running this batch application to saves the csv files into 
 *  database tables and perform business concerns on them.
 * 
 * */
package com.safari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootBatchApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(SpringBootBatchApplication.class, args);
	}
}
