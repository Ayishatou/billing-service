package com.techsolutions.billingservice.resources;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record TotalAmountResponse(
        Long clientId,
        BigDecimal totalAmount,
        Integer invoiceCount
) {
}
