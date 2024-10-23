package com.capstone.service;

import com.capstone.model.Product;
import com.capstone.utils.Result;

import java.math.BigDecimal;

public interface ProductService {

    public Product updateStockWithPessimisticLock(Long productId, int quantity);

    public Product updateStockWithOptimisticLock(Long productId, int quantity);

    Result<Object> createProduct(Product product);
}
