package net.lightapi.tokenization.tokenizer;

import java.util.UUID;

public class UuidTokenizer implements Tokenizer {
    @Override
    public String tokenize(String value) {
        return UUID.randomUUID().toString();
    }

}
