
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


public class SchemeGetHandlerTest {
    @ClassRule
    public static TestServer server = TestServer.getInstance();

    static final Logger logger = LoggerFactory.getLogger(SchemeGetHandlerTest.class);
    static final boolean enableHttp2 = server.getServerConfig().isEnableHttp2();
    static final boolean enableHttps = server.getServerConfig().isEnableHttps();
    static final int httpPort = server.getServerConfig().getHttpPort();
    static final int httpsPort = server.getServerConfig().getHttpsPort();
    static final String url = enableHttp2 || enableHttps ? "https://localhost:" + httpsPort : "http://localhost:" + httpPort;

    @Test
    public void testSchemeGetHandlerTest() throws ClientException, ApiException {
        final String authToken = "Bearer eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTgzNzEwODM3NCwianRpIjoiNUVIWDFzXzlDZkhXQjY3dk1XOHRudyIsImlhdCI6MTUyMTc0ODM3NCwibmJmIjoxNTIxNzQ4MjU0LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJzY29wZSI6WyJ0b2tlbi5yIiwidG9rZW4udyIsInNjaGVtZS5yIl19.r_3u4HAJpCztcX8HhV5kihSj6V2gBbqfB4Bdjr3arRKHKJdncaaoDRcYgXihdtutBsA7QVRimu576HL6FwV9iurpqEEA-uy-rzfuCfXJYP4s4F5C_PFaeroGi9siG_dc34p-iFh6eA6dksa6pwBiko9Pb1eBO8XfIV7ndNUmqTUbEvjV6J_Nv_aVPoDgOz00laMDDgj3bOtkz3lGTrfZCQloAhagthfMcUzyj04qe_bKZFKcrbCxXfBjelItUBwdt1td8FBpiSQPI0FXVud69TFmDmDZT6UXci8qJVOb0vuADJcPFe5PEWXxIORoduU8an0Mtey5svQx3c0W_Gqvcg";

        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, enableHttp2 ? OptionMap.create(UndertowOptions.ENABLE_HTTP2, true): OptionMap.EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v1/scheme").setMethod(Methods.GET);
            request.getRequestHeaders().put(Headers.AUTHORIZATION, authToken);
            connection.sendRequest(request, client.createClientCallback(reference, latch));
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
}
