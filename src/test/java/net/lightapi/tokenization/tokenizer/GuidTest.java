package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class GuidTest {
    @Test
    public void testGuid() {
        Tokenizer guid = new GuidTokenizer();
        String s = guid.tokenize("1234567890");
        System.out.println("s = " + s);
    }
}
