package com.capstone.service.impl;

import com.capstone.handler.OptimisticLockingFailureException;
import com.capstone.model.*;
import com.capstone.repository.OrderRepository;
import com.capstone.repository.ProductRepository;
import com.capstone.repository.UserRepository;
import com.capstone.service.OrderService;
import com.capstone.utils.Result;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Transactional
    public String placeOrderWithOptimisticLock(Long userId, List<OrderItems> orderItemsList) {
        int maxRetries = 3;  // 最大重试次数
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            try {
                attempt++;
                BigDecimal totalPrice = BigDecimal.ZERO;

                // 遍历所有订单项，检查库存并计算总价
                for (OrderItems orderItem : orderItemsList) {
                    Product product = productRepository.findById(orderItem.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    System.out.println("Loaded product: " + product);

                    if (product.getStock() == null) {
                        throw new RuntimeException("Stock value is null for product: " + product.getName());
                    }

                    // 检查库存
                    if (product.getStock() < orderItem.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName());
                    }

                    // 计算每个商品的总价，并累加到订单总价中
                    BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    totalPrice = totalPrice.add(itemPrice);

                    // 更新OrderItems的价格
                    orderItem.setPrice(product.getPrice());
                }

                // 检查用户余额
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                if (user.getBalance().compareTo(totalPrice) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }

                // 扣减用户余额和商品库存
                user.setBalance(user.getBalance().subtract(totalPrice));
                userRepository.save(user);  // 保存用户信息

                // 更新每个商品的库存
                for (OrderItems orderItem : orderItemsList) {
                    Product product = productRepository.findById(orderItem.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    product.setStock(product.getStock() - orderItem.getQuantity());
                    productRepository.save(product);  // 保存库存更新
                }

                // 创建订单并保存
                Order order = new Order();
                order.setUserId(userId);
                order.setTotalPrice(totalPrice);
                order.setStatus("COMPLETED");

                // 将OrderItems与Order关联
                for (OrderItems item : orderItemsList) {
                    item.setOrder(order);
                }

                orderRepository.save(order);  // 保存订单及其条目

                success = true;  // 标记成功，结束重试
                return "Order placed successfully, Order ID: " + order.getId();

            } catch (OptimisticLockingFailureException e) {
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to place order after " + maxRetries + " retries. Please try again.");
                }
                System.out.println("Optimistic lock failure on attempt " + attempt + ". Retrying...");
                try {
                    Thread.sleep(50 * attempt);  // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        throw new RuntimeException("Order placement failed after multiple retries.");
        }


    @Transactional
    public String placeOrderWithPessimisticLock(Long userId, List<OrderItems> orderItemsList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 遍历所有订单项，检查库存并计算总价
        for (OrderItems orderItem : orderItemsList) {
            Product product = orderItem.getProduct();

            // 使用悲观锁锁定商品行，防止并发修改
            product = productRepository.findByIdWithPessimisticLock(product.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // 检查库存
            if (product.getStock() < orderItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // 计算每个商品的总价
            BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);

            // 更新 OrderItems 的价格
            orderItem.setPrice(product.getPrice());
        }

        // 检查用户余额
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 扣减用户余额
        user.setBalance(user.getBalance().subtract(totalPrice));
        userRepository.save(user);  // 保存用户余额

        // 更新每个商品的库存
        for (OrderItems orderItem : orderItemsList) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() - orderItem.getQuantity());
            productRepository.save(product);  // 保存库存更新
        }

        // 创建订单并保存
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("COMPLETED");

        // 将 OrderItems 与 Order 关联
        for (OrderItems item : orderItemsList) {
            item.setOrder(order);
        }

        orderRepository.save(order);  // 保存订单及其条目

        return "Order placed successfully with pessimistic lock, Order ID: " + order.getId();
    }



    @Transactional
    public String purchaseProductWithOptimisticLock(Long userId, Long productId, int quantity) throws OptimisticLockException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        double totalPrice = product.getPrice().doubleValue() * quantity ;

        if (user.getBalance().doubleValue() < totalPrice) {
            throw new RuntimeException("Insufficient balance");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(totalPrice)) );
        product.setStock(product.getStock() - quantity);

        userRepository.save(user);
        productRepository.save(product);

        return "Purchase successful with optimistic lock";
    }

    @Transactional
    public String purchaseProductWithPessimisticLock(Long userId, Long productId, int quantity) {
        User user = userRepository.findByIdWithPessimisticLock(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        double totalPrice = product.getPrice().doubleValue() * quantity ;

        if (user.getBalance().doubleValue() < totalPrice) {
            throw new RuntimeException("Insufficient balance");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }


        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(totalPrice)) );
        product.setStock(product.getStock() - quantity);

        userRepository.save(user);
        productRepository.save(product);

        return "Purchase successful with pessimistic lock";
    }

    private final Lock lock = new ReentrantLock();


    public String purchaseProductWithLock(Long userId, Long productId, int quantity) {

            lock.lock();
            try {

                Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));


                if (product.getStock() < quantity) {
                    throw new RuntimeException("Insufficient stock");
                }


                product.setStock(product.getStock() - quantity);
                productRepository.save(product);


                BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));


                Order order = new Order();
                order.setUserId(userId);
                order.setTotalPrice(totalPrice);
                orderRepository.save(order);

                return "Purchase successful";
            } finally {

                lock.unlock();
            }
        }
}
