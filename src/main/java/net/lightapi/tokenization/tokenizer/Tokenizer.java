package net.lightapi.tokenization.tokenizer;

public interface Tokenizer {
    /**
     * Convert value to a token
     * @param value original value
     * @return token
     */
    String tokenize(String value);
}

