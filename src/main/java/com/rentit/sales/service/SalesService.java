package com.rentit.sales.service;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.common.infrastructure.IdentifierFactory;
import com.rentit.exception.*;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.domain.repository.PlantInventoryEntryRepository;
import com.rentit.inventory.domain.repository.PlantInventoryItemRepository;
import com.rentit.inventory.service.InventoryService;
import com.rentit.inventory.service.PlantInventoryEntryAssembler;
import com.rentit.inventory.service.PlantInventoryItemAssembler;
import com.rentit.inventory.web.dto.PlantInventoryItemDTO;
import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.reservation.domain.repository.PlantReservationRepository;
import com.rentit.reservation.service.PlantReservationAssembler;
import com.rentit.reservation.service.PlantReservationService;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderExtension;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import com.rentit.sales.domain.repository.PurchaseOrderExtensionRepository;
import com.rentit.sales.domain.repository.PurchaseOrderRepository;
import com.rentit.sales.domain.validation.PurchaseOrderValidator;
import com.rentit.sales.web.dto.CatalogQueryDTO;
import com.rentit.sales.web.dto.PurchaseOrderDTO;
import com.rentit.sales.web.dto.PurchaseOrderViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesService {


    @Autowired
    IdentifierFactory identifierFactory;

    @Autowired
    PlantReservationService plantReservationService;

    @Autowired
    PlantReservationAssembler plantReservationAssembler;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;


    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    @Autowired
    PurchaseOrderExtensionRepository purchaseOrderExtensionRepository;

    @Autowired
    InventoryService inventoryService;



    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO,PlantInventoryEntry plantInventoryEntry,String userName, String email) throws BindException {


            Long dayDiff = Math.abs(ChronoUnit.DAYS.between(purchaseOrderDTO.getRentalPeriod().getEndDate(), purchaseOrderDTO.getRentalPeriod().getStartDate())) + 1;
            BigDecimal price = plantInventoryEntry.getPrice();

            PurchaseOrder purchaseOrder = PurchaseOrder.of(
                    identifierFactory.nextDomainObjectID(),
                    null,
                    null,
                    price.multiply(new BigDecimal(dayDiff)),
                    PurchaseOrderStatus.PENDING_WORKER_CONFIRM,
                    null,
                    null,
                    plantInventoryEntry,
                    BusinessPeriod.of(purchaseOrderDTO.getRentalPeriod().getStartDate(), purchaseOrderDTO.getRentalPeriod().getEndDate()),
                    userName +'\n'+email
            );


            DataBinder binder = new DataBinder(purchaseOrder);
            binder.addValidators(new PurchaseOrderValidator());
            binder.validate();
            if (binder.getBindingResult().hasErrors())
                throw new BindException(binder.getBindingResult());
            purchaseOrderRepository.save(purchaseOrder);

            return purchaseOrderAssembler.toResource(purchaseOrder);
        }

    public List<PurchaseOrderDTO> findAllPORest() {
        List<PurchaseOrder> purchaseOrdersList = purchaseOrderRepository.findAll();
        List<PurchaseOrderDTO> purchaseOrdersDTOList = new ArrayList<PurchaseOrderDTO>();
        for (PurchaseOrder purchaseOrder : purchaseOrdersList) {
            if (purchaseOrder.getStatus() == PurchaseOrderStatus.OPEN)
                purchaseOrdersDTOList.add(PurchaseOrderDTO.of(
                        purchaseOrder.getId(),
                        purchaseOrder.getIssueDate(),
                        purchaseOrder.getPaymentSchedule(),
                        purchaseOrder.getTotal(),
                        purchaseOrder.getConsumerEmail(),
                        purchaseOrder.getStatus(),
                        plantInventoryEntryAssembler.toResource(purchaseOrder.getPlant()),
                        null
                ));
        }
        return purchaseOrdersDTOList;
    }

    public List<PurchaseOrderDTO> findPORestByStatus( PurchaseOrderStatus status) {
        List<PurchaseOrder> purchaseOrdersList = purchaseOrderRepository.findAll();
        List<PurchaseOrderDTO> purchaseOrdersDTOList = new ArrayList<PurchaseOrderDTO>();
        for (PurchaseOrder purchaseOrder : purchaseOrdersList) {
            if (purchaseOrder.getStatus() == status)
                purchaseOrdersDTOList.add(PurchaseOrderDTO.of(
                        purchaseOrder.getId(),
                        purchaseOrder.getIssueDate(),
                        purchaseOrder.getPaymentSchedule(),
                        purchaseOrder.getTotal(),
                        purchaseOrder.getConsumerEmail(),
                        purchaseOrder.getStatus(),
                        plantInventoryEntryAssembler.toResource(purchaseOrder.getPlant()),
                        null
                ));
        }
        return purchaseOrdersDTOList;
    }

    public PurchaseOrderDTO findPOById(String id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        if (purchaseOrder == null) {
            throw new PurchaseOrderNotFoundException(id);
        } else {
            return purchaseOrderAssembler.toResource(purchaseOrder);
        }
    }

    public List<PlantInventoryEntry> findAvailableEntries(CatalogQueryDTO catalogQueryDTO) {
        return plantInventoryEntryRepository.findAvailableByName(catalogQueryDTO.getName(), catalogQueryDTO.getRentalPeriod().getStartDate(), catalogQueryDTO.getRentalPeriod().getEndDate());
    }

    public PurchaseOrderDTO acceptPurchaseOrder(String id) throws BindException, PlantUnavailableException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);

        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(purchaseOrder));
    }
    public PurchaseOrderDTO acceptAndReservePurchaseOrder(String id) throws BindException, PlantUnavailableException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        purchaseOrder.setStatus(PurchaseOrderStatus.OPEN);

        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(purchaseOrder));
    }

    public PurchaseOrderDTO modifyPurchaseOrder(String id, BigDecimal total, String start, String end) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.setStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM);
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        po.setRentalPeriod(BusinessPeriod.of(startDate,endDate));
        po.setTotal(total);
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }
    public PurchaseOrderDTO workerModifyPurchaseOrder(String id, BigDecimal total, String start, String end) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.setStatus(PurchaseOrderStatus.PENDING);
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        po.setRentalPeriod(BusinessPeriod.of(startDate,endDate));
        po.setTotal(total);
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }
    public PurchaseOrderDTO modifyWorkerPurchaseOrder(String id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.setStatus(PurchaseOrderStatus.PENDING);
        po.setTotal(BigDecimal.valueOf(1090));
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }

    public PurchaseOrderDTO rejectPurchaseOrder(String id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.handleRejection();
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }

    public void closePurchaseOrder() throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        for (int i = 0; i < purchaseOrderList.size(); i++) {
            PurchaseOrder purchaseOrder = purchaseOrderList.get(i);

            purchaseOrder.getRentalPeriod().getEndDate();
            LocalDate now = LocalDate.now();

            LocalDate issueDate = purchaseOrder.getRentalPeriod().getEndDate();
            if(issueDate.isBefore(now)) {
                System.out.println(" ------- -------   " + issueDate.toString() + now.toString());

                if(purchaseOrder.getStatus()==PurchaseOrderStatus.OPEN ) {
                    purchaseOrder.setStatus(PurchaseOrderStatus.CLOSED);
                }
                else
                    purchaseOrder.setStatus(PurchaseOrderStatus.EXPIRED);
                purchaseOrderRepository.save(purchaseOrder);

            }
        }
    }

    public PurchaseOrderDTO cancelPurchaseOrder(String id) throws PurchaseOrderCancellationException {


        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        purchaseOrder.cancelPurchaseOrder();

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderAssembler.toResource(purchaseOrder);
    }

    public PurchaseOrderDTO cancelConfirmedPurchaseOrder(String id) throws PurchaseOrderCancellationException {


        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        purchaseOrder.cancelConfirmedPurchaseOrder();

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderAssembler.toResource(purchaseOrder);
    }

    public PurchaseOrderDTO resubmitPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws BadRequestException, ReservationPeriodNotAvailableException {
        PurchaseOrder po = purchaseOrderRepository.findOne(purchaseOrderDTO.get_id());

        if (po.getStatus() != PurchaseOrderStatus.PENDING && po.getStatus() != PurchaseOrderStatus.REJECTED)
            throw new ReservationPeriodNotAvailableException("Cannot change accepted purchase order");

        PlantInventoryEntry entry = po.getPlant();

        if (inventoryService.findAvailablePlantsRest(entry.getName(), po.getRentalPeriod().getStartDate(), po.getRentalPeriod().getEndDate()).size() == 0)
            throw new ReservationPeriodNotAvailableException("No reservation slot for the current period");
        po.handleResubmiting(purchaseOrderDTO.getRentalPeriod().convertToEntity());

        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }

    public List<PurchaseOrderViewDTO> getAllPurchaseOrderByStatus(PurchaseOrderStatus purchaseOrderStatus) {
        List<PurchaseOrderViewDTO> purchaseOrderViewDTOs = new ArrayList<>();

        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.getPurchaseOrderByStatus(purchaseOrderStatus);
        if (purchaseOrderStatus == PurchaseOrderStatus.PENDING) {
            purchaseOrders.addAll(purchaseOrderRepository.getPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_EXTENSION));
        }
        purchaseOrders.forEach(purchaseOrder -> {
            PurchaseOrderViewDTO purchaseOrderViewDTO = new PurchaseOrderViewDTO();
            purchaseOrderViewDTO.setPurchaseOrderDTO(purchaseOrderAssembler.toResource(purchaseOrder));
            purchaseOrderViewDTO.setPlantInventoryItemDTO(getPlantInventoryItemDTO(purchaseOrder));
            purchaseOrderViewDTOs.add(purchaseOrderViewDTO);
        });
        return purchaseOrderViewDTOs;
    }
    public PurchaseOrderDTO rejectCancelPurchaseOrder(String id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.setStatus(PurchaseOrderStatus.CANCEL_REJECTED);
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }
    public PurchaseOrderDTO acceptCancelPurchaseOrder(String id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.setStatus(PurchaseOrderStatus.CANCEL);
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.save(po));
    }
    public List<PurchaseOrderViewDTO> getPurchaseOrderByDate(LocalDate date) {
        List<PurchaseOrder> openPurchaseOrders = purchaseOrderRepository.getPurchaseOrderByStatus(PurchaseOrderStatus.OPEN);
        List<PurchaseOrder> filteredPurchaseOrders = openPurchaseOrders.stream().filter(o -> o.getRentalPeriod().getStartDate().isEqual(date)).collect(Collectors.toList());
        List<PurchaseOrderViewDTO> purchaseOrderViewDTOs = new ArrayList<>();
        filteredPurchaseOrders.forEach(purchaseOrder -> {
            PurchaseOrderViewDTO purchaseOrderViewDTO = new PurchaseOrderViewDTO();
            purchaseOrderViewDTO.setPurchaseOrderDTO(purchaseOrderAssembler.toResource(purchaseOrder));


            purchaseOrderViewDTO.setPlantInventoryItemDTO(getPlantInventoryItemDTO(purchaseOrder));
            purchaseOrderViewDTOs.add(purchaseOrderViewDTO);

        });
        return purchaseOrderViewDTOs;
    }

    private PlantInventoryItemDTO getPlantInventoryItemDTO(PurchaseOrder purchaseOrder) {
        PlantReservation reservation = purchaseOrder.getReservation();
        if (reservation == null) {
            return null;
        }
        return plantInventoryItemAssembler.toResource(plantInventoryItemRepository.findOne(reservation.getPlant().getId()));
    }


    public PurchaseOrderDTO createPurchaseOrderExtension(String id, LocalDate endDate) throws PurchaseOrderExtensionNotFoundException, PlantNotFoundException, PlantUnavailableException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.OPEN) {
            PurchaseOrderExtension purchaseOrderExtension = PurchaseOrderExtension.of(
                    identifierFactory.nextDomainObjectID(),
                    endDate,
                    PurchaseOrderStatus.PENDING_EXTENSION
            );
            PlantReservation plantReservation = plantReservationRepository.findOne(purchaseOrder.getReservation().getId());
            List<PlantInventoryItem> plantInventoryItems = plantInventoryItemRepository.findByAvailablityById(
                    plantReservation.getPlant().getId(),
                    purchaseOrder.getRentalPeriod().getEndDate().plusDays(1),
                    purchaseOrderExtension.getEndDate());
            if (plantInventoryItems.size() > 0) {
                purchaseOrderExtensionRepository.save(purchaseOrderExtension);
                purchaseOrder.addExtension(purchaseOrderExtension);
                PurchaseOrder purchaseOrderResult = purchaseOrderRepository.save(purchaseOrder);
                return purchaseOrderAssembler.toResource(purchaseOrderResult);
            } else {
                throw new PlantUnavailableException();
            }
        } else {
            throw new PurchaseOrderExtensionNotFoundException("You purchase order status must be OPEN");
        }
    }

    public PurchaseOrderDTO acceptExtensionPurchaseOrder(String id, String exId) throws PurchaseOrderExtensionNotFoundException, PlantUnavailableException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        PlantReservation plantReservation = plantReservationRepository.findOne(purchaseOrder.getReservation().getId());

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.PENDING_EXTENSION) {
            List<PlantInventoryItem> plantInventoryItems = plantInventoryItemRepository.findByAvailablityById(
                    plantReservation.getPlant().getId(),
                    purchaseOrder.getRentalPeriod().getEndDate().plusDays(1),
                    purchaseOrder.getExtensions().get(purchaseOrder.getExtensions().size() - 1).getEndDate());
            if (plantInventoryItems.size() > 0) {
                PlantReservation plantReservationUpdated = PlantReservation.of(
                        plantReservation.getId(),
                        BusinessPeriod.of(
                                purchaseOrder.getRentalPeriod().getStartDate(),
                                purchaseOrder.getExtensions().get(purchaseOrder.getExtensions().size() - 1).getEndDate()),
                        plantReservation.getPlant());
                purchaseOrder.getExtensions().get(purchaseOrder.getExtensions().size() - 1).acceptExtension();
                purchaseOrder.confirmReservation(plantReservationUpdated, purchaseOrder.getPlant().getPrice());
                PurchaseOrder purchaseOrderResult = purchaseOrderRepository.save(purchaseOrder);
                plantReservationRepository.save(plantReservationUpdated);
                return purchaseOrderAssembler.toResource(purchaseOrderResult);
            } else {
                throw new PlantUnavailableException();
            }
        } else {
            throw new PurchaseOrderExtensionNotFoundException("You purchase order status must be OPEN");
        }
    }

    public PurchaseOrderDTO rejectExtensionPurchaseOrder(String id) throws PlantNotFoundException, PurchaseOrderExtensionNotFoundException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.PENDING_EXTENSION) {


            purchaseOrder.getExtensions().get(purchaseOrder.getExtensions().size() - 1).rejectExtension();
            purchaseOrder.handleAcceptance();
            PurchaseOrder purchaseOrderResult = purchaseOrderRepository.save(purchaseOrder);
            return purchaseOrderAssembler.toResource(purchaseOrderResult);
        } else {
            throw new PurchaseOrderExtensionNotFoundException("You purchase order status must be OPEN");
        }
    }
}
