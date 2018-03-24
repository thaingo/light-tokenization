package net.lightapi.tokenization;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;
import com.zaxxer.hikari.HikariDataSource;
import net.lightapi.tokenization.model.Scheme;
import net.lightapi.tokenization.tokenizer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DbStartupHookProvider implements StartupHookProvider {
    public static final String DATA_SOURCE = "datasource";
    public static final String TOKENIZATION = "tokenization";

    public static Map<String, DataSource> dbMap = new ConcurrentHashMap<>();
    public static Map<String, String> clientDatabase = new ConcurrentHashMap<>();
    public static List<Scheme> schemeList = new ArrayList<>();
    public static Map<String, Scheme> schemeFormat = new ConcurrentHashMap<>();
    // this is to support multiple token vaults.
    public static Map<String, Cache<String, String>> dbTokenMap = new ConcurrentHashMap<>();
    public static final Map<Integer, Tokenizer> tokenizerMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DbStartupHookProvider.class);

    @Override
    public void onStartup() {
        Map<String, Object> dataSourceMap = (Map<String, Object>) Config.getInstance().getJsonMapConfig(DATA_SOURCE);
        // iterate all db config
        dataSourceMap.forEach((k, v) -> {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(((Map<String, String>)v).get("jdbcUrl"));
            ds.setUsername(((Map<String, String>)v).get("username"));
            ds.setPassword(((Map<String, String>)v).get("password"));
            Map<String, Object> configParams = (Map<String, Object>)((Map<String, Object>)v).get("parameters");
            configParams.forEach((p, q) -> ds.addDataSourceProperty(p, q));
            dbMap.put(k, ds);
            if(!TOKENIZATION.equals(k)) {
                // prepopulate cache for each vault database
                dbTokenMap.put(k, Caffeine.newBuilder()
                        .expireAfterWrite(24, TimeUnit.HOURS)
                        .build());
            }
        });

        // load the cache for tokenization db
        try (final Connection connection = dbMap.get("tokenization").getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name, description FROM format_scheme");
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()) {
                Scheme scheme = new Scheme();
                scheme.setId(resultSet.getString("id"));
                scheme.setName(resultSet.getString("name"));
                scheme.setDescription(resultSet.getString("description"));
                schemeFormat.put(scheme.getId(), scheme);
                schemeList.add(scheme);
            }
        } catch (SQLException e) {
            logger.error("SQLException:", e);
            throw new RuntimeException("SQLException:", e);
        }

        try (final Connection connection = dbMap.get("tokenization").getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT client_id, db_name FROM client_database");
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()) {
                clientDatabase.put(resultSet.getString("client_id"), resultSet.getString("db_name"));
            }
        } catch (SQLException e) {
            logger.error("SQLException:", e);
            throw new RuntimeException("SQLException:", e);
        }

        // initialize tokenizer map.
        tokenizerMap.put(0, new UuidTokenizer());
        tokenizerMap.put(1, new GuidTokenizer());
        tokenizerMap.put(2, new LuhnTokenizer());
        tokenizerMap.put(3, new RandomNumber());
        tokenizerMap.put(4, new Luhn4Tokenizer());
        tokenizerMap.put(5, new AlphaNumeric());
        tokenizerMap.put(6, new AlphaNumeric4());
        tokenizerMap.put(7, new CreditCard());
        tokenizerMap.put(8, new CreditCard4());
    }
}
