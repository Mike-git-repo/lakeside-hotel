package com.capstone.service;

import com.capstone.model.OrderItems;
import jakarta.persistence.OptimisticLockException;

import java.util.List;

public interface OrderService {

    public String placeOrderWithOptimisticLock(Long userId, List<OrderItems> orderItemsList) throws OptimisticLockException;

    public String placeOrderWithPessimisticLock(Long userId, List<OrderItems> orderItemsList);

    String purchaseProductWithOptimisticLock(Long userId, Long productId, int quantity);

    String purchaseProductWithPessimisticLock(Long userId, Long productId, int quantity);

    String purchaseProductWithLock(Long userId, Long productId, int quantity);
}
