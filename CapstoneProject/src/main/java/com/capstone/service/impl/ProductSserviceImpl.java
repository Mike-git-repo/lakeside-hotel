package com.capstone.service.impl;

import com.capstone.handler.OptimisticLockingFailureException;
import com.capstone.model.Product;
import com.capstone.repository.ProductRepository;
import com.capstone.service.ProductService;
import com.capstone.utils.Result;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductSserviceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public Product updateStockWithOptimisticLock(Long productId, int quantity) {

        int maxRetries = 3;  // 最大重试次数
        int attempt = 0;
        boolean success = false;
        Product product = null;

        while (attempt < maxRetries && !success) {
            try {
                attempt++;

                product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getStock() < quantity) {
                    throw new RuntimeException("Insufficient stock");
                }

                product.setStock(product.getStock() - quantity);
                product = productRepository.save(product);  // @Version 将确保更新时版本匹配

                success = true;  // 成功后设置为true，结束重试
            } catch (OptimisticLockingFailureException e) {
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to update stock after " + maxRetries + " retries. Please try again.");
                }
                System.out.println("Optimistic lock failure on attempt " + attempt + ". Retrying...");
                // 你可以在此处加入延迟（如指数退避）以减轻高并发下的冲突
                try {
                    Thread.sleep(50 * attempt);  // 每次重试时等待的时间增加
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();  // 恢复线程中断状态
                }
            }
        }
        return product;
        }

    @Transactional
    public Product updateStockWithPessimisticLock(Long productId, int quantity) {
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);
        return productRepository.save(product);
    }

    @Override
    public Result<Object> createProduct(Product product) {
        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if (existingProduct.isPresent()) {
            throw new RuntimeException("Product with name " + product.getName() + " already exists");
        }
        Product savedProduct = new Product();
        BeanUtils.copyProperties(product,savedProduct);
        productRepository.save(savedProduct);

        return Result.success();
    }


}

