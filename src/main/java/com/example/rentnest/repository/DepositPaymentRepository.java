package com.example.rentnest.repository;

import com.example.rentnest.model.DepositPayment;

import java.util.Optional;

public interface DepositPaymentRepository extends BaseRepository<DepositPayment, Long>{
    Optional<DepositPayment> findByOrderId(String orderId);
    Optional<DepositPayment> findByRequestId(Long requestId);
}
