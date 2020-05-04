/***
 *  
 *  Batch configuration for various business concerns
 * 
 * */

package com.safari.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	 private final Logger logger = LoggerFactory.getLogger(this.getClass());

	 /**
	 * 
	 * */
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	// file location mentioned in properties files
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
	
	/**
	 *  This method will create a job for importing the Employee Data
	 *  @return		Job Object 
	 * */
	@Bean
	public Job importEmployeeData(){
		
		logger.info("Creating Job from JobFactory for employee data...");
		
		Job job = jobBuilderFactory.get("importEmployeeData")
			.incrementer(new RunIdIncrementer())
			.start(fileLoadStep())
			.build();
		
		return job;
	}
	
	/**
	 *  This method will create step for loading the employee csv, and distribute the work in chunk of 100 records
	 *  with each have separate ItemReader, ItemProcessor and ItemWriter.
	 *  
	 *  @return		Step Object
	 * */
	@Bean
	public Step fileLoadStep(){
		
		logger.info("Creating Step for employee data...");
		
		Step step = stepBuilderFactory.get("file-load")
				.<Employee,Employee>chunk(100)
				.reader(reader())
				.processor(employeeProcessor())
				.writer(employeeWriter()) 
				.build();
		
		return step;
	}
	
	/**
	 * 	This method is for the Reader implementation, used to read the data stored in the file as mentioned in the filepath object
	 *  and named it to Employee Item Reader 
	 *  
	 *  @return		FlatFileItemReader object
	 * */
	@Bean
	public FlatFileItemReader<Employee> reader(){
		
		logger.info("Reading the employee csv file...");
		
		FlatFileItemReader<Employee> flatFileItemReader = new FlatFileItemReader<Employee>();
		flatFileItemReader.setResource(new FileSystemResource(filePath));
		flatFileItemReader.setName("EmployeeItemReader");
		
		// this is for the first line which is generally the header in most of the cases
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
		
	}
	
	/**
	 * 	This method is used for mapping the lines from the CSV to Employee object
	 * 
	 * 	@return		LineMapper object
	 * */
	@Bean
	public LineMapper<Employee> lineMapper() {
		
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
		
		DelimitedLineTokenizer lineToken = new DelimitedLineTokenizer();
		
		// here in the csv the delimiter used is comma 
		lineToken.setDelimiter(",");
		lineToken.setStrict(false);
		
		// various fields in csv file
		String [] tokens = {"id","firstname","lastname","email","gender"};
		lineToken.setNames(tokens);
		
		lineMapper.setLineTokenizer(lineToken);
		lineMapper.setFieldSetMapper(new EmployeeFieldSetMapper());
		
		return lineMapper;
	}	
}