package com.techsolutions.billingservice.resources;

import com.techsolutions.billingservice.enums.InvoiceStatus;
import com.techsolutions.billingservice.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceResponse(
        Long id,
        Long clientId,
        BigDecimal amount,
        String description,
        LocalDate dateEmission,
        LocalDate datePaiement,
        InvoiceStatus status,
        PaymentMethod paymentMethod) {
}
