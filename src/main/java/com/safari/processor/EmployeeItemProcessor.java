package com.safari.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.safari.model.Employee;

@Component
public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(final Employee employee) throws Exception {
		
		String firstName = employee.getFirstName().toUpperCase();
		String lastName = employee.getLastName().toUpperCase();
		
		final Employee transformedEmployee = new Employee(
				employee.getId(),
				firstName,
				lastName,
				employee.getEmailId(),
				employee.getGender()
			);
		
		//System.out.println("Employee - >>"+transformedEmployee);
		return transformedEmployee;
	}
}
