package ssi.backend.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import ssi.backend.dtos.CartRequestDto;
import ssi.backend.dtos.CartResponseDto;
import ssi.backend.dtos.CartResponseSubDto;
import ssi.backend.entities.*;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.CartRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.repositories.UserRepository;
import ssi.backend.utils.ListMapper;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private Validator validator;
    @Autowired
    private SaleItemImageService saleItemImageService;

    public List<CartResponseDto> getAllCartById(int userId) {
//        if () {
//            throw new IllegalArgumentException("userId is null");
//        }
        List<Cart> currentCart = cartRepository.findAllByUserId(userId);
        List<CartResponseDto> cartResponseDtos = new ArrayList<>();

        Set<String> sellerNameSet = new HashSet<>();
        currentCart.stream().forEach(cart -> {
            //cart -> id ...
            String sellerName = cart.getProduct().getSeller().getNickname();
            sellerNameSet.add(sellerName);
        });


        sellerNameSet.stream().forEach(sellerName -> {
            List<Cart> cartBySeller = currentCart.stream().filter((cart) -> sellerName.equals(cart.getProduct().getSeller().getNickname())).toList();

            CartResponseDto cartResponseDto = new CartResponseDto();
            cartResponseDto.setProductSellerNickname(sellerName);
            List<CartResponseSubDto> cartResponseSubDtos = listMapper.mapList(cartBySeller, CartResponseSubDto.class, modelMapper);

            cartResponseDto.setCart(cartResponseSubDtos);

            cartResponseDtos.add(cartResponseDto);
        });
        return cartResponseDtos;
    }


    public CartResponseSubDto addToCart(CartRequestDto cartRequestDto) {

        System.out.println(cartRequestDto.getQuantity());
        SaleItemImage saleItemImage = saleItemImageService.findSaleItemImageByNameV2(cartRequestDto.getImageFileName());
        User existingUser = userRepository.findById(cartRequestDto.getUserId()).orElseThrow(() -> new ItemNotFoundException("User id " + cartRequestDto.getUserId() + " does not exist"));
        ;

        Product existingProduct = productRepository.findById(cartRequestDto.getProductId()).orElseThrow(() -> new ItemNotFoundException("Product id " + cartRequestDto.getProductId() + " does not exist"));

        Brand existingBrand = brandRepository.findById(existingProduct.getBrand().getId()).orElseThrow(() -> new ItemNotFoundException("Brand id " + existingProduct.getBrand().getId() + " does not exist"));

        Cart existCart = cartRepository.findAllByProductIdAndUserId(existingProduct.getId(), existingUser.getId());
        Cart saveCart = existCart == null ? new Cart() : existCart;
        saveCart.setUser(existingUser);
        saveCart.setProduct(existingProduct);
        System.out.println(existingProduct.getSeller().getNickname());

        if (saleItemImage == null) {
            saveCart.setImage(null);
        } else {
            SaleItemImage image = saleItemImageService.findSaleItemImage(saleItemImage.getId());
            Boolean imageExist = fileService.fileExists(existingProduct, saleItemImageService.findSaleItemImage(saleItemImage.getId()).getFileName());
            saveCart.setImage(image);
        }

        if (existCart == null) {
            saveCart.setId(null);
            saveCart.setQuantity(cartRequestDto.getQuantity());
        } else {
            saveCart.setQuantity(saveCart.getQuantity() + cartRequestDto.getQuantity());

        }

        Cart cartResponseDto = cartRepository.save(saveCart);
        System.out.println(cartResponseDto.getProduct().getSeller().getNickname());
        return modelMapper.map(cartResponseDto, CartResponseSubDto.class);
    }


    public CartResponseSubDto updateQuantity(int cartId ,int quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ItemNotFoundException("Cart id " + cartId));

        cart.setQuantity(quantity);

        return  modelMapper.map(cartRepository.save(cart), CartResponseSubDto.class);
    }

    public void removeCart(int cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ItemNotFoundException("Cart id " + cartId));
        cartRepository.delete(cart);
    }
}
