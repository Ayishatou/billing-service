package com.techsolutions.billingservice.repository;

import com.techsolutions.billingservice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Méthode pour trouver les factures d'un client spécifique
    List<Invoice> findByClientId(Long clientId);
}
