package com.example.plants.controller;

import com.example.plants.entities.Plant;
import com.example.plants.repository.PlantRepository;
import com.example.plants.statistics.Statistics;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class PlantController {

  private final PlantRepository plantRepository;

  @Autowired
  private final KafkaTemplate<String,String> kafkaTemplate;

  public PlantController(final PlantRepository plantRepository, KafkaTemplate<String, String> kafkaTemplate) {
    this.plantRepository = plantRepository;
    this.kafkaTemplate = kafkaTemplate;

  }
 // Log log = new Log();
  Logger log = LogManager.getLogger(PlantController.class);

  Statistics statistics = Statistics.getInstance();


  String topic = "plants.demo";

  @GetMapping(value = "/") 
  public String getPage() {
    //statistics.setGetMappingCounter(statistics.getGetMappingCounter()+1);
    statistics.incrementGetCounter();
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return "Welcome to plants collection "+username;
  }

  @GetMapping("/plants/search")
  public List<Plant> searchPlants(
    @RequestParam(name="hasFruit", required = false) Boolean hasFruit,
    @RequestParam(name="maxQuantity", required = false) Integer quantity
  ) {
    //statistics.setGetMappingCounter(statistics.getGetMappingCounter()+1);
    statistics.incrementGetCounter();
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.info(username+" search for the plant in terms of fruit or quantity");

    //json object
    JSONObject plantObject = new JSONObject();
  
    if (hasFruit != null && quantity != null && hasFruit) {

      try {
        plantObject.put("Operation", "Get");
        plantObject.put("Plants: ", this.plantRepository.findByHasFruitTrueAndQuantityLessThan(quantity));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

      return this.plantRepository.findByHasFruitTrueAndQuantityLessThan(quantity);

    }
    if (hasFruit != null && quantity != null && !hasFruit) {

      try {
        plantObject.put("Operation", "Get");
        plantObject.put("Plants: ", this.plantRepository.findByHasFruitFalseAndQuantityLessThan(quantity));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

      return this.plantRepository.findByHasFruitFalseAndQuantityLessThan(quantity);
    }
    if (hasFruit != null && hasFruit) {

      try {
        plantObject.put("Operation", "Get");
        plantObject.put("Plants: ", this.plantRepository.findByHasFruitTrue());
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

      return this.plantRepository.findByHasFruitTrue();
    }
    if (hasFruit != null && !hasFruit) {

      try {
        plantObject.put("Operation", "Get");
        plantObject.put("Plants: ", this.plantRepository.findByHasFruitFalse());
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

      return this.plantRepository.findByHasFruitFalse();
    }
    if (quantity != null) {

      try {
        plantObject.put("Operation", "Get");
        plantObject.put("Plants: ", this.plantRepository.findByQuantityLessThan(quantity));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

      return this.plantRepository.findByQuantityLessThan(quantity);
    }

    //kafka producer
    kafkaTemplate.send(topic,plantObject.toString());
    return new ArrayList<>();
  }

  @GetMapping("/plants")
  public Iterable<Plant> getAllPlants() {
    //statistics.setGetMappingCounter(statistics.getGetMappingCounter()+1);
    statistics.incrementGetCounter();
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.info(username+ " look for all the plants in the database");

    //kafka producer
    JSONObject plantObject = new JSONObject();
    try {
      plantObject.put("operation", "Get");
      plantObject.put("plants: ", this.plantRepository.findAll());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    kafkaTemplate.send(topic,plantObject.toString());
    return this.plantRepository.findAll();

  }
  
  @GetMapping("/plants/{id}")
  public Optional<Plant> getPlantById(@PathVariable("id") Integer id) {
    //statistics.setGetMappingCounter(statistics.getGetMappingCounter()+1);
    statistics.incrementGetCounter();
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.info(username+" look for the plant "+ id);


    //kafka producer
    JSONObject plantObject = new JSONObject();
    try {
      plantObject.put("Operation", "Get");
      plantObject.put("Plant: ", this.plantRepository.findById(id));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    kafkaTemplate.send(topic,plantObject.toString());


    return this.plantRepository.findById(id);
  }

  @PostMapping("/plants")
  public Plant createNewPlant(@RequestBody Plant plant) {
    //statistics.setPostMappingCounter(statistics.getPostMappingCounter()+1);
    statistics.incrementPostCounter();
    Plant newPlant = this.plantRepository.save(plant);
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.info(username+" create new plant named " +newPlant.getName() + " ,in the database");

    //kafka producer
    JSONObject plantObject = new JSONObject();
    try {
      plantObject.put("Operation", "Post");
      plantObject.put("Plant Id: ", newPlant.getId());
      plantObject.put("Plant Name: ", newPlant.getName());
      plantObject.put("Plant Quantity: ", newPlant.getQuantity());
      plantObject.put("Plant Watering Frequency: ", newPlant.getWateringFrequency());
      plantObject.put("Plant Has Fruit: ", newPlant.getHasFruit());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    kafkaTemplate.send(topic,plantObject.toString());

    return newPlant;
    
  }

  @PutMapping("/plants/{id}")
  public Plant updatePlant(@PathVariable("id") Integer id, @RequestBody Plant p) {
    //statistics.setPutMappingCounter(statistics.getPutMappingCounter()+1);
    statistics.incrementPutCounter();
    Optional<Plant> plantToUpdateOptional = this.plantRepository.findById(id);
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!plantToUpdateOptional.isPresent()) {
      log.error(username+ " cannot update the plant, nonexistent plant");
      return null;
    }
    Plant plantToUpdate = plantToUpdateOptional.get();

    log.info(username+" update the plant "+ plantToUpdate.getId()+ " in the database");

    if (p.getHasFruit() != null) {
      plantToUpdate.setHasFruit(p.getHasFruit());
    }
    if (p.getQuantity() != null) {
      plantToUpdate.setQuantity(p.getQuantity());
    }
    if (p.getName() != null) {
      plantToUpdate.setName(p.getName());
    }
    if (p.getWateringFrequency() != null) {
      plantToUpdate.setWateringFrequency(p.getWateringFrequency());
    }
    Plant updatedPlant = this.plantRepository.save(plantToUpdate);

    //kafka producer
    JSONObject plantObject = new JSONObject();
    try {
      plantObject.put("Operation", "Put");
      plantObject.put("Plant Id: ", updatedPlant.getId());
      plantObject.put("Plant Name: ", updatedPlant.getName());
      plantObject.put("Plant Quantity: ", updatedPlant.getQuantity());
      plantObject.put("Plant Watering Frequency: ", updatedPlant.getWateringFrequency());
      plantObject.put("Plant Has Fruit: ", updatedPlant.getHasFruit());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    kafkaTemplate.send(topic,plantObject.toString());

    return updatedPlant;
  }

  @DeleteMapping("/plants/{id}")
  public Plant deletePlant(@PathVariable("id") Integer id) {
    //statistics.setDeleteMappingCounter(statistics.getGetMappingCounter()+1);
    statistics.incrementDeleteCounter();
    Optional<Plant> plantToDeleteOptional = this.plantRepository.findById(id);
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!plantToDeleteOptional.isPresent()) {
      log.error(username+" cannot delete the plant: " + id +  "nonexistent plant");
      return null;
    }
    Plant plantToDelete = plantToDeleteOptional.get();
    log.info(username+" delete the plant named " +plantToDelete.getName() +  " ,in the database");

    //kafka producer
    JSONObject plantObject = new JSONObject();
    try {
      plantObject.put("Operation", "Delete");
      plantObject.put("Plant Id: ", plantToDelete.getId());
      plantObject.put("Plant Name: ", plantToDelete.getName());
      plantObject.put("Plant Quantity: ", plantToDelete.getQuantity());
      plantObject.put("Plant Watering Frequency: ", plantToDelete.getWateringFrequency());
      plantObject.put("Plant Has Fruit: ", plantToDelete.getHasFruit());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    kafkaTemplate.send(topic,plantObject.toString());


    this.plantRepository.delete(plantToDelete);
    return plantToDelete;
  }

}