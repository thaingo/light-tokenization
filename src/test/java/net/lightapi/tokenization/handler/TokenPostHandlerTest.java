
package net.lightapi.tokenization.handler;

import com.networknt.client.Http2Client;
import com.networknt.exception.ApiException;
import com.networknt.exception.ClientException;
import io.undertow.UndertowOptions;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import java.io.IOException;


public class TokenPostHandlerTest {
    @ClassRule
    public static TestServer server = TestServer.getInstance();

    static final Logger logger = LoggerFactory.getLogger(TokenPostHandlerTest.class);
    static final boolean enableHttp2 = server.getServerConfig().isEnableHttp2();
    static final boolean enableHttps = server.getServerConfig().isEnableHttps();
    static final int httpPort = server.getServerConfig().getHttpPort();
    static final int httpsPort = server.getServerConfig().getHttpsPort();
    static final String url = enableHttp2 || enableHttps ? "https://localhost:" + httpsPort : "http://localhost:" + httpPort;

    @Test
    public void testTokenPostHandlerTest() throws ClientException, ApiException {
        final String requestBody = "{\"schemeId\":1,\"value\":\"1234567890\"}";
        final String authToken = "Bearer eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTgzNzA5NDMzMywianRpIjoiUk1RZXN0MUVBTGY5ZFJHSl8tSEowQSIsImlhdCI6MTUyMTczNDMzMywibmJmIjoxNTIxNzM0MjEzLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJzY29wZSI6WyJ0b2tlbi5yIiwidG9rZW4udyJdfQ.fVmHPr5vlDf01zhIiRio1N4-lRIaKShjWis1lEJOXx3oHnkqWWiog0JWIw7R_7b5siPgvPuJOMSi5zDQjva9D-EiIRGGcwp9Egb_gqOLLvMaux3fZrzYX8WSk_VcpDtbHb303DyAWuOkgMM9VamDZT6sP66qTAVU5Ao0iS9bi3kTyv13_To2nXVDeb1FTTXHcw8gSY-2HpjsIx5IDf8rayMMp1p1Y6heyQBrVN5qJd1UhmWwuzsj3VwX_iSx-qw7AFZResTobHntRlbPX5D2Xo0fMDZ7HR8JzT_32aWLheOmionfOFUeuve9WtDk5c0TypcNMgiJi6WVjYcjZCcmBg";
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), Http2Client.WORKER, Http2Client.SSL, Http2Client.POOL, enableHttp2 ? OptionMap.create(UndertowOptions.ENABLE_HTTP2, true): OptionMap.EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v1/token").setMethod(Methods.POST);
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");
            request.getRequestHeaders().put(Headers.AUTHORIZATION, authToken);
            connection.sendRequest(request, client.createClientCallback(reference, latch, requestBody));
            latch.await();
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
        logger.debug("body = " + body);
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(body);
    }

    @Test
    public void testExistingValue() throws ClientException, ApiException {
        final String requestBody = "{\"schemeId\":1,\"value\":\"4323927763\"}";
        final String authToken = "Bearer eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTgzNzA5NDMzMywianRpIjoiUk1RZXN0MUVBTGY5ZFJHSl8tSEowQSIsImlhdCI6MTUyMTczNDMzMywibmJmIjoxNTIxNzM0MjEzLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJzY29wZSI6WyJ0b2tlbi5yIiwidG9rZW4udyJdfQ.fVmHPr5vlDf01zhIiRio1N4-lRIaKShjWis1lEJOXx3oHnkqWWiog0JWIw7R_7b5siPgvPuJOMSi5zDQjva9D-EiIRGGcwp9Egb_gqOLLvMaux3fZrzYX8WSk_VcpDtbHb303DyAWuOkgMM9VamDZT6sP66qTAVU5Ao0iS9bi3kTyv13_To2nXVDeb1FTTXHcw8gSY-2HpjsIx5IDf8rayMMp1p1Y6heyQBrVN5qJd1UhmWwuzsj3VwX_iSx-qw7AFZResTobHntRlbPX5D2Xo0fMDZ7HR8JzT_32aWLheOmionfOFUeuve9WtDk5c0TypcNMgiJi6WVjYcjZCcmBg";
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), Http2Client.WORKER, Http2Client.SSL, Http2Client.POOL, enableHttp2 ? OptionMap.create(UndertowOptions.ENABLE_HTTP2, true): OptionMap.EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v1/token").setMethod(Methods.POST);
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");
            request.getRequestHeaders().put(Headers.AUTHORIZATION, authToken);
            connection.sendRequest(request, client.createClientCallback(reference, latch, requestBody));
            latch.await();
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
        logger.debug("body = " + body);
        Assert.assertEquals(200, statusCode);
        Assert.assertTrue("4158281123".equals(body));
    }

    @Test
    public void testDatabaseNotFound() throws ClientException, ApiException {
        final String requestBody = "{\"schemeId\":1,\"value\":\"1234567890\"}";
        final String authToken = "Bearer eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTgzNzA5NDM1NywianRpIjoieFZMMnV1bU1ON3Q0dDNYbzB4c0RxUSIsImlhdCI6MTUyMTczNDM1NywibmJmIjoxNTIxNzM0MjM3LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzMiLCJzY29wZSI6WyJ0b2tlbi5yIiwidG9rZW4udyJdfQ.pPJhuZ8jAgkLCS4V7p6ZPzB-vUcjNDJYBYo2y03DMESPDmSTTsM_btsfCIM6BJ4Rxthgb8kbiPoG9pbcltVThKoq7BH5RSMYvLFEYN-xo9qk3HEHEBswvtnBEt76QLoPVz-T0VJ2ktn7NEdnCYdFOHxRBPA7dXiQZYN2wfGIyconbWN9l5htkCL0DXVSmmhgZM-VCaH8TCdQ__NO1sUsheBqO2-me3_GX3Ppiou0qtzehQ2tipF67KaFgla0wHJpqv8TcpU2P3WDjeF0F3y4dK_QqtyrKJoypFrQ7rFJjkAzHRbWaf4_XXzaUFGIiHOBlbj1lwarjj2Jh-P5r_pMXg";
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), Http2Client.WORKER, Http2Client.SSL, Http2Client.POOL, enableHttp2 ? OptionMap.create(UndertowOptions.ENABLE_HTTP2, true): OptionMap.EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v1/token").setMethod(Methods.POST);
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");
            request.getRequestHeaders().put(Headers.AUTHORIZATION, authToken);
            connection.sendRequest(request, client.createClientCallback(reference, latch, requestBody));
            latch.await();
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
        logger.debug("body = " + body);
        Assert.assertEquals(400, statusCode);
        Assert.assertNotNull(body);
        Assert.assertTrue(body.contains("ERR12102"));
    }

}
