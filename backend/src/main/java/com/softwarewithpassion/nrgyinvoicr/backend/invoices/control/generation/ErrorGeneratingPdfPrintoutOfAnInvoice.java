package com.softwarewithpassion.nrgyinvoicr.backend.invoices.control.generation;

import com.softwarewithpassion.nrgyinvoicr.backend.clients.entity.Client;

public class ErrorGeneratingPdfPrintoutOfAnInvoice extends Exception {
    ErrorGeneratingPdfPrintoutOfAnInvoice(Client client, Exception e) {
        super("There was an error generating PDF printout of an invoice for client " + client.getNumber() + ". " + e.getMessage());
    }
}

