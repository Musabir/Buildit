package com.rentit.sales.web.controller;

import com.google.gson.Gson;
import com.rentit.exception.*;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.service.InventoryService;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import com.rentit.sales.service.PurchaseOrderAssembler;
import com.rentit.sales.service.SalesService;
import com.rentit.sales.web.dto.CatalogQueryDTO;
import com.rentit.sales.web.dto.PurchaseOrderDTO;
import com.rentit.sales.web.dto.PurchaseOrderViewDTO;
import com.rentit.user.domain.model.User;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    SalesService salesService;


    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    HttpSession session;

    boolean check = true;


    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String checkSession() {
        return "login";
    }


    @GetMapping("/catalog/form")
    public String getQueryForm(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException, JSONException {
        salesService.closePurchaseOrder();
        List<PlantInventoryEntryDTO> list = new ArrayList<>();
        URL url = new URL("http://final-rentitt-team111.herokuapp.com/api/sales/orders");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        String inline = "";
        Scanner sc = new Scanner(url.openStream());
        while(sc.hasNext())
        {
            inline+=sc.nextLine();
        }
        JSONArray jsonArray  = new JSONArray(inline);

        for(int i=0;i<jsonArray.length();i++){
                    PlantInventoryEntryDTO data = new Gson().fromJson(jsonArray.get(i).toString(), PlantInventoryEntryDTO.class);
                    System.out.println(" data " +data);
                    list.add(data);

        }


        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            model.addAttribute("catalogQuery", new CatalogQueryDTO());
            model.addAttribute("plants", list);
            if (user.getRole() == 2)
                return "dashboard/catalog/query-form";
            else {
                return "";
            }
        }
        return "login";

    }

    @PostMapping("/catalog/query")
    public String postQueryForm(CatalogQueryDTO catalogQueryDTO, Model model) throws JSONException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        List<PlantInventoryEntryDTO> list = new ArrayList<>();
        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            if (catalogQueryDTO.getRentalPeriod().getEndDate().isBefore(catalogQueryDTO.getRentalPeriod().getStartDate())) {

                    model.addAttribute("error", "End Date ("+catalogQueryDTO.getRentalPeriod().getEndDate()+") is before After Date ("+catalogQueryDTO.getRentalPeriod().getStartDate()+"). Please correct valid date!");
                    return "dashboard/catalog/no-result";

                }
                else {
                String walletBalanceUrl = "http://final-rentitt-team111.herokuapp.com/api/sales/orders/query/";

                RestTemplate restTemplate = new RestTemplate();

                String result = restTemplate.postForObject(walletBalanceUrl, catalogQueryDTO, String.class);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    PlantInventoryEntryDTO data = new Gson().fromJson(jsonArray.get(i).toString(), PlantInventoryEntryDTO.class);
                    System.out.println(" data " + data);
                    list.add(data);

                }
                System.out.println(result);
                model.addAttribute("plants", list);
                model.addAttribute("rentalPeriod", catalogQueryDTO.getRentalPeriod());
                if (user.getRole() == 2)
                    return "dashboard/catalog/query-result";
                else {
                    return "";
                }
            }

        }
        else return "login";
    }

    @PostMapping("/orders")
    public String postCreatePO(PurchaseOrderDTO purchaseOrderDTO, Model model) throws BindException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");
        if (session != null && session.getAttribute("user") != null) {

            PurchaseOrderDTO purchaseOrder = null;
            String walletBalanceUrl = "http://final-rentitt-team111.herokuapp.com/api/sales/orders/findplant/";

            RestTemplate restTemplate = new RestTemplate();

            PlantInventoryEntry result = restTemplate.postForObject( walletBalanceUrl, purchaseOrderDTO, PlantInventoryEntry.class);
            purchaseOrder = salesService.createPurchaseOrder(purchaseOrderDTO,result,user.getUsername(),user.getEmail());

            model.addAttribute("purchaseOrder", purchaseOrder);
            if (user.getRole() == 2)
                return "dashboard/catalog/po-result";
            else {
                return "";
            }
        }
        return "login";

    }

    @RequestMapping(method = GET, path = "/closedorders")
    public String getAllClosedPurchaseOrder(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {

            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CLOSED));
            if (user.getRole() == 2)
                return "dashboard/orders/site-closedrequest";
            else {
                return "";
            }
        }
        return "login";

    }

    @RequestMapping(method = GET, path = "/timeoutorders")
    public String getAllTimeOutPurchaseOrder(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {

            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.EXPIRED));
            if (user.getRole() == 2)
                return "dashboard/orders/site-timeout";
            else {
                return "";
            }
        }
        return "login";

    }

    @RequestMapping(method = GET, path = "/pendingorders")
    public String getAllSitePendingPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 2)
                return "";
            else if (user.getRole() ==2)
                return "dashboard/orders/site-pendingrequest";
            else return "";
        }
        return "login";

    }
    @RequestMapping(method = GET, path = "/workpendingorders")
    public String getAllWorkPendingPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() == 1)
                return "dashboard/orders/pending-orders";
            else
                return "";
        }
        return "login";

    }

    @RequestMapping(method = GET, path = "/rejectedorders")
    public String getAllRejectedPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.REJECTED));
            model.addAttribute("pod", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-rejectedrequest";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = POST, path = "/pendingorders")
    public String acceptPendingPurchaseOrders(Model model, PurchaseOrderDTO purchaseOrderDTO) throws BindException, PurchaseOrderExtensionNotFoundException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {

            try {
                if (purchaseOrderDTO.getStatus() == PurchaseOrderStatus.PENDING_WORKER_CONFIRM) {

                    salesService.acceptPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));

                } else {
                    salesService.acceptExtensionPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")), "");
                }

            } catch (PlantUnavailableException e) {
                model.addAttribute("error_message", e.getMessage());
            } finally {
                model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
                model.addAttribute("po", new PurchaseOrderDTO());
            }
            if (user.getRole() == 1)
            return "dashboard/orders/pending-orders";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = POST, path = "/workerpendingorders")
    public String getAllPendingWorkerPurchaseOrders(@RequestParam("order-id")String id, @RequestParam("total") BigDecimal total, @RequestParam("rental-start")String start,
                                                    @RequestParam("rental-end") String end, Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            salesService.modifyPurchaseOrder(id+"",total,start,end);
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 2)
                return "";
            else if (user.getRole() == 2)
                return "dashboard/orders/site-workpending";
        }
        return "login";
    }

    @RequestMapping(method = GET, path = "/workerpendingorders")
    public String getAllPendingWorkerGETPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 2)
                return "";
            else if (user.getRole() == 2)
                return "dashboard/orders/site-workpending";
        }
        return "login";
    }


    @RequestMapping(method = DELETE, path = "/pendingorders")
    public String rejectPendingPurchaseOrders(Model model, PurchaseOrderDTO purchaseOrderDTO) throws PlantNotFoundException, BindException, PurchaseOrderExtensionNotFoundException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            if (purchaseOrderDTO.getStatus() == PurchaseOrderStatus.PENDING_WORKER_CONFIRM) {
                salesService.rejectPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
            } else {
                salesService.rejectExtensionPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
            }
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 1)
                return "dashboard/orders/pending-orders";
            else return "";
        } else {
            return "login";
        }
    }



    @RequestMapping(method = GET, path = "/cancelpending")
    public String getAllCancelPurchaseOrders(Model model) throws IOException, JSONException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        List<PurchaseOrderViewDTO> list = new ArrayList<>();
        if (session != null && session.getAttribute("user") != null) {

            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL_REJECTED));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL));

            model.addAttribute("orders", list);
            model.addAttribute("pod", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-cancelledrequest";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = POST, path = "/cancelconfirmedorders")
    public String cancelConfirmedPurchaseOrders(Model model, PurchaseOrderDTO purchaseOrderDTO) throws PlantNotFoundException, BindException, PurchaseOrderExtensionNotFoundException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            if (purchaseOrderDTO.getStatus() == PurchaseOrderStatus.OPEN) {
                try {
                    salesService.cancelConfirmedPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    salesService.cancelConfirmedPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            }
            List<PurchaseOrderViewDTO> list = new ArrayList<>();
            //  if(session !=null &&  session.getAttribute("user") != null) {
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.OPEN));
            model.addAttribute("orders", list);
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-confirmedrequest";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = POST, path = "/cancelpending")
    public String cancelPendingPurchaseOrders(Model model, PurchaseOrderDTO purchaseOrderDTO) throws PlantNotFoundException, BindException, PurchaseOrderExtensionNotFoundException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            if (purchaseOrderDTO.getStatus() == PurchaseOrderStatus.PENDING) {
                try {
                    salesService.cancelConfirmedPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    salesService.cancelConfirmedPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            }
            List<PurchaseOrderViewDTO> list = new ArrayList<>();
            //  if(session !=null &&  session.getAttribute("user") != null) {
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING));
            model.addAttribute("orders", list);
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-pendingrequest";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = POST, path = "/cancelworkpending")
    public String cancelWorkPendingPurchaseOrders(Model model, PurchaseOrderDTO purchaseOrderDTO) throws PlantNotFoundException, BindException, PurchaseOrderExtensionNotFoundException, InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        if (session != null && session.getAttribute("user") != null) {
            if (purchaseOrderDTO.getStatus() == PurchaseOrderStatus.PENDING_WORKER_CONFIRM) {
                try {
                    salesService.cancelPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    salesService.cancelPurchaseOrder(purchaseOrderAssembler.resolveId(purchaseOrderDTO.getLink("self")));
                } catch (PurchaseOrderCancellationException e) {
                    e.printStackTrace();
                }
            }
            List<PurchaseOrderViewDTO> list = new ArrayList<>();
            //  if(session !=null &&  session.getAttribute("user") != null) {
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("orders", list);
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-pendingrequest";
            else return "";
        } else {
            return "login";
        }
    }

    @RequestMapping(method = GET, path = "/orders")
    public String getAllPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        User user = (User) session.getAttribute("user");

        List<PurchaseOrderViewDTO> list = new ArrayList<>();
        if (session != null && session.getAttribute("user") != null) {
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.OPEN));

            model.addAttribute("orders", list);
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 2)
                return "dashboard/orders/site-confirmedrequest";
            else return "";

        } else {
            return "login";
        }

    }

    @RequestMapping(method = GET, path = "/allorders")
    public String getWorkerAllPurchaseOrders(Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {

        salesService.closePurchaseOrder();

        System.out.println("-----------------"+"Here");
        User user = (User) session.getAttribute("user");

        List<PurchaseOrderViewDTO> list = new ArrayList<>();
        if (session != null && session.getAttribute("user") != null) {
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.OPEN));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CLOSED));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.REJECTED));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL_REJECTED));
            list.addAll(salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.CANCEL_PENDING));

            model.addAttribute("orders", list);
            model.addAttribute("po", new PurchaseOrderDTO());
            if (user.getRole() == 1)
                return "dashboard/orders/submittedpo";
            else return "";

        } else {
            return "login";
        }

    }

    @RequestMapping(method = POST, path = "/pendingmodifyorder")
    public String modifyOrder(@RequestParam("order-id")String id, @RequestParam("total") BigDecimal total, @RequestParam("rental-start")String start,
                              @RequestParam("rental-end") String end, Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        System.out.println("------------------ " + id + " " + total + " " + start + " " + end);
        if (session != null && session.getAttribute("user") != null) {
            salesService.modifyPurchaseOrder(id,total,start,end);
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 2)
                return "";
            else if (user.getRole() ==2)
                return "dashboard/orders/site-pendingrequest";
            else return "";
        }
        return "login";

    }

    @RequestMapping(method = POST, path = "/pendingworkermodifyorder")
    public String modifyWorkOrder(@RequestParam("order-id")String id, @RequestParam("total") BigDecimal total, @RequestParam("rental-start")String start,
                              @RequestParam("rental-end") String end, Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            salesService.modifyPurchaseOrder(id+"",total,start,end);
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 2)
                return "";
            else if (user.getRole() ==2)
                return "dashboard/orders/site-workpending";
            else return "";
        }
        return "login";

    }

    @RequestMapping(method = POST, path = "/workermodifypendingorders")
    public String workermodifyOrder(@RequestParam("order-id")String id, @RequestParam("total") BigDecimal total, @RequestParam("rental-start")String start,
                                  @RequestParam("rental-end") String end, Model model) throws InvoiceNotFoundException, MessagingException, PurchaseOrderNotFoundException, IOException {
        salesService.closePurchaseOrder();

        if (session != null && session.getAttribute("user") != null) {
            salesService.workerModifyPurchaseOrder(id+"",total,start,end);
            model.addAttribute("orders", salesService.getAllPurchaseOrderByStatus(PurchaseOrderStatus.PENDING_WORKER_CONFIRM));
            model.addAttribute("pod", new PurchaseOrderDTO());
            User user = (User) session.getAttribute("user");
            if (user.getRole() != 1)
                return "";
            else if (user.getRole() ==1)
                return "dashboard/orders/pending-orders";
            else return "";
        }
        return "login";

    }


    @ExceptionHandler(PlantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlePlantNotFoundException(PlantNotFoundException ex) {
    }

    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ResponseEntity<String> handlePurchaseOrderNotFoundException(PurchaseOrderNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


}