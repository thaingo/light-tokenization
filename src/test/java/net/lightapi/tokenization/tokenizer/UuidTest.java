package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class UuidTest {
    @Test
    public void testUuid() {
        Tokenizer uuid = new UuidTokenizer();
        String s = uuid.tokenize("1234567890");
        System.out.println("s = " + s);
    }

}
