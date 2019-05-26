package com.hclc.nrgyinvoicr.backend.readings;

import com.hclc.nrgyinvoicr.backend.AuditableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class Reading extends AuditableEntity {

    @Id
    @SequenceGenerator(name = "reading_id_seq", sequenceName = "reading_id_seq", initialValue = 1, allocationSize = 50)
    @GeneratedValue(strategy = SEQUENCE, generator = "reading_id_seq")
    private Long id;

    @NotNull
    @Temporal(TIMESTAMP)
    private Date date;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Embedded
    private ReadingsSpread readingsSpread;

    public Reading() {
    }

    public Reading(@NotNull Date date, @NotNull Meter meter) {
        this.date = date;
        this.meter = meter;
    }

    public void updateWithReadingsSpread(Date readingsSinceClosed, Date readingsUntilOpen, long numberOfMadeReadings) {
        this.readingsSpread = new ReadingsSpread(readingsSinceClosed, readingsUntilOpen, numberOfMadeReadings);
    }

    public Long getId() {
        return id;
    }
}