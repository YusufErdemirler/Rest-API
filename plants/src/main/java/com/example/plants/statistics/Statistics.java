package com.example.plants.statistics;

import com.example.plants.logger.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    private AtomicInteger getMappingCounter =  new AtomicInteger(0);
    private AtomicInteger postMappingCounter = new AtomicInteger(0);
    private AtomicInteger putMappingCounter = new AtomicInteger(0);
    private AtomicInteger deleteMappingCounter = new AtomicInteger(0);


    private static Statistics instance;
    Log log1 = new Log();
    //Logger log1 = LogManager.getLogger(Statistics.class);



    public static Statistics getInstance() {
        if (instance == null)
            instance = new Statistics();

        return instance;
    }
    
    public void writeIntoLogger() {
        log1.info("Get: " +instance.getGetMappingCounter()+
                  " Delete: " + instance.getDeleteMappingCounter()+
                  " Post: "+ instance.getPostMappingCounter() +
                  " Put: "+ instance.getPutMappingCounter());

                  instance.setDeleteMappingCounter(0);
		          instance.setGetMappingCounter(0);
		          instance.setPostMappingCounter(0);
		          instance.setPutMappingCounter(0);
                  
        
    }

    public int getGetMappingCounter() {
        return getMappingCounter.get();
    }
    
    public void setGetMappingCounter(int getMappingCounter) {
        this.getMappingCounter.getAndSet(getMappingCounter);
    }
    
    public int getPostMappingCounter() {
        return postMappingCounter.get();
    }
    
    public void setPostMappingCounter(int postMappingCounter) {
        this.postMappingCounter.getAndSet(postMappingCounter);
     
    }
    
    public int getPutMappingCounter() {
        return putMappingCounter.get();
    }
     
    public void setPutMappingCounter(int putMappingCounter) {
        this.putMappingCounter.getAndSet(putMappingCounter);
    }
    
    public int getDeleteMappingCounter() {
        return deleteMappingCounter.get();
    }
     
    public void setDeleteMappingCounter(int deleteMappingCounter) {
        this.deleteMappingCounter.getAndSet(deleteMappingCounter);
    }
    
    //Another approach
   
    public void incrementGetCounter() {
        getMappingCounter.getAndIncrement();
       
    }

    public void incrementPostCounter() {
        postMappingCounter.getAndIncrement();
       
    }

    public void incrementPutCounter() {
        putMappingCounter.getAndIncrement();
       
    }

    public void incrementDeleteCounter() {
        deleteMappingCounter.getAndIncrement();
       
    }
    
    
}
