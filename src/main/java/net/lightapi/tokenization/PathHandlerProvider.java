
package net.lightapi.tokenization;

import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;
import com.networknt.info.ServerInfoGetHandler;
import com.networknt.health.HealthGetHandler;

import net.lightapi.tokenization.handler.*;

public class PathHandlerProvider implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
        
            .add(Methods.POST, "/v1/token", new TokenPostHandler())
        
            .add(Methods.GET, "/v1/token/{token}", new TokenGetHandler())
        
            .add(Methods.DELETE, "/v1/token/{token}", new TokenDeleteHandler())
        
            .add(Methods.GET, "/v1/scheme", new SchemeGetHandler())
        
            .add(Methods.GET, "/v1/health", new HealthGetHandler())
        
            .add(Methods.GET, "/v1/scheme/{id}", new SchemeIdGetHandler())
        
            .add(Methods.GET, "/v1/server/info", new ServerInfoGetHandler())
        
        ;
    }
}
