package scot.mygov.publishing.codec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StopwordsUriEncodingTest {

    StopwordsUriEncoding sut = new StopwordsUriEncoding();

    @Test
    public void encodesSimpleTitleCorrectly() {
        assertEquals("vat-information", sut.encode("Vat Information"));
    }

    @Test
    public void removesStopwords() {
        assertEquals("vat-information", sut.encode("This is about Vat Information"));
    }

    @Test
    public void replacesFullstops() {
        assertEquals("1-vat-information", sut.encode("1. This is about. Vat Information"));
    }

    @Test
    public void replacesSpecialChars() {
        assertEquals("vat-information", sut.encode("This is about. Vat Information?%$£@"));
    }

    @Test
    public void replacesSpecialChars2() {
        assertEquals("vat-information-ae-aa", sut.encode("This is about. Vat Information ä å"));
    }
}
