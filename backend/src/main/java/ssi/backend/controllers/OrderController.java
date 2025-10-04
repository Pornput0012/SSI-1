package ssi.backend.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import ssi.backend.dtos.OrderBuyerPageDTO;
import ssi.backend.dtos.OrderDetailResponseDTO;
import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.dtos.OrderResponseDTO;
import ssi.backend.dtos.OrderSellerPageDTO;
import ssi.backend.services.OrderService;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}", allowCredentials = "true")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/v2/users/{id}/orders")
    public ResponseEntity<OrderBuyerPageDTO> getOrderBuyer(@PathVariable("id") Integer buyerId,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) throws ParseException, JOSEException {

        OrderBuyerPageDTO orderBuyerPage = orderService.getOrderBuyer(buyerId, page, size, sortField, sortDirection);
        return ResponseEntity.ok(orderBuyerPage);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/v2/sellers/{sid}/orders")
    public ResponseEntity<OrderSellerPageDTO> getOrderSeller(@PathVariable("sid") Integer sellerId,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) throws ParseException, JOSEException {

        OrderSellerPageDTO orderSellerPage = orderService.getOrderSeller(sellerId, page, size, sortField, sortDirection);
        return ResponseEntity.ok(orderSellerPage);
    }

    @GetMapping("/v2/orders/{id}")
    public ResponseEntity<OrderDetailResponseDTO> getOrderById(@PathVariable("id") Integer orderId) throws ParseException, JOSEException {
        OrderDetailResponseDTO orderDetailResponseDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDetailResponseDTO);
    }

    @PostMapping("/v2/orders")
    public ResponseEntity<List<OrderResponseDTO>> createOrders(@Valid @RequestBody List<OrderRequestDTO> orderRequestDTOs) throws ParseException, JOSEException {
    List<OrderResponseDTO> responses = new ArrayList<>();
    for (OrderRequestDTO orderRequestDTO : orderRequestDTOs) {
        OrderResponseDTO response = orderService.createOrder(orderRequestDTO);
        responses.add(response);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(responses);
}
}