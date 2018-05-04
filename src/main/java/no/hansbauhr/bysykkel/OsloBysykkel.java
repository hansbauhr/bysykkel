package no.hansbauhr.bysykkel;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@Singleton
@Path("/")
@Api(tags = "status", value = "/")
@SwaggerDefinition(
  info = @Info(
      title = "status", 
      version = "V${project.version}", 
      description = "Bysykkel status"),
  schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS})

public class OsloBysykkel {

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
      value = "stativ",
      notes = "Finner antall ledige sykler på stativet",
      response = Response.class, 
      hidden = false)
  @ApiResponses(
      value = {@ApiResponse(
          code = 200, 
          message = "Antall ledige sykler på stativet")})
  public Response ledigSykkel(@PathParam ("stativ") String stativ) {
    System.out.println(String.format("Henter informasjonfra stativ %s", stativ));
    return Response.ok().entity("{}").build();
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
      value = {@ApiResponse(code = 200, message = "Sykkelstativer", response = Stativer.class)})
  public Response stativ() {
    String clientId = "";
    BysykkelKlient tilkobling = BysykkelKlient.nyTilkobling();
    tilkobling.targetUri("https://oslobysykkel.no/api/v1/stations");
    tilkobling.runRequest(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, clientId);
    System.out.println(tilkobling.getResponse().getStatusInfo());
    String entity = tilkobling.getResponse().readEntity(String.class);
    System.out.println(entity);
    Stativer fromJson = new Gson().fromJson(entity, Stativer.class);
    System.out.println(fromJson.stations().get(0).title());
    return Response.ok().entity(fromJson).build();
  }
  
}
