package com.example.plants;


import com.example.plants.statistics.Statistics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class PlantsApplication {

	
	
	private static void run() {
		System.out.println("Get: " +Statistics.getInstance().getGetMappingCounter());
		System.out.println("Delete: " +Statistics.getInstance().getDeleteMappingCounter());
		System.out.println("Post: "+ Statistics.getInstance().getPostMappingCounter());
		System.out.println("Put: "+ Statistics.getInstance().getPutMappingCounter());
		Statistics.getInstance().writeIntoLogger();
		
	
    }

	public static void main(String[] args) {
		SpringApplication.run(PlantsApplication.class, args);

		ScheduledExecutorService executorService;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(PlantsApplication::run, 0, 60, TimeUnit.SECONDS);
		



	}

}
