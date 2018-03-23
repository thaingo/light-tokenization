package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class Luhn4Test {
    @Test
    public void testLuhn4() {
        Tokenizer luhn4 = new Luhn4Tokenizer();
        String s = luhn4.tokenize("1234567890");
        System.out.println("s = " + s);
    }
}
