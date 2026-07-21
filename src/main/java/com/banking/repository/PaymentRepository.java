package com.banking.repository;

import com.banking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.fromAccount.id = :accountId OR p.toAccount.id = :accountId ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByAccount(@Param("accountId") Long accountId);

    @Query("SELECT p FROM Payment p WHERE p.fromAccount.id = :accountId AND p.createdAt >= :startDate AND p.createdAt <= :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByAccountAndDateRange(
        @Param("accountId") Long accountId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    List<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status);
}
