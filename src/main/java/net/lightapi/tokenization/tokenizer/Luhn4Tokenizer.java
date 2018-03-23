package net.lightapi.tokenization.tokenizer;

public class Luhn4Tokenizer implements Tokenizer {
    @Override
    public String tokenize(String value) {
        RandomNumber randomNumber = new RandomNumber();
        while(true) {
            String r = randomNumber.tokenize(value.substring(0, value.length() - 4));
            String s = r + value.substring(value.length() - 4);
            if(CreditCard4.Check(s)) return s;
        }
    }
}
