package com.my.company.services;

import com.my.company.repositories.ProductRepository;
import com.my.company.domain.Product;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService, InitializingBean {

    private static final Object LIST_LOCK = new Object();;
    private static final Map<String, String> BY_ID_LOCK = new HashMap();;

    private ProductRepository productRepository;
    private ProductService productService;
    private CompletableFuture<List<Product>> listCached;
    private static final Map<String, CompletableFuture<Product>> BY_ID_CACHE = new HashMap();;

    @Override
    public void afterPropertiesSet() throws Exception {
        // put list all result into cache
        productService.listAll();
        productService.getById("1");
        productService.getById("2");
    }

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public CompletableFuture<List<Product>> listAll() {
        if (listCached == null) {
            synchronized (LIST_LOCK) {
                if (listCached == null) {
                    listCached = CompletableFuture.supplyAsync(() -> {
                        List<Product> products = new ArrayList<>();
                        try {
                            System.out.println("wait list");
                            Thread.sleep(10000);
                            System.out.println("go list");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        productRepository.findAll().forEach(products::add); //fun with Java 8
                        return products;
                    });
                }
            }
        }
        return listCached;
    }

    @Override
    public CompletableFuture<Product> getById(String id) {
        if (needsCalculate(id)) {
            synchronized (BY_ID_LOCK.get(id)) {
                return CompletableFuture.supplyAsync(() -> {
                    Product product = productRepository.findById(id).orElse(null);
                    try {
                        System.out.println("wait one");
                        Thread.sleep(2000);
                        System.out.println("go one");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return product;
                });
            }
        }
        return BY_ID_CACHE.get(id);
    }

    private synchronized boolean needsCalculate(String id) {
        if (BY_ID_LOCK.get(id) == null) {
            BY_ID_LOCK.put(id, id);
            return true;
        }
        return false;
    }

    @Override
    public Product saveOrUpdate(Product product) {
        productRepository.save(product);
        return product;
    }

    @Override
    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
