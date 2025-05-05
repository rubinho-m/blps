package com.example.demo.services.impl;

import com.example.demo.dto.BankDetailsDto;
import com.example.demo.exceptions.PaymentException;
import com.example.demo.mappers.BankDetailsMapper;
import com.example.demo.model.AccountId;
import com.example.demo.model.BankDetails;
import com.example.demo.model.Payments;
import com.example.demo.repository.BankDetailsRepository;
import com.example.demo.repository.PaymentsRepository;
import com.example.demo.services.BankDetailsService;
import com.example.demo.services.StripeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BankDetailsServiceImpl implements BankDetailsService {
    private final BankDetailsRepository bankDetailsRepository;
    private final BankDetailsMapper bankDetailsMapper;
    private final PaymentsRepository paymentsRepository;
    private final StripeService stripeService;

    @Autowired
    public BankDetailsServiceImpl(BankDetailsRepository bankDetailsRepository,
                                  BankDetailsMapper bankDetailsMapper,
                                  PaymentsRepository paymentsRepository,
                                  StripeService stripeService) {
        this.bankDetailsRepository = bankDetailsRepository;
        this.bankDetailsMapper = bankDetailsMapper;
        this.paymentsRepository = paymentsRepository;
        this.stripeService = stripeService;
    }

    @Override
    public List<BankDetailsDto> findAllBankDetails(int page, int pageSize) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        return bankDetailsRepository.findAll(pageable)
                .stream()
                .map(bankDetailsMapper::toDto)
                .peek(bankDetail -> {
                    bankDetail.setCardNumber("***");
                    bankDetail.setValidityPeriod("***");
                    bankDetail.setCvv("***");
                })
                .toList();
    }

    @Override
    public BankDetails saveBankDetails(BankDetailsDto bankDetailsDto) {
        return bankDetailsRepository.save(bankDetailsMapper.toEntity(bankDetailsDto));
    }

    @Override
    public void makePayment(AccountId accountId, BankDetails bankDetails, int amount) {
        final boolean shouldBeSuccess = !"BAD".equals(bankDetails.getBankAccountName());

        paymentsRepository.save(
                Payments.builder()
                        .accountId(accountId)
                        .bankDetails(bankDetails)
                        .amount(amount)
                        .build()
        );

        try {
            if (shouldBeSuccess) {
                stripeService.makePayment(amount);
            } else {
                stripeService.makeErrorPayment(amount);
            }
        } catch (Exception e) {
            throw new PaymentException("Произошла ошибка при списании средств");
        }
    }
}
