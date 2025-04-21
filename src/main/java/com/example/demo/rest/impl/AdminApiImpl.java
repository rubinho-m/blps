package com.example.demo.rest.impl;

import com.example.demo.dto.AdminChangeRequestDto;
import com.example.demo.dto.BankDetailsDto;
import com.example.demo.dto.SubscriptionResponseDto;
import com.example.demo.rest.AdminApi;
import com.example.demo.services.AccountIdService;
import com.example.demo.services.BankDetailsService;
import com.example.demo.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminApiImpl implements AdminApi {
    private final SubscriptionService subscriptionService;
    private final AccountIdService accountIdService;
    private final BankDetailsService bankDetailsService;

    @Autowired
    public AdminApiImpl(SubscriptionService subscriptionService,
                        AccountIdService accountIdService,
                        BankDetailsService bankDetailsService) {
        this.subscriptionService = subscriptionService;
        this.accountIdService = accountIdService;
        this.bankDetailsService = bankDetailsService;
    }

    @Override
    public ResponseEntity<List<SubscriptionResponseDto>> getAllSubscriptions(int page, int pageSize) {
        return ResponseEntity.ok(subscriptionService.getAll(page, pageSize));
    }

    @Override
    public ResponseEntity<List<BankDetailsDto>> getAllBankDetails(int page, int pageSize) {
        return ResponseEntity.ok(bankDetailsService.findAllBankDetails(page, pageSize));
    }

    @Override
    public ResponseEntity<List<AdminChangeRequestDto>> getAllRequestedChanges(int page, int pageSize) {
        return ResponseEntity.ok(accountIdService.getAllRequestedChanges(page, pageSize));
    }

    @Override
    public ResponseEntity<Void> permitChangeRequest(Long id) {
        accountIdService.permitChangeRequest(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> denyChangeRequest(Long id) {
        accountIdService.denyChangeRequest(id);
        return ResponseEntity.noContent().build();
    }
}
