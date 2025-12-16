package com.techsolutions.billingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.resources.TotalAmountResponse;
import com.techsolutions.billingservice.enums.InvoiceStatus;
import com.techsolutions.billingservice.enums.PaymentMethod;
import com.techsolutions.billingservice.exception.InvoiceNotFoundException;
import com.techsolutions.billingservice.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
@DisplayName("InvoiceController Integration Tests")
class InvoiceControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private InvoiceService invoiceService;

        private InvoiceRequest requestDTO;
        private InvoiceResponse responseDTO;

        @BeforeEach
        void setUp() {
                requestDTO = InvoiceRequest.builder()
                                .clientId(100L)
                                .amount(new BigDecimal("1000.00"))
                                .description("Test invoice")
                                .paymentMethod(PaymentMethod.CARD)
                                .build();

                responseDTO = InvoiceResponse.builder()
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
        @DisplayName("POST /api/v1/invoices - Create invoice successfully")
        void testCreateInvoice_Success() throws Exception {
                // Given
                when(invoiceService.createInvoice(any(InvoiceRequest.class))).thenReturn(responseDTO);

                // When & Then
                mockMvc.perform(post("/api/v1/invoices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.clientId").value(100))
                                .andExpect(jsonPath("$.amount").value(1000.00))
                                .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("POST /api/v1/invoices - Validation failure")
        void testCreateInvoice_ValidationFail() throws Exception {
                // Given - invalid DTO without clientId
                InvoiceRequest invalidDTO = InvoiceRequest.builder()
                                .amount(new BigDecimal("1000.00"))
                                .description("Test")
                                .build();

                // When & Then
                mockMvc.perform(post("/api/v1/invoices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/v1/invoices/{id} - Get invoice successfully")
        void testGetInvoice_Success() throws Exception {
                // Given
                when(invoiceService.getInvoice(anyLong())).thenReturn(responseDTO);

                // When & Then
                mockMvc.perform(get("/api/v1/invoices/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.clientId").value(100));
        }

        @Test
        @DisplayName("GET /api/v1/invoices/{id} - Invoice not found")
        void testGetInvoice_NotFound() throws Exception {
                // Given
                when(invoiceService.getInvoice(anyLong()))
                                .thenThrow(new InvoiceNotFoundException("Invoice not found"));

                // When & Then
                mockMvc.perform(get("/api/v1/invoices/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/v1/clients/{clientId}/invoices - Get client invoices")
        void testGetInvoicesByClient_Success() throws Exception {
                // Given
                List<InvoiceResponse> invoices = Arrays.asList(responseDTO);
                when(invoiceService.getInvoicesByClient(anyLong())).thenReturn(invoices);

                // When & Then
                mockMvc.perform(get("/api/v1/clients/100/invoices"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].clientId").value(100));
        }

        @Test
        @DisplayName("PUT /api/v1/invoices/{id}/pay - Pay invoice successfully")
        void testPayInvoice_Success() throws Exception {
                // Given
                responseDTO = InvoiceResponse.builder()
                                .id(1L)
                                .clientId(100L)
                                .amount(new BigDecimal("1000.00"))
                                .description("Test invoice")
                                .dateEmission(LocalDate.now())
                                .status(InvoiceStatus.PAID)
                                .paymentMethod(PaymentMethod.CARD)
                                .datePaiement(LocalDate.now())
                                .build();
                when(invoiceService.payInvoice(anyLong())).thenReturn(responseDTO);

                // When & Then
                mockMvc.perform(put("/api/v1/invoices/1/pay"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("PAID"));
        }

        @Test
        @DisplayName("GET /api/v1/clients/{clientId}/total - Calculate total")
        void testGetTotalAmountByClient_Success() throws Exception {
                // Given
                TotalAmountResponse totalDTO = TotalAmountResponse.builder()
                                .clientId(100L)
                                .totalAmount(new BigDecimal("1500.00"))
                                .invoiceCount(2)
                                .build();
                when(invoiceService.getTotalAmountByClient(anyLong())).thenReturn(totalDTO);

                // When & Then
                mockMvc.perform(get("/api/v1/clients/100/total"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.clientId").value(100))
                                .andExpect(jsonPath("$.totalAmount").value(1500.00))
                                .andExpect(jsonPath("$.invoiceCount").value(2));
        }
}
