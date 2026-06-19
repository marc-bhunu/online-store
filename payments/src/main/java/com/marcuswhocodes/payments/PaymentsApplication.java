package com.marcuswhocodes.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsApplication.class, args);
        System.out.println("Payments Application is running!!!");
    }

}
