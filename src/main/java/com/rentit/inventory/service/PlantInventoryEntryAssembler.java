package com.rentit.inventory.service;

import com.google.gson.Gson;
import com.rentit.common.rest.ExtendedLink;
import com.rentit.exception.PlantNotFoundException;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.web.controller.PlantInventoryRestController;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.sales.service.PurchaseOrderAssembler;
import com.rentit.sales.web.controller.SalesRestController;
import com.rentit.sales.web.dto.CustomPurchaseOrderDto;
import com.rentit.sales.web.dto.PurchaseOrderDTO;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import static org.springframework.http.HttpMethod.POST;


@Service
public class PlantInventoryEntryAssembler extends ResourceAssemblerSupport<PlantInventoryEntry, PlantInventoryEntryDTO> {


    UriTemplate uriTemplate;


    public PlantInventoryEntryAssembler() {
        super(PlantInventoryRestController.class, PlantInventoryEntryDTO.class);

        AnnotationMappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);
        try {
            String mapping = discoverer.getMapping(PlantInventoryRestController.class,
                    PlantInventoryRestController.class.getMethod("show", String.class));

            uriTemplate = new UriTemplate(mapping);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlantInventoryEntryDTO toResource(PlantInventoryEntry plantInventoryEntry) {
        System.out.println("-------   "+plantInventoryEntry.getId()+"    "+plantInventoryEntry.getName()+"    "
                +plantInventoryEntry.getDescription()+"    "+plantInventoryEntry.getPrice()+"    ");
        String id = null;
            RestTemplate restTemplate = new RestTemplate();
            String acceptUrl = "http://final-rentitt-team111.herokuapp.com/api/sales/orders/findbyname/";
            String pl = restTemplate.postForObject(acceptUrl,plantInventoryEntry.getName(), String.class);
            id = pl;

        PlantInventoryEntryDTO dto = createResourceWithId(id, plantInventoryEntry);
        dto.set_id(id);
        dto.setName(plantInventoryEntry.getName());
        dto.setDescription(plantInventoryEntry.getDescription());
        dto.setPrice(plantInventoryEntry.getPrice());

    /* //musabir
        try {
            dto.add(new ExtendedLink(
                        linkTo(methodOn(SalesRestController.class).createPurchaseOrder(new PurchaseOrderDTO())).toString(),
                        "createPurchaseOrder",POST));
        } catch (PlantNotFoundException e) {
            e.printStackTrace();
        } catch (BindException e) {
            e.printStackTrace();
        }
        */

        return dto;
    }
}
