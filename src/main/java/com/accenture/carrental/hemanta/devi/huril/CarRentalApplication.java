package com.accenture.carrental.hemanta.devi.huril;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalApplication{

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

/*	@Bean
	public CommandLineRunner initialize(UserService userService, CarRepository carRepository) {
		return (args) -> {
			System.out.println(userService.findAllCustomers(RoleType.ADMIN));
			
			 * // Initialize database if empty Car car = new Car(); car.setId(2);
			 * car.setModel("Audi"); car.setRegistrationNumber("14A2015");
			 * car.setYear(2018); car.setPricePerDay(230.00); carRepository.save(car);
			 * System.out.println("Initializer");
			 



		};
	}*/
}
