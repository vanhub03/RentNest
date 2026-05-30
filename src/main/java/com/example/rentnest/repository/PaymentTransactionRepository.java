package com.example.rentnest.repository;

import com.example.rentnest.model.PaymentTransaction;

import java.util.Optional;

public interface PaymentTransactionRepository extends BaseRepository<PaymentTransaction, Long>{
    Optional<PaymentTransaction> findByOrderId(String orderId);
    Optional<PaymentTransaction> findByRequestId(String requestId);
}
