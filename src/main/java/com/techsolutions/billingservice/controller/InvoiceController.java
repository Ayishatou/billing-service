package com.techsolutions.billingservice.controller;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.resources.TotalAmountResponse;
import com.techsolutions.billingservice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "Operations related to invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequestDTO) {
        return new ResponseEntity<>(invoiceService.createInvoice(invoiceRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get invoice details by ID")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @GetMapping("/clients/{clientId}/invoices")
    @Operation(summary = "List all invoices for a specific client")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByClient(clientId));
    }

    @PutMapping("/invoices/{id}/pay")
    @Operation(summary = "Mark an invoice as PAID")
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.payInvoice(id));
    }

    @GetMapping("/clients/{clientId}/total")
    @Operation(summary = "Calculate total amount billed to a client")
    public ResponseEntity<TotalAmountResponse> getTotalAmountByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.getTotalAmountByClient(clientId));
    }
}