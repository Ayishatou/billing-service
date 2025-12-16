package com.techsolutions.billingservice.service;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.resources.TotalAmountResponse;
import com.techsolutions.billingservice.entities.Invoice;
import com.techsolutions.billingservice.enums.InvoiceStatus;
import com.techsolutions.billingservice.enums.PaymentMethod;
import com.techsolutions.billingservice.exception.InvoiceNotFoundException;
import com.techsolutions.billingservice.exception.InvalidInvoiceOperationException;
import com.techsolutions.billingservice.mapper.InvoiceMapper;
import com.techsolutions.billingservice.repository.InvoiceRepository;
import com.techsolutions.billingservice.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceService Unit Tests")
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice testInvoice;
    private InvoiceRequest testRequestDTO;
    private InvoiceResponse testResponseDTO;

    @BeforeEach
    void setUp() {
        // Prepare test data
        testInvoice = Invoice.builder()
                .id(1L)
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .dateEmission(LocalDate.now())
                .status(InvoiceStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        testRequestDTO = InvoiceRequest.builder()
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .paymentMethod(PaymentMethod.CARD)
                .build();

        testResponseDTO = InvoiceResponse.builder()
                .id(1L)
                .clientId(100L)
                .amount(new BigDecimal("1000.00"))
                .description("Test invoice")
                .dateEmission(LocalDate.now())
                .status(InvoiceStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

    @Test
    @DisplayName("Should create invoice successfully")
    void testCreateInvoice_Success() {
        // Given
        when(invoiceMapper.toEntity(any(InvoiceRequest.class))).thenReturn(testInvoice);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toDTO(any(Invoice.class))).thenReturn(testResponseDTO);

        // When
        InvoiceResponse result = invoiceService.createInvoice(testRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDTO.id(), result.id());
        assertEquals(testResponseDTO.clientId(), result.clientId());
        assertEquals(testResponseDTO.amount(), result.amount());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should get invoice by id successfully")
    void testGetInvoice_Success() {
        // Given
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));
        when(invoiceMapper.toDTO(any(Invoice.class))).thenReturn(testResponseDTO);

        // When
        InvoiceResponse result = invoiceService.getInvoice(1L);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDTO.id(), result.id());
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when invoice not found")
    void testGetInvoice_NotFound() {
        // Given
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvoiceNotFoundException.class, () -> {
            invoiceService.getInvoice(999L);
        });
        verify(invoiceRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get invoices by client id")
    void testGetInvoicesByClient_Success() {
        // Given
        List<Invoice> invoices = Arrays.asList(testInvoice);
        when(invoiceRepository.findByClientId(anyLong())).thenReturn(invoices);
        when(invoiceMapper.toDTO(any(Invoice.class))).thenReturn(testResponseDTO);

        // When
        List<InvoiceResponse> result = invoiceService.getInvoicesByClient(100L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(invoiceRepository, times(1)).findByClientId(100L);
    }

    @Test
    @DisplayName("Should pay invoice successfully")
    void testPayInvoice_Success() {
        // Given
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toDTO(any(Invoice.class))).thenReturn(testResponseDTO);

        // When
        InvoiceResponse result = invoiceService.payInvoice(1L);

        // Then
        assertNotNull(result);
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw exception when paying already paid invoice")
    void testPayInvoice_AlreadyPaid() {
        // Given
        testInvoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));

        // When & Then
        assertThrows(InvalidInvoiceOperationException.class, () -> {
            invoiceService.payInvoice(1L);
        });
    }

    @Test
    @DisplayName("Should calculate total amount by client")
    void testGetTotalAmountByClient_Success() {
        // Given
        Invoice invoice2 = Invoice.builder()
                .id(2L)
                .clientId(100L)
                .amount(new BigDecimal("500.00"))
                .description("Test invoice 2")
                .dateEmission(LocalDate.now())
                .status(InvoiceStatus.PENDING)
                .build();

        List<Invoice> invoices = Arrays.asList(testInvoice, invoice2);
        when(invoiceRepository.findByClientId(anyLong())).thenReturn(invoices);

        // When
        TotalAmountResponse result = invoiceService.getTotalAmountByClient(100L);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result.totalAmount());
        assertEquals(2, result.invoiceCount());
        assertEquals(100L, result.clientId());
    }
}
