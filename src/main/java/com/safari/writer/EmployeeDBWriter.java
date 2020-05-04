package com.safari.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.safari.model.Employee;
import com.safari.repository.EmployeeRepository;

@Component
public class EmployeeDBWriter implements ItemWriter<Employee> {

	@Autowired
	EmployeeRepository employeeRepo;
	
	@Override
	public void write(List<? extends Employee> employees) throws Exception {
		
		System.out.println("Saving the employees...");
		employeeRepo.saveAll(employees);
	}

}
