package com.techsolutions.billingservice.mapper;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.entities.Invoice;
import com.techsolutions.billingservice.enums.InvoiceStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvoiceMapper {

    public Invoice toEntity(InvoiceRequest dto) {
        return Invoice.builder()
                .clientId(dto.clientId())
                .amount(dto.amount())
                .description(dto.description())
                .paymentMethod(dto.paymentMethod())
                .dateEmission(LocalDate.now())
                .status(InvoiceStatus.PENDING)
                .build();
    }

    public InvoiceResponse toDTO(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .clientId(invoice.getClientId())
                .amount(invoice.getAmount())
                .description(invoice.getDescription())
                .dateEmission(invoice.getDateEmission())
                .datePaiement(invoice.getDatePaiement())
                .status(invoice.getStatus())
                .paymentMethod(invoice.getPaymentMethod())
                .build();
    }
}
