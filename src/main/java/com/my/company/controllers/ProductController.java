package com.my.company.controllers;

import com.my.company.domain.Product;
import com.my.company.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class ProductController {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping("/")
    public String redirToList(){
        return "redirect:/product/list";
    }

    @RequestMapping({"/product/list", "/product"})
    @ResponseBody
    public List<Product> listProducts(Model model) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Product>> future = productService.listAll();
        System.out.println(future.get());
        return future.get();
    }

    @RequestMapping("/product/show/{id}")
    @ResponseBody
    public Product getProduct(@PathVariable String id, Model model) throws ExecutionException, InterruptedException {
        return productService.getById(id).get();
    }

    @RequestMapping("/product/delete/{id}")
    public String delete(@PathVariable String id){
        productService.delete(id);
        return "redirect:/product/list";
    }

}
