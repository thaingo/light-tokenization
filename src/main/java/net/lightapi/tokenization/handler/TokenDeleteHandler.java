
package net.lightapi.tokenization.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.networknt.status.Status;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.lightapi.tokenization.DbStartupHookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TokenDeleteHandler extends AbstractTokenHandler {
    private static final Logger logger = LoggerFactory.getLogger(TokenDeleteHandler.class);
    private static final String FAIL_TO_DELETE_TOKEN_VAULT = "ERR12106";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get database name and datasource
        String database = getDatabaseName(exchange);
        DataSource ds = getDataSource(exchange, database);

        // get path parameter token
        String token = exchange.getQueryParameters().get("token").getFirst();
        if(logger.isDebugEnabled()) logger.debug("token = " + token);

        // delete from database
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM token_vault WHERE id = ?")) {
            statement.setString(1, token);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException:", e);
            Status status = new Status(FAIL_TO_DELETE_TOKEN_VAULT, database);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            return;
        }

        // delete from cache
        Cache cache = DbStartupHookProvider.dbTokenMap.get(database);
        cache.invalidate(token);
        exchange.setStatusCode(StatusCodes.OK);
        exchange.endExchange();
    }
}
