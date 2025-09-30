package ssi.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.CartRequestDto;
import ssi.backend.dtos.CartResponseDto;
import ssi.backend.dtos.CartResponseSubDto;
import ssi.backend.entities.SaleItemImage;
import ssi.backend.services.CartService;
import ssi.backend.services.FileService;
import ssi.backend.services.SaleItemImageService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/carts")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SaleItemImageService saleItemImageService;


    @GetMapping("/{userId}")
    public List<CartResponseDto> getCartById(@PathVariable int userId) {
        return cartService.getAllCartById(userId);
    }


    @PostMapping("")
    public CartResponseSubDto addToCart( @RequestBody CartRequestDto cartRequestDto) {

        return cartService.addToCart(cartRequestDto);
    }
}
