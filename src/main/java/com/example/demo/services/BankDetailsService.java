package com.example.demo.services;

import com.example.demo.dto.BankDetailsDto;
import com.example.demo.model.AccountId;
import com.example.demo.model.BankDetails;

import java.util.List;

public interface BankDetailsService {
    List<BankDetailsDto> findAllBankDetails(int page, int pageSize);

    BankDetails saveBankDetails(BankDetailsDto bankDetailsDto);

    void makePayment(AccountId accountId, BankDetails bankDetails, int amount);
}
