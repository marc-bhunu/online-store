package com.marcuswhocodes.productservice.service;

import com.marcuswhocodes.productservice.domain.dtos.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto, List<MultipartFile> images);
    ProductDto getProductById(UUID productId);
    void deleteProductById(UUID productId);
    List<ProductDto> getAllProducts();
}
