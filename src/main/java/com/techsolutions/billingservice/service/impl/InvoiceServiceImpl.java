package com.techsolutions.billingservice.service.impl;

import com.techsolutions.billingservice.resources.InvoiceRequest;
import com.techsolutions.billingservice.resources.InvoiceResponse;
import com.techsolutions.billingservice.resources.TotalAmountResponse;
import com.techsolutions.billingservice.entities.Invoice;
import com.techsolutions.billingservice.enums.InvoiceStatus;
import com.techsolutions.billingservice.exception.InvoiceNotFoundException;
import com.techsolutions.billingservice.exception.InvalidInvoiceOperationException;
import com.techsolutions.billingservice.mapper.InvoiceMapper;
import com.techsolutions.billingservice.repository.InvoiceRepository;
import com.techsolutions.billingservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Injection de dépendances via constructeur (Lombok)
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequestDTO) {
        Invoice invoice = invoiceMapper.toEntity(invoiceRequestDTO);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toDTO(savedInvoice);
    }

    @Override
    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        return invoiceMapper.toDTO(invoice);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByClient(Long clientId) {
        return invoiceRepository.findByClientId(clientId).stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse payInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvalidInvoiceOperationException("Invoice is already paid");
        }

        // Logique de paiement [cite: 14]
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setDatePaiement(LocalDate.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toDTO(savedInvoice);
    }

    @Override
    public TotalAmountResponse getTotalAmountByClient(Long clientId) {
        List<Invoice> invoices = invoiceRepository.findByClientId(clientId);

        // Calcul de la somme totale [cite: 14]
        BigDecimal total = invoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TotalAmountResponse.builder()
                .clientId(clientId)
                .totalAmount(total)
                .invoiceCount(invoices.size())
                .build();
    }
}