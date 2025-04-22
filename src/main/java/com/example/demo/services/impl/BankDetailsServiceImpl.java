package com.example.demo.services.impl;

import com.example.demo.exceptions.PaymentException;
import com.example.demo.dto.BankDetailsDto;
import com.example.demo.mappers.BankDetailsMapper;
import com.example.demo.model.AccountId;
import com.example.demo.model.BankDetails;
import com.example.demo.model.Payments;
import com.example.demo.repository.BankDetailsRepository;
import com.example.demo.repository.PaymentsRepository;
import com.example.demo.services.BankDetailsService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
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

    @Autowired
    public BankDetailsServiceImpl(BankDetailsRepository bankDetailsRepository,
                                  BankDetailsMapper bankDetailsMapper,
                                  PaymentsRepository paymentsRepository) {
        this.bankDetailsRepository = bankDetailsRepository;
        this.bankDetailsMapper = bankDetailsMapper;
        this.paymentsRepository = paymentsRepository;
    }

    @PostConstruct
    protected void init() {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY");
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
        processPayment(amount, shouldBeSuccess);
    }

    private void processPayment(int amount, boolean shouldBeSuccess) {
        final PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) amount)
                .setCurrency("usd")
                .setPaymentMethod(shouldBeSuccess ? "pm_card_visa" : "pm_card_chargeDeclined")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )
                .setConfirm(true)
                .build();
        try {
            final PaymentIntent paymentIntent = PaymentIntent.create(params);
            LOGGER.info("Успешное списание! ID: {}", paymentIntent.getId());
        } catch (Exception e) {
            throw new PaymentException("Произошла ошибка при списании средств");
        }
    }
}
