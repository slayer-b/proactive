package com.my.company.services;

import com.my.company.domain.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {

    CompletableFuture<List<Product>> listAll();

    CompletableFuture<Product> getById(String id);

    Product saveOrUpdate(Product product);

    void delete(String id);
}
