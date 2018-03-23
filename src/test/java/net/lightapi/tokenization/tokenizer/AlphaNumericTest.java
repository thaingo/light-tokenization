package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class AlphaNumericTest {
    @Test
    public void testAlphaNumeric() {
        Tokenizer alphaNumeric = new AlphaNumeric();
        String s = alphaNumeric.tokenize("1234567890");
        System.out.println("s = " + s);
    }
}
