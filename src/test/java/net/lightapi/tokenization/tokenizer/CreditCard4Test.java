package net.lightapi.tokenization.tokenizer;

import org.junit.Test;

public class CreditCard4Test {
    @Test
    public void testCredit4Card() {
        Tokenizer creditCard4 = new CreditCard4();
        String s = creditCard4.tokenize("4234567890987324");
        System.out.println("s = " + s);
    }
}
