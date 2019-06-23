package com.hclc.nrgyinvoicr.backend.plans.entity.buckets;

import com.hclc.nrgyinvoicr.backend.readings.entity.ReadingValue;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

class DateBucket extends Bucket {
    private final String sinceAsString;
    private final String untilAsString;
    private final int since;
    private final int until;

    DateBucket(ExpressionLine bucketStart, List<ExpressionLine> bucketContent) {
        this.sinceAsString = bucketStart.getRangeStart();
        this.untilAsString = bucketStart.getRangeEnd();
        this.since = parseDate(sinceAsString);
        this.until = parseDate(untilAsString);
        addToBuckets(new BucketsCreator(bucketContent, 2, DayOfWeekBucket::new).create());
    }

    private DateBucket(String sinceAsString, String untilAsString, int since, int until, List<Bucket> buckets) {
        this.sinceAsString = sinceAsString;
        this.untilAsString = untilAsString;
        this.since = since;
        this.until = until;
        addToBuckets(buckets);
    }

    @Override
    boolean accept(ReadingValue readingValue) {
        int date = parseDate(readingValue.getDate());
        if (since <= date && date <= until) {
            return super.accept(readingValue);
        }
        return false;
    }

    @Override
    public Bucket optimized() {
        if (this.coversFullPeriod()) {
            return Buckets.forBuckets(getOptimizedBuckets());
        }
        return new DateBucket(sinceAsString, untilAsString, since, until, getOptimizedBuckets());
    }

    @Override
    public List<Flattened> flatten() {
        return getBuckets().stream()
                .flatMap(b -> b.flatten().stream())
                .map(f -> f.accept(this))
                .collect(toList());
    }

    private boolean coversFullPeriod() {
        return since == 101 && until == 1231;
    }

    private int parseDate(String date) {
        String[] parts = date.split("\\.");
        int month = Integer.valueOf(parts[0]);
        int day = Integer.valueOf(parts[1]);
        return month * 100 + day;
    }

    private int parseDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.getMonthValue() * 100 + zonedDateTime.getDayOfMonth();
    }

    String getSinceAsString() {
        return sinceAsString;
    }

    String getUntilAsString() {
        return untilAsString;
    }
}