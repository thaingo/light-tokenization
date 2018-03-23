package net.lightapi.tokenization.tokenizer;

import java.util.Random;

public class RandomNumber implements Tokenizer {
    @Override
    public String tokenize(String value) {
        return generateRandom(value.length());
    }

    public static String generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }
}
