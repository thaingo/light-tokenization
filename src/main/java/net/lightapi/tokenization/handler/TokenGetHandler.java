
package net.lightapi.tokenization.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.networknt.status.Status;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.lightapi.tokenization.DbStartupHookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenGetHandler extends AbstractTokenHandler {
    private static final Logger logger = LoggerFactory.getLogger(TokenGetHandler.class);
    private static final String TOKEN_NOT_FOUND = "ERR12107";
    private static final String FAIL_TO_QUERY_TOKEN_VAULT = "ERR12104";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get database name and datasource
        String database = getDatabaseName(exchange);
        DataSource ds = getDataSource(exchange, database);

        // get path parameter token
        String token = exchange.getQueryParameters().get("token").getFirst();
        if(logger.isDebugEnabled()) logger.debug("token = " + token);

        // try to get from cache first
        Cache cache = DbStartupHookProvider.dbTokenMap.get(database);
        String value = (String)cache.getIfPresent(token);
        if(value == null || value.trim().length() == 0) {
            // load from database
            // check if the value has been in database
            try (final Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT value FROM token_vault WHERE id = ?")) {
                statement.setString(1, token);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {
                        value = resultSet.getString("value");
                        cache.put(token, value);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.setStatusCode(StatusCodes.OK);
                        exchange.getResponseSender().send(value);
                        return;
                    } else {
                        // the token is not found in vault
                        setExchangeStatus(exchange, TOKEN_NOT_FOUND, token, database);
                        return;
                    }
                }
            } catch (SQLException e) {
                logger.error("SQLException:", e);
                setExchangeStatus(exchange, FAIL_TO_QUERY_TOKEN_VAULT, database);
                return;
            }
        } else {
            // got the value from cache
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.setStatusCode(StatusCodes.OK);
            exchange.getResponseSender().send(value);
        }
    }
}
