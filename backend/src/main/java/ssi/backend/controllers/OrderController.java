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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.dtos.OrderResponseDTO;
import ssi.backend.services.OrderService;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}", allowCredentials = "true")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PreAuthorize("isAuthenticated()")
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