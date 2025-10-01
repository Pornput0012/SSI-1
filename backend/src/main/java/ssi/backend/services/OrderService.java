package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.CartRepository;
import ssi.backend.repositories.OrderItemRepository;
import ssi.backend.repositories.OrderRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.repositories.UserRepository;
import ssi.backend.utils.ListMapper;

@Service
public class OrderService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public OrderRequestDTO createOrder(OrderRequestDTO orderRequestDTO, Integer userId) {
        if (orderRequestDTO == null) {
            throw new RuntimeException("Request must not be null");
        }
    }
}
