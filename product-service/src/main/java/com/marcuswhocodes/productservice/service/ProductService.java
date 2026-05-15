package com.marcuswhocodes.productservice.service;

import com.marcuswhocodes.productservice.domain.dtos.ProductDto;

import java.util.UUID;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(UUID productId);
    void deleteProductById(UUID productId);
}
