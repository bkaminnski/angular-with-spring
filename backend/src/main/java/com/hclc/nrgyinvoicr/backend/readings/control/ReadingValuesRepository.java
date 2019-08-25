package com.hclc.nrgyinvoicr.backend.readings.control;

import com.hclc.nrgyinvoicr.backend.readings.entity.ReadingValue;
import com.hclc.nrgyinvoicr.backend.readings.entity.ReadingValueId;
import org.springframework.data.repository.CrudRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface ReadingValuesRepository extends CrudRepository<ReadingValue, ReadingValueId>, ReadingValuesRepositoryCustom {
    void deleteByReadingId(Long readingId);

    List<ReadingValue> findByReadingIdInAndDateGreaterThanEqualAndDateLessThanOrderByDateAscReadingIdAsc(List<Long> readingIds, ZonedDateTime sinceClosed, ZonedDateTime untilOpen);
}
