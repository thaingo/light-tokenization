package net.lightapi.tokenization.tokenizer;

import java.util.UUID;

public class AlphaNumeric4 implements Tokenizer {
    @Override
    public String tokenize(String value) {
        return AlphaNumeric.randomAlphaNumeric(value.length() - 4) + value.substring(value.length() - 4);
    }
}
