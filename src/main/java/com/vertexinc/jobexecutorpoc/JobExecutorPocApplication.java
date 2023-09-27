package com.vertexinc.jobexecutorpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobExecutorPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobExecutorPocApplication.class, args);
    }

}
