package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class RandomNumberTest {
    @Test
    public void testRandomNumber() {
        Tokenizer randomNumber = new RandomNumber();
        String s = randomNumber.tokenize("1234567890");
        System.out.println("s = " + s);
    }
}
