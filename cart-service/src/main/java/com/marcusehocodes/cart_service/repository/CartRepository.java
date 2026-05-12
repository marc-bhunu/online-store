package com.marcusehocodes.cart_service.repository;

import com.marcusehocodes.cart_service.domain.documents.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends MongoRepository<Cart, UUID> {
    List<Cart> findCartByUserId(UUID userId);
}
