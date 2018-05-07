package no.hansbauhr.bysykkel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@Singleton
@Path("/")
@Api(tags = "Endepunkter", value = "/")
@SwaggerDefinition(
  info = @Info(
      title = "status", 
      version = "V${project.version}", 
      description = "Finn en ledig bysykkel!"),
  schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS})

public class OsloBysykkel {
  private static final String STATIONS = "/stations";
  String apiUrlOsloBysykkel = System.getenv("API_URL_OSLO_BYSYKKEL");
  String apiNokkel = System.getenv("API_NOKKEL");

  @Context
  public HttpServletRequest servletRequest;

  @GET
  @ApiOperation(
      hidden = true, 
      value = "redirect til dok")
  public Response redirectToDocumentation() throws URISyntaxException {
    return Response.temporaryRedirect(new URI("swagger-ui/index.html")).build();
  }

  @GET
  @Path("/helsesjekk")
  @Produces(MediaType.TEXT_PLAIN)
  @ApiOperation(
      value = "Helsesjekk",
      notes = "Returnerer Jeg lever!",
      response = Response.class, 
      hidden = false)
  @ApiResponses(
      value = {@ApiResponse(
          code = 200, 
          message = "Helsesjekk")})
  public Response helsesjekk() {
    return Response.ok().entity("Jeg lever!").build();
  }
  
  @GET
  @Path("/ledigsykkel/{stativ}")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Sjekk om det fins ledige bysykler på en gitt lokasjon",
      notes = "Finner antall ledige sykler på stativet",
      response = Response.class, 
      hidden = false)
  @ApiResponses(
      value = {@ApiResponse(
          code = 200, 
          message = "Antall ledige sykler på stativet",
          response = Stations.class)
      })
  public Response ledigSykkel(@PathParam ("stativ") final String stativ) {
    Response response = stativ();
    Stations stations = ((BysyklerOslo)response.getEntity()).stations().stream().filter(s->s.title().equalsIgnoreCase(stativ)).collect(Collectors.toList()).get(0);
    
    BysykkelKlient tilkobling = BysykkelKlient.nyTilkobling();
    String targetUri = apiUrlOsloBysykkel + STATIONS + "/availability";
    System.out.println(targetUri);
    tilkobling.targetUri(targetUri);
    tilkobling.runRequest(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiNokkel);
    StatusType statusInfo = tilkobling.getResponse().getStatusInfo();
    System.out.println(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
    
    String entity = tilkobling.getResponse().readEntity(String.class);
    BysyklerOslo bysyklerOslo = new Gson().fromJson(entity, BysyklerOslo.class);
    Optional<Stations> findFirst = bysyklerOslo.stations().stream().filter(s->s.id().intValue() == stations.id().intValue()).findFirst();
    findFirst.get().title(stations.title());
    findFirst.get().subtitle(stations.subtitle());
    return Response.ok().entity(findFirst.get()).build();
  }
  
  
  @GET
  @Path("/stativ")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Hent sykkelstativnavn",
      notes = "Finner navn på alle sykkelstativene",
      response = Response.class, 
      hidden = false)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Sykkelstativer", response = BysyklerOslo.class)})
  public Response stativ() {
    BysykkelKlient tilkobling = BysykkelKlient.nyTilkobling();
    String targetUri = apiUrlOsloBysykkel + STATIONS;
    System.out.println(targetUri);
    tilkobling.targetUri(targetUri);
    tilkobling.runRequest(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiNokkel);
    StatusType statusInfo = tilkobling.getResponse().getStatusInfo();
    System.out.println(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
    BysyklerOslo bysyklerOslo = new Gson().fromJson(tilkobling.getResponse().readEntity(String.class), BysyklerOslo.class);
    return Response.ok().entity(bysyklerOslo).build();
  }
  
}
