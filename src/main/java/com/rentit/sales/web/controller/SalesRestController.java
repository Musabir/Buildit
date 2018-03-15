package com.rentit.sales.web.controller;

import com.rentit.common.infrastructure.IdentifierFactory;
import com.rentit.exception.*;
import com.rentit.inventory.domain.repository.PlantInventoryEntryRepository;
import com.rentit.inventory.service.InventoryService;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import com.rentit.sales.domain.repository.PurchaseOrderRepository;
import com.rentit.sales.service.PurchaseOrderAssembler;
import com.rentit.sales.service.SalesService;
import com.rentit.sales.web.dto.CustomPurchaseOrderDto;
import com.rentit.sales.web.dto.PurchaseOrderDTO;
import com.rentit.sales.web.dto.PurchaseOrderViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/sales/orders")
public class SalesRestController {
    @Autowired
    InventoryService inventoryService;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    SalesService salesService;

    @Autowired
    IdentifierFactory identifierFactory;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;


    @GetMapping
    public List<PurchaseOrderDTO> findAllOrders(){
            return salesService.findAllPORest();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") String id) throws PurchaseOrderNotFoundException {
        return salesService.findPOById(id);
    }

    /*
    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) throws PlantNotFoundException, BindException {
        PurchaseOrderDTO newlyCreatedPODTO = salesService.createPurchaseOrder(partialPODTO);
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(newlyCreatedPODTO.getId().getHref()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<PurchaseOrderDTO>(newlyCreatedPODTO, headers, HttpStatus.CREATED);
    }

    */
    @GetMapping("/pendingdorders")
    public List<CustomPurchaseOrderDto> findAllPendingOrders(){
        return listOfCustomPurchaseOrderDto(PurchaseOrderStatus.PENDING);
    }
    @GetMapping("/cancelpending")
    public List<CustomPurchaseOrderDto> findAllCancelPendingOrders(){
        return listOfCustomPurchaseOrderDto(PurchaseOrderStatus.CANCEL_PENDING);
    }

    @RequestMapping(value = "/allorders", method = RequestMethod.GET)
    public List<CustomPurchaseOrderDto> findAllAvailableEntries(){
        List<CustomPurchaseOrderDto> list = new ArrayList<>();
        list.addAll(listOfCustomPurchaseOrderDto(PurchaseOrderStatus.OPEN));
        list.addAll(listOfCustomPurchaseOrderDto(PurchaseOrderStatus.PENDING));
        list.addAll(listOfCustomPurchaseOrderDto(PurchaseOrderStatus.CLOSED));
        list.addAll(listOfCustomPurchaseOrderDto(PurchaseOrderStatus.CANCEL_REJECTED));
        list.addAll(listOfCustomPurchaseOrderDto(PurchaseOrderStatus.CANCEL_PENDING));
        return list;
    }



    @RequestMapping(value = "/acceptpendingorders", method = RequestMethod.POST)
    public List<CustomPurchaseOrderDto> acceptPendingOrder(@RequestBody String id) {
        try {
            salesService.acceptAndReservePurchaseOrder(id);
        } catch (BindException e) {
            e.printStackTrace();
            return null;
        } catch (PlantUnavailableException e) {
            e.printStackTrace();
            return null;
        }
        return listOfCustomPurchaseOrderDto(PurchaseOrderStatus.PENDING);
    }


        @RequestMapping(value = "/rejectpendingorders", method = RequestMethod.POST)
    public List<CustomPurchaseOrderDto> rejectPendingOrder(@RequestBody String id){
            salesService.rejectPurchaseOrder(id);
        return listOfCustomPurchaseOrderDto(PurchaseOrderStatus.PENDING);

    }

    @RequestMapping(value = "/cancelacceptpendingorders", method = RequestMethod.POST)
    public List<CustomPurchaseOrderDto> cancelAcceptPendingOrder(@RequestBody String id){
        salesService.acceptCancelPurchaseOrder(id);
        return listOfCustomPurchaseOrderDto(PurchaseOrderStatus.CANCEL_PENDING);
    }

