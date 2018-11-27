package com.hclc.nrgyinvoicr.backend.invoices;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class Invoice {

    @Id
    @SequenceGenerator(name = "invoice_id_seq", sequenceName = "invoice_id")
    @GeneratedValue(strategy = SEQUENCE, generator = "invoice_id_seq")
    private Long id;

    @NotNull
    @Column(length = 255)
    private String number;

    @NotNull
    @Temporal(TIMESTAMP)
    private Date issueDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
