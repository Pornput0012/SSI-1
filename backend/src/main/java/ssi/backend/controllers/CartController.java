package ssi.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin(originPatterns = "${app.cors.allowedOrigins:*}", allowCredentials = "true")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SaleItemImageService saleItemImageService;


    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDto>> getCartById(@PathVariable int userId) {
        return ResponseEntity.ok(cartService.getAllCartById(userId));
    }
    

    @PostMapping("")
    public ResponseEntity<CartResponseSubDto> addToCart( @RequestBody CartRequestDto cartRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(cartRequestDto));
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponseSubDto>  updateCartByCartId( @PathVariable int cartId, @RequestBody CartRequestDto cartRequestDto) {

        return ResponseEntity.ok(cartService.updateQuantity(cartId,cartRequestDto.getQuantity()));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCartByCartId(@PathVariable int cartId) {
       cartService.removeCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
