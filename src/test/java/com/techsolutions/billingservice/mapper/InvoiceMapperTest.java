package com.techsolutions.billingservice.mapper;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.entities.Invoice;
import com.techsolutions.billingservice.enums.InvoiceStatus;
import com.techsolutions.billingservice.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvoiceMapper Unit Tests")
class InvoiceMapperTest {

    private InvoiceMapper invoiceMapper;

    @BeforeEach
    void setUp() {
        invoiceMapper = new InvoiceMapper();
    }

    @Test
    @DisplayName("Should map InvoiceRequest to Invoice entity")
    void testToEntity() {
        // Given
        InvoiceRequest requestDTO = InvoiceRequest.builder()
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .paymentMethod(PaymentMethod.CARD)
                .build();

        // When
        Invoice invoice = invoiceMapper.toEntity(requestDTO);

        // Then
        assertNotNull(invoice);
        assertEquals(100L, invoice.getClientId());
        assertEquals(new BigDecimal("1000.00"), invoice.getAmount());
        assertEquals("Test invoice", invoice.getDescription());
        assertEquals(PaymentMethod.CARD, invoice.getPaymentMethod());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertNotNull(invoice.getDateEmission());
        assertNull(invoice.getId());
        assertNull(invoice.getDatePaiement());
    }

    @Test
    @DisplayName("Should map Invoice entity to InvoiceResponse")
    void testToDTO() {
        // Given
        Invoice invoice = Invoice.builder()
                .id(1L)
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .dateEmission(LocalDate.now())
                .datePaiement(null)
                .status(InvoiceStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        // When
        InvoiceResponse responseDTO = invoiceMapper.toDTO(invoice);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        assertEquals(100L, responseDTO.clientId());
        assertEquals(new BigDecimal("1000.00"), responseDTO.amount());
        assertEquals("Test invoice", responseDTO.description());
        assertEquals(InvoiceStatus.PENDING, responseDTO.status());
        assertEquals(PaymentMethod.CARD, responseDTO.paymentMethod());
        assertNotNull(responseDTO.dateEmission());
        assertNull(responseDTO.datePaiement());
    }

    @Test
    @DisplayName("Should map paid Invoice entity correctly")
    void testToDTO_PaidInvoice() {
        // Given
        LocalDate paymentDate = LocalDate.now();
        Invoice invoice = Invoice.builder()
                .id(1L)
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .dateEmission(LocalDate.now().minusDays(5))
                .datePaiement(paymentDate)
                .status(InvoiceStatus.PAID)
                .paymentMethod(PaymentMethod.TRANSFER)
                .build();

        // When
        InvoiceResponse responseDTO = invoiceMapper.toDTO(invoice);

        // Then
        assertNotNull(responseDTO);
        assertEquals(InvoiceStatus.PAID, responseDTO.status());
        assertEquals(paymentDate, responseDTO.datePaiement());
        assertEquals(PaymentMethod.TRANSFER, responseDTO.paymentMethod());
    }
}
