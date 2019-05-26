package com.hclc.nrgyinvoicr.backend.readings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.of;

class ReadingLineParserTest {
    private ReadingLineParser readingLineParser;

    @BeforeEach
    void beforeEach() {
        this.readingLineParser = new ReadingLineParser();
    }

    @Test
    void whenLineIsCorrect_shouldParseLine() throws ReadingLineParserException {
        String line = "2019-10-27T00:00+02:00;123.45";

        Reading reading = readingLineParser.parse(1L, 2, line);

        assertThat(reading.getImportId()).isEqualTo(1L);
        assertThat(reading.getDate()).isEqualTo("2019-10-27T00:00+02:00");
        assertThat(reading.getValue()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("parameters")
    void whenLineIsIncorrect_shouldThrowException(String description, String line, String expectedMessage) throws ReadingLineParserException {
        assertThatThrownBy(() -> readingLineParser.parse(1L, 2, line)).hasMessage(expectedMessage);
    }

    private static Stream<Arguments> parameters() {
        return Stream.of(
                of("Line is null", null, "Line 2 is empty."),
                of("Line is empty", "", "Line 2 is empty."),
                of("Line has too little values", "xyz", "Line 2 has 1 value(s). Line should have exactly two values."),
                of("Line has too many values", "xyz;abc;def", "Line 2 has 3 value(s). Line should have exactly two values."),
                of("Invalid date", "2019-27-10T00:00+02:00;123.45", "Invalid date in line 2: 2019-27-10T00:00+02:00. A date should match the following pattern: yyyy-MM-dd'T'HH:mmZZZZZ."),
                of("Invalid value", "2019-10-27T00:00+02:00;123ABC", "Invalid numeric value in line 2: 123ABC.")
        );
    }
}