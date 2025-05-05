package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaidSubscriptionEvent {
    private String email;
    private String login;
    private String name;
    private Integer price;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPaymentDate;
}
