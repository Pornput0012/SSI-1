package ssi.backend.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import ssi.backend.dtos.*;
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
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) String searchKeyWord) throws ParseException, JOSEException {

        OrderBuyerPageDTO orderBuyerPage = orderService.getOrderBuyer(buyerId, page, size, sortField, sortDirection, filterBrands, searchKeyWord);
        return ResponseEntity.ok(orderBuyerPage);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/v2/sellers/{sid}/orders")
    public ResponseEntity<OrderSellerPageDTO> getOrderSeller(@PathVariable("sid") Integer sellerId,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection)
            throws ParseException, JOSEException {

        OrderSellerPageDTO orderSellerPage = orderService.getOrderSeller(sellerId, page, size, sortField,
                sortDirection);
        return ResponseEntity.ok(orderSellerPage);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/v2/sellers/{sid}/orders/{id}")
    public ResponseEntity<OrderSellerResponseDTO> patchViewOrderSeller(@PathVariable("sid") Integer sellerId,
            @PathVariable("id") Integer id) throws ParseException, JOSEException {
        return ResponseEntity.ok(orderService.sellerViewOrder(sellerId, id));
    }

    @GetMapping("/v2/orders/{id}")
    public ResponseEntity<OrderDetailResponseDTO> getOrderById(@PathVariable("id") Integer orderId)
            throws ParseException, JOSEException {
        OrderDetailResponseDTO orderDetailResponseDTO = orderService.getOrderSellerById(orderId);
        return ResponseEntity.ok(orderDetailResponseDTO);
    }

    @PostMapping("/v2/orders")
    public ResponseEntity<List<OrderResponseDTO>> createOrders(
            @Valid @RequestBody List<OrderRequestDTO> orderRequestDTOs) throws ParseException, JOSEException {
        List<OrderResponseDTO> responses = new ArrayList<>();
        for (OrderRequestDTO orderRequestDTO : orderRequestDTOs) {
            OrderResponseDTO response = orderService.createOrder(orderRequestDTO);
            responses.add(response);
            System.out.println("sellerMiss:" + response.getSeller().getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}