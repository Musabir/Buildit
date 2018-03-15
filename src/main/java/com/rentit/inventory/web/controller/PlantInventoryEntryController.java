package com.rentit.inventory.web.controller;

import com.rentit.inventory.service.InventoryService;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.web.dto.PlantInventoryItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


@Controller
@RequestMapping("/dashboard")
public class PlantInventoryEntryController {
    @Autowired
    InventoryService inventoryService;


    @GetMapping("/plants")
    public String list(Model model) {
        model.addAttribute("plants", inventoryService.findAllInventoryEntries());
        return "plants/list";
    }


    @GetMapping(value="/plants/form")
    public String form(PlantInventoryEntryDTO plantInventoryEntryDTO, Model model) {
        model.addAttribute("plant", plantInventoryEntryDTO);
        return "plants/create";
    }

    @PostMapping(value="/plants")
    public String create(PlantInventoryEntryDTO plant) {
        inventoryService.createInventoryEntry(plant);
        return "redirect:/plants";
    }


    @RequestMapping(method = GET, path = "/items/{id}")
    public String showPlantInventoryItem(Model model, @PathVariable String id) {
        PlantInventoryItemDTO item = inventoryService.findInvntoryItemByID(id);

        model.addAttribute("allStatus",possibleStatus());
        model.addAttribute("item", item);
        model.addAttribute("linkm", "/dashboard/maintenance/plants/"+id);
        model.addAttribute("linki","/dashboard/items/"+id);
        return "dashboard/items/show";
    }

    @RequestMapping(method = PUT, path = "/items/{id}")
    public String updatePlantInventoryItemStatus(Model model,PlantInventoryItemDTO plantInventoryItemDTO, @PathVariable String id) {
        PlantInventoryItemDTO po = inventoryService.findInvntoryItemByID(id);
        po.setItemStatus(plantInventoryItemDTO.getItemStatus());
        po = inventoryService.updatePlantItem(po);
        model.addAttribute("allStatus",possibleStatus());
        model.addAttribute("item", po);
        model.addAttribute("linki","/dashboard/items/"+id);
        return "dashboard/items/show";
    }
    public List<String> possibleStatus(){
        List<String> status = new ArrayList<String>();
        status.add("DELIVERED");
        status.add("RETURNED");
        status.add("REJECTED");
        status.add("DISPATCHED");
        return status;
    }




}
