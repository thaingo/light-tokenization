
package net.lightapi.tokenization.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import java.util.HashMap;
import java.util.Map;

public class SchemeGetHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
             exchange.getResponseSender().send("[{\"id\":1,\"name\":\"LNT4\",\"description\":\"Luhn compliant numeric token retaining the last 4 digits. Used for credit card\"},{\"id\":2,\"name\":\"SIN\",\"description\":\"Canadian Social Insurance Number. 9 digits.\"}]");
        
    }
}
