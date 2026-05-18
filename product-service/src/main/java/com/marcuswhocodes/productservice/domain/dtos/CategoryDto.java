package com.marcuswhocodes.productservice.domain.dtos;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDto {
    private String name;
    private CategoryDto parent;

}
