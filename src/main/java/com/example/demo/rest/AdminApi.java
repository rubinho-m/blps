package com.example.demo.rest;

import com.example.demo.dto.AdminChangeRequestDto;
import com.example.demo.dto.BankDetailsDto;
import com.example.demo.dto.SubscriptionResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Admin Api", description = "Админка")
public interface AdminApi {
    @GetMapping("/admin/subscriptions")
    ResponseEntity<List<SubscriptionResponseDto>> getAllSubscriptions(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int pageSize);

    @GetMapping("/admin/cards")
    ResponseEntity<List<BankDetailsDto>> getAllBankDetails(@RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int pageSize);

    @GetMapping("/admin/requests")
    ResponseEntity<List<AdminChangeRequestDto>> getAllRequestedChanges(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                       @RequestParam(value = "size", defaultValue = "10") int pageSize);

    @PutMapping("/admin/requests/{id}/accept")
    ResponseEntity<Void> permitChangeRequest(@PathVariable("id") Long id);

    @PutMapping("/admin/requests/{id}/deny")
    ResponseEntity<Void> denyChangeRequest(@PathVariable("id") Long id);
}
