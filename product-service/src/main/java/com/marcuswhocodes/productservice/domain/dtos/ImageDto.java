package com.marcuswhocodes.productservice.domain.dtos;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImageDto {
    private String url;
    private boolean isPrimary;
    private String sortOrder;
}
