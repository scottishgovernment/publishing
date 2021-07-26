package scot.mygov.publishing.codec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriCodecPeriodFixTest {

    @Test
    public void codecReplacesPeriodsWithHyphens() {
        // ARRANGE
        String input = "1. Introduction";
        UriCodecPeriodFix sut = new UriCodecPeriodFix();

        // ACT
        String actual = sut.encode(input);

        // ASSERT
        // input lowercase, spaces to hyphens, periods to hyphens
        String expected = "1--introduction";
        assertEquals(expected, actual);
    }

    @Test
    public void uriCodecPeriodFixOnlyActsIfPeriodsPresent() {
        // ARRANGE
        String input = "1 Introduction";
        UriCodecPeriodFix sut = new UriCodecPeriodFix();

        // ACT
        String actual = sut.encode(input);

        // ASSERT
        String expected = "1-introduction";
        assertEquals(expected, actual);
    }
}