
package net.lightapi.tokenization.handler;

import com.networknt.config.Config;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import net.lightapi.tokenization.DbStartupHookProvider;

import java.util.HashMap;
import java.util.Map;

public class SchemeGetHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(DbStartupHookProvider.schemeList));
    }
}
