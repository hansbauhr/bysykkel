package no.hansbauhr.bysykkel;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class RestKlient {
  private static final String HEADER_CLIENT_IDENTIFIER = "Client-Identifier";

  private static final Logger LOGGER = LoggerFactory.getLogger(RestKlient.class);

  private Response response;
  protected static Client client;
  private String targetUri;

  String feilmelding;

  protected static Client init() {
    SSLContext sslContext = null;
    System.setProperty("https.protocols", "TLSv1.2");// Java 8

    try {
      sslContext = SSLContext.getDefault();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("msg=\"Feil under initialisering av SSLContext\"", e);
    }

    return ClientBuilder.newBuilder()
        .sslContext(sslContext)
        .hostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier())
        .property(ClientProperties.CONNECT_TIMEOUT, 12000)
        .property(ClientProperties.READ_TIMEOUT, 12000)
        .build();
  }

  public Status runRequest(String acceptType, String contentType, String clientId) {
    try {
      Builder request = lagRequest(acceptType).header(HEADER_CLIENT_IDENTIFIER, clientId);

      response = request.get();
    } catch (ProcessingException e) {
      logFeilmelding(e);
      return Status.INTERNAL_SERVER_ERROR;
    }

    return responseStatus();

  }

  public Response getResponse() {
    return response;
  }

  public String getTargetUri() {
    return targetUri;
  }

  public void targetUri(String targetUri) {
    this.targetUri = targetUri;
  }

  private Status responseStatus() {
    return Status.fromStatusCode(response.getStatus());
  }

  private void logFeilmelding(ProcessingException e) {
    LOGGER.error("msg=\"API-kall feilet: " + targetUri, e);
    feilmelding = e.getMessage();
  }

  private Builder lagRequest(String acceptType) {
    return client.target(targetUri).request(acceptType);
  }

}