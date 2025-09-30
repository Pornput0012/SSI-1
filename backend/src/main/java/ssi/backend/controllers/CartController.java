package ssi.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.CartResponseDto;
import ssi.backend.dtos.CartResponseSubDto;
import ssi.backend.services.CartService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/carts")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/{userId}")
    public List<CartResponseDto> getCartById(@PathVariable int userId) {
        return cartService.getAllCartById(userId);
    }
}
