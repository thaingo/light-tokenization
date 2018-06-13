
package net.lightapi.tokenization.handler;

import com.networknt.config.Config;
import com.networknt.handler.LightHttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import net.lightapi.tokenization.DbStartupHookProvider;

public class SchemeIdGetHandler implements LightHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String id = exchange.getQueryParameters().get("id").getFirst();
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(DbStartupHookProvider.schemeFormat.get(id)));
    }
}
