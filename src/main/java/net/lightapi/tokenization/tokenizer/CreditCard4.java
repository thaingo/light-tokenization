package net.lightapi.tokenization.tokenizer;

public class CreditCard4 implements Tokenizer {

    @Override
    public String tokenize(String value) {
        RandomNumber randomNumber = new RandomNumber();
        while(true) {
            String r = randomNumber.tokenize(value.substring(1, value.length() - 5));
            String s = value.substring(0, 1) + r + value.substring(value.length() - 4);
            if(Check(s)) return s;
        }
    }

    public static boolean Check(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
