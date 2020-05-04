package com.safari.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.safari.model.Employee;

@Component
public class EmployeeFieldSetMapper implements FieldSetMapper<Employee>{

	@Override
	public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
		
		Employee employee = new Employee();
		employee.setId(fieldSet.readInt(0));
		employee.setFirstName(fieldSet.readString("firstname"));
		employee.setLastName(fieldSet.readString("lastname"));
		employee.setEmailId(fieldSet.readString("email"));
		employee.setGender(fieldSet.readString("gender"));
		return employee;
	}
}
