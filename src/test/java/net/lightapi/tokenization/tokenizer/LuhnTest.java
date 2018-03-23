package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class LuhnTest {
    @Test
    public void testLuhn() {
        Tokenizer luhn = new LuhnTokenizer();
        String s = luhn.tokenize("1234567890987324");
        System.out.println("s = " + s);
    }

}
