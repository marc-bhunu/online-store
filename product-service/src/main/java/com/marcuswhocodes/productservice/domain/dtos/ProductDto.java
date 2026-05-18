package com.marcuswhocodes.productservice.domain.dtos;

import com.marcuswhocodes.productservice.domain.entities.Category;
import com.marcuswhocodes.productservice.domain.entities.Inventory;
import com.marcuswhocodes.productservice.domain.enums.ProductStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private String name;
    private String description;
    private double price;
    private String currency;
    private List<ImageDto> images;
    private InventoryDto inventory;
    private ProductStatus status;
    private CategoryDto category;
}
