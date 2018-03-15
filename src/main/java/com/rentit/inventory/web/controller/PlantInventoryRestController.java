package com.rentit.inventory.web.controller;

import com.rentit.exception.InvalidRentalPeriodException;
import com.rentit.exception.InvoiceNotFoundException;
import com.rentit.exception.PlantNotFoundException;
import com.rentit.inventory.service.InventoryService;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/inventory/plants")
public class PlantInventoryRestController {

    @Autowired
    InventoryService inventoryService;




    @RequestMapping(method = GET, path = "all")
    public List<PlantInventoryEntryDTO> list() {
        return inventoryService.findAllInventoryEntries();
    }


    @RequestMapping(method = GET, path = "")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws InvalidRentalPeriodException {

        if (startDate == null ||
            endDate == null ||
            startDate.isAfter(endDate) ||
            startDate.isBefore(LocalDate.now())
        ) {
            throw new InvalidRentalPeriodException();
        }

        return inventoryService.findAvailablePlantsRest(plantName, startDate,endDate);
    }

    @RequestMapping(method = GET, path = "{id}")
    public PlantInventoryEntryDTO show(@PathVariable String id) throws PlantNotFoundException {
        return inventoryService.findPlant(id);
    }


    @ExceptionHandler(InvalidRentalPeriodException.class)
    public ResponseEntity<String> handleInvalidRentalPeriodException(InvalidRentalPeriodException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
