package ssi.backend.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;

import jakarta.transaction.Transactional;
import ssi.backend.dtos.OrderItemDTO;
import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.dtos.OrderResponseDTO;
import ssi.backend.dtos.SellerDTO;
import ssi.backend.entities.*;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ConfilctResourceException;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.CartRepository;
import ssi.backend.repositories.OrderItemRepository;
import ssi.backend.repositories.OrderRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.repositories.UserRepository;

@Service
public class OrderService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) throws ParseException, JOSEException {

        if (orderRequestDTO.getBuyerId() == null ||
                orderRequestDTO.getSellerId() == null ||
                orderRequestDTO.getShippingAddress() == null ||
                orderRequestDTO.getOrderItems() == null) {
            throw new BadRequestException("Missing request parameters.");
        }

        if (orderRequestDTO.getOrderItems().isEmpty()) {
            throw new BadRequestException("Invalid request parameters.");

        }

        // Validate และหา User
        User buyer = userRepository.findById(orderRequestDTO.getBuyerId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Buyer not found with id: " + orderRequestDTO.getBuyerId()));

        User seller = userRepository.findById(orderRequestDTO.getSellerId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Seller not found with id: " + orderRequestDTO.getSellerId()));

        // Validate quantity และเตรียม OrderItem ก่อนการ save
        List<OrderItem> orderItems = new ArrayList<>();
        Integer totalAmount = 0;

        for (OrderItemDTO itemDTO : orderRequestDTO.getOrderItems()) {
            if (itemDTO.getSaleItemId() == null || itemDTO.getPrice() == null || itemDTO.getQuantity() == null) {
                throw new BadRequestException("Missing request parameters in orderItems");
            }

            if (itemDTO.getPrice() < 0 || itemDTO.getQuantity() <= 0) {
                throw new BadRequestException("Invalid request parameters in orderItems");
            }

            Product product = productRepository.findById(itemDTO.getSaleItemId())
                    .orElseThrow(() -> new ItemNotFoundException(
                            "Sale item not found with id: " + itemDTO.getSaleItemId()));

            // ห้ามซื้อสินค้าที่ตัวเองเป็นเจ้าของ
            if (product.getSeller() != null && buyer.getId().equals(product.getSeller().getId())) {
                throw new ConfilctResourceException(
                        "Cannot order product id " + product.getId() + " because buyer is the owner of the product.");
            }

            // ตรวจสอบ quantity ก่อน
            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new ConfilctResourceException("Quantity not enough for product id: " + itemDTO.getSaleItemId());
            }

            // เตรียม OrderItem (ยังไม่ save)
            OrderItem orderItem = new OrderItem();
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

            orderItems.add(orderItem);
            totalAmount += orderItem.getTotalPrice();
        }

        // สร้างและ save Order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setShippingAddress(orderRequestDTO.getShippingAddress());
        order.setOrderNote(orderRequestDTO.getOrderNote());
        order.setStatus(orderRequestDTO.getOrderStatus());
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Save OrderItem และ update Product quantity
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);

            // อัพเดท quantity ของสินค้า
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() - orderItem.getQuantity());
            productRepository.save(product);

            cartRepository.deleteByUserIdAndProductId(buyer.getId(), product.getId());
        }

        // สร้าง OrderResponseDTO
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
        orderResponseDTO.setShippingAddress(savedOrder.getShippingAddress());
        orderResponseDTO.setOrderNote(savedOrder.getOrderNote());

        // สร้าง OrderItemDTO list
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setNo(orderItem.getId());
            dto.setSaleItemId(orderItem.getProduct().getId());
            dto.setPrice(orderItem.getUnitPrice());
            dto.setQuantity(orderItem.getQuantity());
            dto.setDescription(orderItem.getDescription());
            orderItemDTOs.add(dto);
        }
        orderResponseDTO.setOrderItems(orderItemDTOs);
        orderResponseDTO.setOrderStatus(savedOrder.getStatus());

        return orderResponseDTO;
    }
}
