package com.svj.config;

import com.svj.entity.Customer;
import com.svj.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.FileNotFoundException;

@Configuration
@EnableBatchProcessing
public class ApplicationBatchConfig {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<Customer> itemReader(){

        FlatFileItemReader<Customer> itemReader= new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("C:\\Users\\svjra\\Documents\\git\\Springboot\\spring-bulk-processing\\src\\main\\resources\\data\\MOCK_DATA_Errored.csv"));
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper= new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer= new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob","age");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper= new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CustomerDataProcessor processor(){
        return new CustomerDataProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> itemWriter(){
        RepositoryItemWriter<Customer> itemWriter= new RepositoryItemWriter<>();
        itemWriter.setRepository(repository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public Step importCustomerStep(){
        return stepBuilderFactory.get("ImportCustomerStep").<Customer, Customer>chunk(10) // name should match name of bean- use camel casing
                .reader(itemReader())
                .processor(processor())
                .writer(itemWriter())
//                .taskExecutor(taskExecutor())
                .faultTolerant()
                .skipPolicy(skipPolicy())
                .listener(skipListener())
//                .skipLimit(100)
//                .skip(NumberFormatException.class)
//                .noSkip(FileNotFoundException.class)
                .build();
    }

    @Bean
    public Job runJob(){
        return jobBuilderFactory.get("ImportCustomersJob")
                .flow(importCustomerStep())
                .end().build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor= new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }

    @Bean
    public SkipPolicy skipPolicy(){
        return new CustomSkipPolicy();
    }

    @Bean
    public SkipListener skipListener(){
        return new BatchStepEventListener();
    }
}
