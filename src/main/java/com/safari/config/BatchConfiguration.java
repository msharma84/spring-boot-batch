package com.safari.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.safari.model.Employee;
import com.safari.processor.EmployeeItemProcessor;
import com.safari.reader.EmployeeFieldSetMapper;
import com.safari.writer.EmployeeDBWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	@Value("${employee.batch.file}")
	private String filePath;
	
	@Bean
	public EmployeeItemProcessor employeeProcessor(){
		return new EmployeeItemProcessor();
	}
	
	@Bean
	public EmployeeDBWriter employeeWriter(){
		return new EmployeeDBWriter();
	}
	
	@Bean
	public Job importEmployeeData(){
		
		Job job = jobBuilderFactory.get("importEmployeeData")
			.incrementer(new RunIdIncrementer())
			.start(fileLoadStep())
			.build();
		
		return job;
	}
	
	@Bean
	public Step fileLoadStep(){
		
		Step step = stepBuilderFactory.get("file-load")
				.<Employee,Employee>chunk(100)
				.reader(reader())
				.processor(employeeProcessor())
				.writer(employeeWriter()) 
				.build();
		
		return step;
	}
	
	@Bean
	public FlatFileItemReader<Employee> reader(){
		
		FlatFileItemReader<Employee> flatFileItemReader = new FlatFileItemReader<Employee>();
		flatFileItemReader.setResource(new FileSystemResource(filePath));
		flatFileItemReader.setName("EmployeeItemReader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
		
	}

	@Bean
	public LineMapper<Employee> lineMapper() {
		
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
		
		DelimitedLineTokenizer lineToken = new DelimitedLineTokenizer();
		lineToken.setDelimiter(",");
		lineToken.setStrict(false);
		String [] tokens = {"id","firstname","lastname","email","gender"};
		lineToken.setNames(tokens);
		
		lineMapper.setLineTokenizer(lineToken);
		lineMapper.setFieldSetMapper(new EmployeeFieldSetMapper());
		
		return lineMapper;
	}	
}