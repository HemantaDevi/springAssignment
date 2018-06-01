package com.accenture.carrental.hemanta.devi.huril.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.accenture.carrental.hemanta.devi.huril.api.dtos.CarDTO;
import com.accenture.carrental.hemanta.devi.huril.api.dtos.CarRentalDto;
import com.accenture.carrental.hemanta.devi.huril.entities.Car;
import com.accenture.carrental.hemanta.devi.huril.entities.CarRental;
import com.accenture.carrental.hemanta.devi.huril.entities.User;
import com.accenture.carrental.hemanta.devi.huril.exceptions.CarRegNumAlreadyExistException;
import com.accenture.carrental.hemanta.devi.huril.exceptions.InvalidDateException;
import com.accenture.carrental.hemanta.devi.huril.services.CarService;
import com.accenture.carrental.hemanta.devi.huril.services.RentalService;
import com.accenture.carrental.hemanta.devi.huril.services.UserService;

@Controller
public class CarController {
	private final CarService carService;
	private final RentalService rentalService;
	private final UserService userService;

	public CarController(CarService carService, RentalService rentalService, UserService userService) {
		this.carService = carService;
		this.rentalService = rentalService;
		this.userService = userService;
	}

	@GetMapping("login")
	public String login() {
		return "login";
	}

	/*
	 * @GetMapping("/") public String indexPage() { return "admin"; }
	 * 
	 * @GetMapping("/customer") public String indexPageCustomer() { return
	 * "customer"; }
	 */

	@Secured("ROLE_ADMIN")
	@GetMapping("/showAll")
	public String ListOfCars(Model model, HttpServletRequest request) {
		List<Car> cars = carService.ListAllCars();
		model.addAttribute("cars", cars);
		String hostNport = request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("hostNport", hostNport);
		return "allCars";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping("/create")
	public String InsertCar() {
		return "create";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/creating")
	public String createCar(@RequestParam("registrationNumber") String registrationNumber,
			@RequestParam("carModel") String carModel, @RequestParam("pricePerDay") double pricePerDay,
			@RequestParam("year") int year, Model model, HttpServletRequest request) {
		CarDTO dto = new CarDTO();
		dto.setRegistrationNumber(registrationNumber);
		dto.setModel(carModel);
		dto.setPricePerDay(pricePerDay);
		dto.setYear(year);

			try {
				carService.addCar(dto);
			} catch (CarRegNumAlreadyExistException e) {
					model.addAttribute("error", e.getMessage());
				return "create";
			}

		model.addAttribute("cars", carService.ListAllCars());
		String hostNport = request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("hostNport", hostNport);
		return "allCars";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/update/{registrationNumber}")
	public String updateCar(@PathVariable String registrationNumber, Model model) {
		Car car = carService.findByRegistrationNumber(registrationNumber);
		model.addAttribute("cars", car);
		return "update";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/updating")
	public String updating(@RequestParam("registrationNumber") String registrationNumber,
			@RequestParam("carModel") String carModel, @RequestParam("pricePerDay") double pricePerDay,
			@RequestParam("year") int year, Model model, HttpServletRequest request) {
		Car car = new Car();
		car.setPricePerDay(pricePerDay);
		car.setYear(year);
		car.setRegistrationNumber(registrationNumber);
		carService.updateCar(car);
		model.addAttribute("cars", carService.ListAllCars());
		String hostNport = request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("hostNport", hostNport);
		return "allCars";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/delete/{registrationNumber}")
	public String deleteCar(@PathVariable String registrationNumber, Model model,HttpServletRequest request) {
		if(carService.deleteCar(registrationNumber)==-1) {
			model.addAttribute("errMsg","Car is rented. Cannot be deleted");
			//do not redirect
		}
		String hostNport = request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("hostNport", hostNport);
			model.addAttribute("cars", carService.ListAllCars());
			return "allCars";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/registration")
	public String getACarByItsRegistrationNumber() {
		return "carFromRegistration";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/searching")
	public String searchingTheCar(@RequestParam("registrationNumber") String registrationNumber, Model model, HttpServletRequest request) {
		Car car = carService.findByRegistrationNumber(registrationNumber);
		if (car != null) {
			List<Car> cars = new ArrayList<>();
			cars.add(car);
			model.addAttribute("cars", cars);
			String hostNport = request.getServerName() + ":" + request.getServerPort();
			model.addAttribute("hostNport", hostNport);
			return "allCars";
		} else {
			return "carFromRegistration";
		}
	}

	// list of car available for rent
	@Secured("ROLE_ADMIN")
	@GetMapping("/available")
	public String available(Model model) {
		List<Car> availableList = carService.findByAvailability();
		model.addAttribute("availabilityList", availableList);
		model.addAttribute("errMsg", "");
		return "available";
	}

	// allocate a car to a customer for a specific period
	@Secured("ROLE_ADMIN")
	@GetMapping("/allocate/{registrationNumber}")
	public String allocateCar(@PathVariable String registrationNumber, Model model) {
		model.addAttribute("car", carService.findByRegistrationNumber(registrationNumber));
		model.addAttribute("users", userService.findAllUsers());
		return "allocate";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/allocating")
	public String allocateCartoCustomer(@RequestParam("registrationNumber") String registrationNumber,
			@RequestParam("pricePerDay") double pricePerDay, @RequestParam("user") String nationalId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, Model model)
			throws InvalidDateException {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		// if else to check whether start is before end, if not throw exception
		if (start.isAfter(end)) {
			List<Car> availableList = carService.findByAvailability();
			model.addAttribute("availabilityList", availableList);
			model.addAttribute("errMsg", "START DATE SHOULD BE BEFORE END DATE");
			return "available";
		}
		Car car = carService.findByRegistrationNumber(registrationNumber);
		User user = userService.findUserByNationalId(nationalId);
		CarRental carRental = new CarRental();
		carRental.setStartDate(start);
		carRental.setEndDate(end);
		carRental.setCar(car);
		carRental.setUser(user);
		carRental.setPricePerDay(pricePerDay);
		carRental.setReturned(false);
		rentalService.allocateCar(carRental);
		List<Car> availableList = carService.findByAvailability();
		model.addAttribute("availabilityList", availableList);
		return "available";
	}

	// car rented for a specific period
	@Secured("ROLE_ADMIN")
	@GetMapping("/rentedCar")
	public String carRented() {
		return "rentedCars";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/listRent")
	public String getListOfCarRented(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, Model model1) {
		LocalDate start = LocalDate.parse(startDate).minusDays(1);
		LocalDate end = LocalDate.parse(endDate).plusDays(1);
		List<CarRentalDto> carRented = new ArrayList<>();
		List<CarRental> carRental = rentalService.RentedForSpecificPeriod();
		for (CarRental carRental2 : carRental) {
			if (start.isBefore(carRental2.getStartDate()) && end.isAfter(carRental2.getEndDate())) {
				CarRentalDto dto = new CarRentalDto();
				dto.setRegistrationNumber(carRental2.getCar().getRegistrationNumber());
				dto.setCarModel(carRental2.getCar().getModel());
				dto.setPricePerDay(carRental2.getPricePerDay());
				dto.setYear(carRental2.getCar().getYear());
				dto.setStartDate(carRental2.getStartDate());
				dto.setEndDate(carRental2.getEndDate());
				carRented.add(dto);
			}
		}
		model1.addAttribute("availabilityList", carRented);
		return "listOfRentedCars";
	}

}