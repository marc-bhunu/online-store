package com.marcuswhocodes.productservice.controller;

import com.marcuswhocodes.productservice.domain.dtos.ProductDto;
import com.marcuswhocodes.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping("/all")
    private ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    private ResponseEntity<ProductDto> createProduct(
            @RequestPart("product") ProductDto productDto,
            @RequestPart("images")  List<MultipartFile> images
    ){
        ProductDto response = productService.createProduct(productDto, images);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    private ResponseEntity<ProductDto> getProductId(@PathVariable UUID productId){
        ProductDto response = productService.getProductById(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{productId}")
    private ResponseEntity<Void> deleteProduct(@PathVariable UUID productId){
        productService.deleteProductById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
