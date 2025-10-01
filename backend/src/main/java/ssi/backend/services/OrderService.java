package ssi.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ssi.backend.dtos.OrderItemDTO;
import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.dtos.OrderResponseDTO;
import ssi.backend.dtos.SellerDTO;
import ssi.backend.entities.*;
import ssi.backend.exceptions.ItemNotFoundException;
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

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // 1. Validate และหา User
        User buyer = userRepository.findById(orderRequestDTO.getBuyerId())
            .orElseThrow(() -> new ItemNotFoundException("Buyer not found with id: " + orderRequestDTO.getBuyerId()));
        
        User seller = userRepository.findById(orderRequestDTO.getSellerId())
            .orElseThrow(() -> new ItemNotFoundException("Seller not found with id: " + orderRequestDTO.getSellerId()));

        // 2. สร้าง Order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setShippingAddress(orderRequestDTO.getShippingAddress());
        order.setOrderNote(orderRequestDTO.getOrderNote());
        order.setStatus(orderRequestDTO.getOrderStatus() != null ? orderRequestDTO.getOrderStatus() : "COMPLETED");

        // 3. Save Order
        Order savedOrder = orderRepository.save(order);

        // 4. สร้าง OrderItems และคำนวณยอดรวม
        List<OrderItem> orderItems = new ArrayList<>(); // สร้าง list ใหม่เพื่อเก็บ OrderItem
        Integer totalAmount = 0;
        
        // 5. วน loop สร้าง OrderItem แต่ละรายการ
        for (OrderItemDTO itemDTO : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getSaleItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Product not found"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(itemDTO.getPrice());
            orderItem.setTotalPrice(itemDTO.getPrice() * itemDTO.getQuantity());
            orderItem.setDescription(itemDTO.getDescription());
            
            // คัดลอกข้อมูลจาก Product เพื่อป้องกันการเปลี่ยนแปลงในอนาคต
            orderItem.setModel(product.getModel());
            orderItem.setRamGb(product.getRamGb());
            orderItem.setScreenSizeInch(product.getScreenSizeInch());
            orderItem.setStorageGb(product.getStorageGb());
            orderItem.setColor(product.getColor());
            
            orderItems.add(orderItem); // เพิ่ม OrderItem เข้าไปใน list
            totalAmount += orderItem.getTotalPrice(); // รวมยอดเงิน
        }
        
        // 5. บันทึก OrderItems ทั้งหมด
        orderItemRepository.saveAll(orderItems);
        
        // อัปเดต totalAmount ใน Order
        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        // 6. สร้าง OrderResponseDTO
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(savedOrder.getId());
        orderResponseDTO.setBuyerId(buyer.getId());

        // สร้าง SellerDTO
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setId(seller.getId());
        sellerDTO.setUserName(seller.getNickname());
        orderResponseDTO.setSeller(sellerDTO);

        orderResponseDTO.setOrderDate(savedOrder.getOrderDate());
        orderResponseDTO.setPaymentDate(savedOrder.getPaymentDate());
        orderResponseDTO.setOrderNote(savedOrder.getOrderNote());

        List<OrderItemDTO> orderItemDTO = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setNo(orderItem.getId());
            dto.setSaleItemId(orderItem.getProduct().getId());
            dto.setPrice(orderItem.getUnitPrice());
            dto.setQuantity(orderItem.getQuantity());
            dto.setDescription(orderItem.getDescription());
            orderItemDTO.add(dto);
        }
        orderResponseDTO.setOrderItems(orderItemDTO);
        orderResponseDTO.setOrderStatus(savedOrder.getStatus());

        return orderResponseDTO;
    }
}
