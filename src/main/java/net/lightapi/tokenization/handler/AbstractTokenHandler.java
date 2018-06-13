package net.lightapi.tokenization.handler;

import com.networknt.audit.AuditHandler;
import com.networknt.exception.ApiException;
import com.networknt.handler.LightHttpHandler;
import com.networknt.status.Status;
import com.networknt.utility.Constants;
import io.undertow.server.HttpServerExchange;
import net.lightapi.tokenization.DbStartupHookProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

public abstract class AbstractTokenHandler implements LightHttpHandler {
    private static final String CLIENT_ID_MISSING = "ERR12101";
    private static final String DATABASE_NOT_FOUND = "ERR12102";
    private static final String DATASOURCE_NOT_CONFIGURED = "ERR12103";
    private static final Logger logger = LoggerFactory.getLogger(AbstractTokenHandler.class);

    String getDatabaseName(HttpServerExchange exchange) throws ApiException {
        // get client_id from auditInfo which is populated by JwtVerifyHandler
        Map<String, Object> auditInfo = exchange.getAttachment(AuditHandler.AUDIT_INFO);
        String clientId = (String)auditInfo.get(Constants.CLIENT_ID_STRING);
        if(logger.isDebugEnabled()) logger.debug("clientId = " + clientId);
        if(clientId == null) {
            Status status = new Status(CLIENT_ID_MISSING);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            throw new ApiException(status);
        }
        // get the vault database from clientId
        String database = DbStartupHookProvider.clientDatabase.get(clientId);
        if(database == null) {
            Status status = new Status(DATABASE_NOT_FOUND, clientId);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            throw new ApiException(status);
        }
        if(logger.isDebugEnabled()) logger.debug("database = " + database);
        return database;
    }

    DataSource getDataSource(HttpServerExchange exchange, String database) throws ApiException {
        // get the datasource based on database name
        DataSource ds = DbStartupHookProvider.dbMap.get(database);
        if(ds == null) {
            Status status = new Status(DATASOURCE_NOT_CONFIGURED, database);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            throw new ApiException(status);
        }
        return ds;
    }
}
