package ssi.backend.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;

import jakarta.transaction.Transactional;
import ssi.backend.dtos.BuyerDTO;
import ssi.backend.dtos.OrderBuyerPageDTO;
import ssi.backend.dtos.OrderDetailResponseDTO;
import ssi.backend.dtos.OrderItemDTO;
import ssi.backend.dtos.OrderRequestDTO;
import ssi.backend.dtos.OrderResponseDTO;
import ssi.backend.dtos.OrderSellerPageDTO;
import ssi.backend.dtos.OrderSellerResponseDTO;
import ssi.backend.dtos.SellerDTO;
import ssi.backend.dtos.SellerDetailDTO;
import ssi.backend.entities.*;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ConfilctResourceException;
import ssi.backend.exceptions.ForbiddenException;
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

    public OrderBuyerPageDTO getOrderBuyer(Integer buyerId, Integer page, Integer size, String sortField,
                                           String sortDirection, List<String> filterBrands, String searchKeyWord) throws ParseException, JOSEException {

        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + buyerId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();

        if (!userId.equals(buyerId)) {
            throw new ForbiddenException("Request buyer id not matched with id in access token.");
        }

        boolean hasSortField = sortField != null && !sortField.isBlank();
        boolean sortDesc = "desc".equalsIgnoreCase(sortDirection);

        if (page != null || size != null) {
            System.out.println("ji");
        }
        Sort sort;
        if (hasSortField) {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc(sortField))
                    : Sort.by(Sort.Order.asc(sortField));
            sort = sort.and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc("orderDate")).and(Sort.by(Sort.Order.desc("id")))
                    : Sort.by(Sort.Order.asc("orderDate")).and(Sort.by(Sort.Order.asc("id")));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.findFilteredBuyerOrders(
                user,
                (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                searchKeyWord,
                pageable
        );

        List<OrderResponseDTO> orderDTOs = orderPage.getContent().stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setBuyerId(order.getBuyer().getId());

            SellerDTO sellerDTO = new SellerDTO();
            sellerDTO.setId(order.getSeller().getId());
            sellerDTO.setNickname(order.getSeller().getNickname());
            dto.setSeller(sellerDTO);

            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentDate(order.getPaymentDate());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setOrderNote(order.getOrderNote());
            dto.setOrderStatus(order.getStatus());

            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(oi -> {
                OrderItemDTO i = new OrderItemDTO();
                i.setNo(oi.getId());
                i.setSaleItemId(oi.getProduct().getId());
                i.setPrice(oi.getUnitPrice());
                i.setQuantity(oi.getQuantity());
                i.setDescription(oi.getDescription());
                return i;
            }).toList();

            dto.setOrderItems(itemDTOs);
            return dto;

        }).toList();

        OrderBuyerPageDTO response = new OrderBuyerPageDTO();
        response.setContent(orderDTOs);
        response.setFirst(orderPage.isFirst());
        response.setLast(orderPage.isLast());
        response.setTotalPages(orderPage.getTotalPages());
        response.setTotalElements((int) orderPage.getTotalElements());
        response.setSize(orderPage.getSize());
        response.setSort(sort.toString());
        response.setNumber(orderPage.getNumber());
        response.setSort((sortField == null ? "orderDate" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));

        return response;
    }

    public OrderSellerPageDTO getOrderSeller(Integer sellerId, Integer page, Integer size, String sortField,
                                             String sortDirection) throws ParseException, JOSEException {
        userRepository.findById(sellerId)
                .orElseThrow(() -> new ItemNotFoundException("User not found with id: " + sellerId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();

        if (!userId.equals(sellerId)) {
            throw new ForbiddenException("User id in access token not matched with resource id.");
        }

        boolean hasSortField = sortField != null && !sortField.isBlank();
        boolean sortDesc = "desc".equalsIgnoreCase(sortDirection);

        Sort sort;
        if (hasSortField) {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc(sortField))
                    : Sort.by(Sort.Order.asc(sortField));
            sort = sort.and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc("orderDate")).and(Sort.by(Sort.Order.desc("id")))
                    : Sort.by(Sort.Order.asc("orderDate")).and(Sort.by(Sort.Order.asc("id")));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.findAllSellerOrder(sellerId, pageable);

        System.out.println(orderPage.getContent().isEmpty());
        List<OrderSellerResponseDTO> orderDTOs = orderPage.getContent().stream().map(order -> {
            OrderSellerResponseDTO dto = new OrderSellerResponseDTO();
            dto.setId(order.getId());
            dto.setSellerId(sellerId);

            BuyerDTO buyerDTO = new BuyerDTO();
            buyerDTO.setId(order.getBuyer().getId());
            buyerDTO.setUserName(order.getBuyer().getNickname());
            dto.setBuyer(buyerDTO);

            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentDate(order.getPaymentDate());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setOrderNote(order.getOrderNote());
            dto.setOrderStatus(order.getStatus());
            dto.setViewOrder(order.getViewOrder());

            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .filter(oi -> oi.getProduct().getSeller().getId().equals(sellerId))
                    .map(oi -> {
                        OrderItemDTO i = new OrderItemDTO();
                        i.setNo(oi.getId());
                        i.setSaleItemId(oi.getProduct().getId());
                        i.setPrice(oi.getUnitPrice());
                        i.setQuantity(oi.getQuantity());
                        i.setDescription(oi.getDescription());
                        return i;
                    }).toList();

            dto.setOrderItems(itemDTOs);
            return dto;

        }).toList();

        OrderSellerPageDTO response = new OrderSellerPageDTO();
        response.setContent(orderDTOs);

        response.setFirst(orderPage.isFirst());
        response.setLast(orderPage.isLast());
        response.setTotalPages(orderPage.getTotalPages());
        response.setTotalElements((int) orderPage.getTotalElements());
        response.setSize(orderPage.getSize());
        response.setSort(sort.toString());
        response.setNumber(orderPage.getNumber());
        response.setSort((sortField == null ? "orderDate" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));

        return response;
    }

    public OrderSellerResponseDTO sellerViewOrder(Integer sellerId, Integer id) throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();

        if (!userId.equals(sellerId)) {
            throw new ForbiddenException("User id in access token not matched with resource id.");
        }

        Order existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder == null) {
            throw new ItemNotFoundException("Order not found with id: " + id);
        }

        existingOrder.setViewOrder(true);


        orderRepository.save(existingOrder);
        OrderSellerResponseDTO dto = new OrderSellerResponseDTO();
        dto.setId(existingOrder.getId());
        dto.setSellerId(sellerId);
        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setId(existingOrder.getBuyer().getId());
        buyerDTO.setUserName(existingOrder.getBuyer().getNickname());
        dto.setBuyer(buyerDTO);
        dto.setOrderDate(existingOrder.getOrderDate());
        dto.setPaymentDate(existingOrder.getPaymentDate());
        dto.setShippingAddress(existingOrder.getShippingAddress());
        dto.setOrderNote(existingOrder.getOrderNote());
        dto.setOrderStatus(existingOrder.getStatus());
        dto.setViewOrder(existingOrder.getViewOrder());
        return dto;
    }

    public OrderDetailResponseDTO getOrderSellerById(Integer orderId) throws ParseException, JOSEException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found with id: " + orderId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();

        boolean isBuyer = order.getBuyer().getId().equals(userId);
        boolean isSeller = order.getSeller().getId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new ForbiddenException(
                    "User id: " + userId + " not an owner (seller or buyer) of order id: " + orderId);
        }

        OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyer().getId());
        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setId(order.getBuyer().getId());
        buyerDTO.setUserName(order.getBuyer().getNickname());
        dto.setBuyer(buyerDTO);

        SellerDetailDTO sellerDTO = new SellerDetailDTO();
        sellerDTO.setId(order.getSeller().getId());
        sellerDTO.setEmail(order.getSeller().getEmail());
        sellerDTO.setFullName(order.getSeller().getFullname());
        sellerDTO.setUserType(order.getSeller().getUserType());
        sellerDTO.setNickName(order.getSeller().getNickname());
        dto.setSeller(sellerDTO);

        dto.setOrderDate(order.getOrderDate());
        dto.setPaymentDate(order.getPaymentDate());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setOrderNote(order.getOrderNote());
        dto.setOrderStatus(order.getStatus());

        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(oi -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setNo(oi.getId());
            i.setSaleItemId(oi.getProduct().getId());
            i.setPrice(oi.getUnitPrice());
            i.setQuantity(oi.getQuantity());
            i.setDescription(oi.getDescription());
            return i;
        }).toList();

        dto.setOrderItems(itemDTOs);

        return dto;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) throws ParseException, JOSEException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();

        if (!userId.equals(orderRequestDTO.getBuyerId())) {
            throw new ForbiddenException("Request buyer id not matched with id in access token.");
        }

        if (orderRequestDTO.getBuyerId() == null ||
                orderRequestDTO.getSellerId() == null ||
                orderRequestDTO.getShippingAddress() == null ||
                orderRequestDTO.getOrderItems() == null) {
            throw new BadRequestException("Missing request parameters.");
        }

        if (orderRequestDTO.getOrderItems().isEmpty()) {
            throw new BadRequestException("Invalid request parameters.");

        }


        User buyer = userRepository.findById(orderRequestDTO.getBuyerId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Buyer not found with id: " + orderRequestDTO.getBuyerId()));

        User seller = userRepository.findById(orderRequestDTO.getSellerId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Seller not found with id: " + orderRequestDTO.getSellerId()));

        if (orderRequestDTO.getBuyerId().equals(orderRequestDTO.getSellerId())) {
            throw new BadRequestException(
                    "Cannot create order because buyer and seller are the same user.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Integer totalAmount = 0;
        boolean allItemsAvailable = true;

        for (OrderItemDTO itemDTO : orderRequestDTO.getOrderItems()) {
            if (itemDTO.getSaleItemId() == null || itemDTO.getPrice() == null || itemDTO.getQuantity() == null) {
                throw new BadRequestException("Missing request parameters in orderItems");
            }

            if (itemDTO.getPrice() < 0 || itemDTO.getQuantity() <= 0) {
                throw new BadRequestException("Invalid request parameters in orderItems");
            }

            Product product = productRepository.findById(itemDTO.getSaleItemId())
                    .orElseThrow(() -> new ItemNotFoundException(
                            "Product not found with id: " + itemDTO.getSaleItemId()));

            if (product.getSeller() == null ||
                    !product.getSeller().getId().equals(orderRequestDTO.getSellerId())) {
                System.out.println("product seller is " +  product.getSeller().getId());
                System.out.println("orderRequestDTO.getSellerId() " +  orderRequestDTO.getSellerId());
                System.out.println(product.getSeller().getId().equals(orderRequestDTO.getSellerId()));

                System.out.println(product.getSeller() == null);
                throw new BadRequestException(
                        "Product id " + product.getId() + " does not exist for seller id: " + orderRequestDTO.getSellerId());
            }

            // ตรวจสอบ quantity และทำเครื่องหมายว่ามีสินค้าไม่พอหรือไม่
            if (product.getQuantity() < itemDTO.getQuantity()) {
                allItemsAvailable = false;
            }

            // เตรียม OrderItem (ยังไม่ save)
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(itemDTO.getPrice());
            orderItem.setTotalPrice(itemDTO.getPrice() * itemDTO.getQuantity());
            orderItem.setDescription(itemDTO.getDescription());

            // คัดลอกข้อมูลจาก Product เพื่อป้องกันการเปลี่ยนแปลงในอนาคต
            orderItem.setBrandName(product.getBrand().getName());
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
        order.setStatus(allItemsAvailable ? "COMPLETED" : "CANCELLED");
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Save OrderItem และ update Product quantity
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);

            if (allItemsAvailable) {
                // อัพเดท quantity ของสินค้า
                Product product = orderItem.getProduct();
                product.setQuantity(product.getQuantity() - orderItem.getQuantity());
                productRepository.save(product);

                cartRepository.deleteByUserIdAndProductId(buyer.getId(), product.getId());
            }
        }

        // สร้าง OrderResponseDTO
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(savedOrder.getId());
        orderResponseDTO.setBuyerId(buyer.getId());

        // สร้าง SellerDTO
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setId(seller.getId());
        sellerDTO.setNickname(seller.getNickname());
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
