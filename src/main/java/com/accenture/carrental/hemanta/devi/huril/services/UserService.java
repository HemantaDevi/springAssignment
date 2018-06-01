package com.accenture.carrental.hemanta.devi.huril.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.accenture.carrental.hemanta.devi.huril.entities.User;
import com.accenture.carrental.hemanta.devi.huril.entities.enums.RoleType;
import com.accenture.carrental.hemanta.devi.huril.exceptions.NotInsertedException;
import com.accenture.carrental.hemanta.devi.huril.repositories.UserRepository;

@Transactional
@Service
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	public List<User> findAllCustomers(RoleType role){
		return userRepository.findByRole(role);
	}

	public List<User> findAllUsers(){
		List<User> user = new ArrayList<>();
		userRepository.findAll().forEach(user::add);
		return user;
	}
	public User findUserByNationalId(String nationalId) {
		return userRepository.findOneByNationalId(nationalId);
	}
	
	public User createCustomer(User user) throws NotInsertedException {
		if(userRepository.findOneByNationalId(user.getNationalId()) != null) {
			throw new NotInsertedException("Customer already exist");
		}else {
		user.setRole(RoleType.CUSTOMER);
		return userRepository.save(user);
		}
	}
	
	public User CreateAdmin(User user) throws NotInsertedException {
		if(userRepository.findOneByNationalId(user.getNationalId()) != null) {
			throw new NotInsertedException("Admin already exist");
		}else {
		user.setRole(RoleType.ADMIN);
		return userRepository.save(user);
		}
}
	
	public User updateUser(User user) {
		if(userRepository.findOneByNationalId(user.getNationalId()) != null) {
			User user1 = userRepository.findOneByNationalId(user.getNationalId());
			user1.setName(user.getName());
			return userRepository.save(user1);
		}
		return null;	
	}

	public int deleteUser(String nationalId) {
		return userRepository.deleteByNationalId(nationalId);
	}
	
	@PostConstruct
	public void initialise() {
		if (findAllCustomers(RoleType.ADMIN).isEmpty()) {

			User user2 = new User();
			user2.setNationalId("Brandi");
			user2.setName("Brandi");
			user2.setPassword(bCryptPasswordEncoder.encode("54321"));
			user2.setRole(RoleType.ADMIN);
			user2.setSex("Male");
			user2.setDateOfBirth(LocalDate.of(1993, 03, 22));

			try {
				CreateAdmin(user2);
			} catch (NotInsertedException e) {
				e.printStackTrace();
			}
			
		}

		if (findAllCustomers(RoleType.CUSTOMER).isEmpty()) {

			User user1 = new User();
			user1.setNationalId("James");
			user1.setName("James Milly");
			user1.setPassword(bCryptPasswordEncoder.encode("12345"));
			user1.setRole(RoleType.CUSTOMER);
			user1.setSex("Male");
			user1.setDateOfBirth(LocalDate.of(1990, 12, 22));

			try {
				createCustomer(user1);
			} catch (NotInsertedException e) {
				e.printStackTrace();
			}
		}
	}
}
