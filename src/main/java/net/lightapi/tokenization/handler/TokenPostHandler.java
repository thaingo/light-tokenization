package net.lightapi.tokenization.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.networknt.audit.AuditHandler;
import com.networknt.body.BodyHandler;
import com.networknt.status.Status;
import com.networknt.utility.Constants;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.lightapi.tokenization.DbStartupHookProvider;
import net.lightapi.tokenization.tokenizer.GuidTokenizer;
import net.lightapi.tokenization.tokenizer.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TokenPostHandler extends AbstractTokenHandler {
    private static final Logger logger = LoggerFactory.getLogger(TokenPostHandler.class);
    private static final String SCHEME_NOT_FOUND = "ERR12100";
    private static final String FAIL_TO_QUERY_TOKEN_VAULT = "ERR12104";
    private static final String FAIL_TO_INSERT_TOKEN_VAULT = "ERR12105";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get database name and datasource
        String database = getDatabaseName(exchange);
        DataSource ds = getDataSource(exchange, database);

        // get the body from body parser attachment and get schemeId and value.
        Map<String, Object> body = (Map<String, Object>)exchange.getAttachment(BodyHandler.REQUEST_BODY);
        if(logger.isDebugEnabled()) logger.debug("body = " + body);
        Integer schemeId = (Integer)body.get("schemeId");
        String value = (String)body.get("value");
        if(logger.isDebugEnabled()) logger.debug("schemeId = " + schemeId + " value = " + value);

        // check if the value has been in database
        try (final Connection connection = ds.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM token_vault WHERE value = ?")) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    String id = resultSet.getString("id");
                    // return this token to the caller as the same value has tokenized already.
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.setStatusCode(StatusCodes.OK);
                    exchange.getResponseSender().send(id);
                    return;
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException:", e);
            Status status = new Status(FAIL_TO_QUERY_TOKEN_VAULT, database);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            return;
        }

        // call the tokenizer based on the schemeId
        Tokenizer tokenizer = DbStartupHookProvider.tokenizerMap.get(schemeId);
        if(tokenizer == null) {
            Status status = new Status(SCHEME_NOT_FOUND, schemeId);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            return;
        }
        String token = tokenizer.tokenize(value);
        if(logger.isDebugEnabled()) logger.debug("token = " + token);

        // insert the token and value into token vault.
        try (Connection connection = ds.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO token_vault (id, value) values (?, ?)")) {
            statement.setString(1, token);
            statement.setString(2, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException:", e);
            Status status = new Status(FAIL_TO_INSERT_TOKEN_VAULT, database);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            return;
        }

        // put the token into the right cache.
        Cache cache = DbStartupHookProvider.dbTokenMap.get(database);
        cache.put(token, value);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseSender().send(token);
    }
}
