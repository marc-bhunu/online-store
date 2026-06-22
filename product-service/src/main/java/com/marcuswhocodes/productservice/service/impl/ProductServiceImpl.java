package com.marcuswhocodes.productservice.service.impl;

import com.marcuswhocodes.productservice.domain.dtos.*;
import com.marcuswhocodes.productservice.domain.entities.Category;
import com.marcuswhocodes.productservice.domain.entities.Images;
import com.marcuswhocodes.productservice.domain.entities.Inventory;
import com.marcuswhocodes.productservice.domain.entities.Product;
import com.marcuswhocodes.productservice.domain.enums.ProductStatus;
import com.marcuswhocodes.productservice.repository.ProductRepository;
import com.marcuswhocodes.productservice.service.ProductService;
import com.marcuswhocodes.productservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;
    @Override
    public ProductDto createProduct(ProductDto productDto, List<MultipartFile> images) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .currency(productDto.getCurrency())
                .status(ProductStatus.ACTIVE)
                .build();

        List<Images> imageList = images.stream()
                .map((file) -> {
                    int i = 0;
                    String imageKeyname = null;
                    try {
                        imageKeyname = s3Service.uploadFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return Images.builder()
                            .url(imageKeyname)
                            .product(product)
                            .isPrimary(false)
                            .sortOrder(String.valueOf(i++))
                            .build();
                })
                .toList();

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(productDto.getInventory().getQuantity())
                .quantityReserved(productDto.getInventory().getQuantityReserved())
                .quantityAvailable(productDto.getInventory().getQuantityAvailable())
                .build();

        Category category = mapCategoryDtoToEntity(productDto.getCategory());

        product.setImages(imageList);
        product.setCategory(category);
        product.setInventory(inventory);
        return mapToDto(productRepository.save(product));
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToDto(product);
    }

    @Override
    public void deleteProductById(UUID productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToDto).toList();
    }

    @Override
    public void reserveProduct(List<ReverseProductDto> products) {
        for (ReverseProductDto productDto : products) {
            Product product = productRepository
                    .findById(productDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getInventory().getQuantityAvailable() < productDto.getQuantity()) {
                throw new RuntimeException("Not enough inventory for product: " + product.getName());
            }

            product.getInventory().setQuantityReserved(
                    product.getInventory().getQuantityReserved() + productDto.getQuantity()
            );
            product.getInventory().setQuantityAvailable(
                    product.getInventory().getQuantityAvailable() - productDto.getQuantity()
            );
            productRepository.save(product);
        }
    }

    private ProductDto mapToDto(Product save) {
        return ProductDto.builder()
                .name(save.getName())
                .description(save.getDescription())
                .price(save.getPrice())
                .currency(save.getCurrency())
                .status(save.getStatus())
                .images(save.getImages().stream()
                        .map(image ->  ImageDto.builder()
                                .url(s3Service.getPresignedUrl(image.getUrl()))
                                .isPrimary(image.isPrimary())
                                .sortOrder(image.getSortOrder())
                                .build())
                        .toList())
                .inventory(InventoryDto.builder()
                        .quantity(save.getInventory().getQuantity())
                        .quantityReserved(save.getInventory().getQuantityReserved())
                        .quantityAvailable(save.getInventory().getQuantityAvailable())
                        .build())
                .category(CategoryDto.builder()
                        .name(save.getCategory().getName())
                        .build())
                .build();
    }

    private Category mapCategoryDtoToEntity(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();
        if (categoryDto.getParent() != null) {
            category.setParent(mapCategoryDtoToEntity(categoryDto.getParent()));
        }
        return category;
    }
}