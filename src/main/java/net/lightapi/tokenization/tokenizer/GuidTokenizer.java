package net.lightapi.tokenization.tokenizer;

import com.networknt.utility.Util;

public class GuidTokenizer implements Tokenizer {
    @Override
    public String tokenize(String value) {
        return Util.getUUID();
    }
}
