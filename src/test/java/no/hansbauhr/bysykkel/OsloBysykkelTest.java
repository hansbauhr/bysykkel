package no.hansbauhr.bysykkel;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OsloBysykkelTest {

  @Rule
  public WireMockRule simOsloBysykkel = new WireMockRule(wireMockConfig().dynamicPort());
  
  public OsloBysykkelTest() {
    simOsloBysykkel.stubFor(get(urlEqualTo("/oslobysykkel.no/api/v1/stations"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readAsResource("stativ.json"))));
  }
  
  
  @Test
  public void skal_hente_stativer() {
    Response response = new OsloBysykkel().stativ();
    List<Stations> stations = ((Stativer)response.getEntity()).stations();
    Assert.assertEquals(207, stations.size());
    Assert.assertEquals("Nylandsveien", stations.get(0).title());
    Assert.assertEquals("mellom Norbygata og Urtegata", stations.get(0).subtitle());
  }
  
  public String readAsResource(String resourceName) {
    String content = null;
    try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
         InputStreamReader streamReader = new InputStreamReader(inputStream);) 
    {
      content = new BufferedReader(streamReader).lines().collect(Collectors.joining("\n"));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } 
    
    return content;
  }

}
