package com.hclc.nrgyinvoicr.backend.invoices.control;

import com.hclc.nrgyinvoicr.backend.invoices.entity.Invoice;
import com.hclc.nrgyinvoicr.backend.invoices.entity.InvoicesSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static com.hclc.nrgyinvoicr.backend.invoices.entity.InvoiceSpecifications.issueDateBetween;

@Service
public class InvoicesService {
    private final InvoicesRepository invoicesRepository;

    InvoicesService(InvoicesRepository invoicesRepository) {
        this.invoicesRepository = invoicesRepository;
    }

    public Page<Invoice> findInvoices(InvoicesSearchCriteria criteria) {
        ZonedDateTime from = criteria.getIssueDateSince();
        ZonedDateTime to = criteria.getIssueDateUntil();
        Specification<Invoice> specification = issueDateBetween(from, to);

        return this.invoicesRepository.findAll(specification, criteria.getPageDefinition().asPageRequest());
    }
}