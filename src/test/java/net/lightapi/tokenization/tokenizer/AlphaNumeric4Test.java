package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class AlphaNumeric4Test {
    @Test
    public void testAlphaNumeric4() {
        Tokenizer alphaNumeric = new AlphaNumeric4();
        String s = alphaNumeric.tokenize("1234567890");
        System.out.println("s = " + s);
    }
}
