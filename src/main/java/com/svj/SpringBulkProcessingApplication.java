package com.svj;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringBulkProcessingApplication {

	@PostMapping("/importCusotmers-info")
	public ResponseEntity<?> importCustomerInfo2DB(){
		JobParameters jobParameters= new JobParametersBuilder()
				.addLong("startAt", System.currentTimeMillis()).toJobParameters();
		try {
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("Job executed successfully!");
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBulkProcessingApplication.class, args);
	}

	@Autowired
	private Job job;
	@Autowired
	private JobLauncher jobLauncher;
}