    @RequestMapping(value = "/cancelrejectpendingorders", method = RequestMethod.POST)
    public List<PurchaseOrderViewDTO> cancelRejectPendingOrder(@RequestBody String id){
        salesService.rejectCancelPurchaseOrder(id);
        return salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL_PENDING);
    }
    @RequestMapping(value = "/findpurchaseorderbyid", method = RequestMethod.POST)
    public PurchaseOrder findPurchaseOrderbyId(@RequestBody String id)  {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);

        return purchaseOrder;
    }

    @PostMapping("{id}/accept")
    public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable String id) throws
            BindException, PlantUnavailableException {
        return salesService.acceptPurchaseOrder(id);
    }

    @DeleteMapping("{id}/accept")
    public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable String id) {
        return salesService.rejectPurchaseOrder(id);
    }

    @PostMapping("/accept/{id}")
    public PurchaseOrderDTO acceptPendingPurchaseOrder(@PathVariable String id) throws
            BindException, PlantUnavailableException {
        return salesService.acceptPurchaseOrder(id);
    }

    @PostMapping("/reject/{id}")
    public PurchaseOrderDTO rejectPendingPurchaseOrder(@PathVariable String id) {
        return salesService.rejectPurchaseOrder(id);
    }

    @PostMapping("{id}/cancel")
    public PurchaseOrderDTO cancelPurchaseOrder(@PathVariable String id) throws PurchaseOrderCancellationException {
        return salesService.cancelPurchaseOrder(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<PurchaseOrderDTO> resubmitPurchaseOrder(@PathVariable String id, @RequestBody PurchaseOrderDTO purchaseOrderDTO) throws Exception {
        PurchaseOrderDTO updatedPurchaseOrder = salesService.resubmitPurchaseOrder(purchaseOrderDTO);
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(updatedPurchaseOrder.getId().getHref()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<PurchaseOrderDTO>(updatedPurchaseOrder, headers, HttpStatus.CREATED);
    }

    @GetMapping("/openandclosedorders")
    public List<PurchaseOrderDTO> findAllOpenAndClosedOrders(){
        List<PurchaseOrderDTO> list = new ArrayList<>();
        list.addAll(salesService.findAllPORest());
        return list;
    }

    @GetMapping("/cancelpendingdorders")
    public List<PurchaseOrderViewDTO> cancelPendingOrders(){
        List<PurchaseOrderViewDTO> list = new ArrayList<>();
        list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL_PENDING));
        return list;
    }

    @ExceptionHandler(PlantNotFoundException.class)
    public ResponseEntity<String> handlePlantNotFoundException(PlantNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PurchaseOrderCancellationException.class)
    public ResponseEntity<String> handlePurchaseOrderCancellationException(PurchaseOrderCancellationException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ResponseEntity<String> handlePurchaseOrderNotFoundException(PurchaseOrderNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PlantUnavailableException.class, InvalidRentalPeriodException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<String> handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PurchaseOrderExtensionNotFoundException.class)
    public ResponseEntity<String> handlePurchaseOrderExtensionNotFoundException(PurchaseOrderExtensionNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    public List<CustomPurchaseOrderDto> listOfCustomPurchaseOrderDto(PurchaseOrderStatus status){
        List<PurchaseOrderViewDTO> s = new ArrayList<>();
        List<CustomPurchaseOrderDto> list = new ArrayList<>();
        s = salesService.getAllPurchaseOrderByStatus(status);
        for(int i =0;i<s.size();i++){
            CustomPurchaseOrderDto cs = new CustomPurchaseOrderDto();
            PurchaseOrderDTO ps = s.get(i).getPurchaseOrderDTO();
            cs.set_id(ps.get_id());
            cs.setConsumerEmail(ps.getConsumerEmail());
            cs.setPlant(ps.getPlant());
            cs.setStatus(ps.getStatus());
            cs.setEndDate(ps.getRentalPeriod().getEndDate().toString());
            cs.setStartDate(ps.getRentalPeriod().getStartDate().toString());
            cs.setTotal(ps.getTotal());
            list.add(cs);
        }
        return list;
    }
}
