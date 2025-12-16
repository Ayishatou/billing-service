package com.techsolutions.billingservice.service;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.resources.TotalAmountResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest invoiceRequestDTO);

    InvoiceResponse getInvoice(Long id);

    List<InvoiceResponse> getInvoicesByClient(Long clientId);

    InvoiceResponse payInvoice(Long id);

    TotalAmountResponse getTotalAmountByClient(Long clientId);
}