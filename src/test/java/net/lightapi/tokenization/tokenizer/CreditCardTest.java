package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class CreditCardTest {
    @Test
    public void testCreditCard() {
        Tokenizer creditCard = new CreditCard();
        String s = creditCard.tokenize("4234567890987324");
        System.out.println("s = " + s);
    }
}
