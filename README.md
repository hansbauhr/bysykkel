# bysykkel
Finn ledige bysykler i Oslo.

## Bygg applikasjonen med Docker
Last ned kildekoden, naviger til prosjekt mappen og kjør:
```sh
docker run --rm -it -v $(pwd):/project maven mvn package -f /project
```

## Kjør applikasjonen med Docker
```sh
docker run --rm -it -e API_NOKKEL='<DIN_KLIENT_IDENTIFIER>' -e API_URL_OSLO_BYSYKKEL='https://oslobysykkel.no/api/v1' -p 8080:8080 -v $(pwd)/target/oslo-bysykkel-1.0.0-SNAPSHOT.war:/usr/local/tomcat/webapps/bysykkel.war tomcat:8.5
```

`<DIN_KLIENT_IDENTIFIER>` er Oslo bysykkels *Client identifier*


Åpne en nettleser og gå til Swagger UI for å teste applikasjonen med GUI:

http://localhost:8080/bysykkel/


Du kan også teste applikasjonen med CURL:

*Sjekk Nydalsveien:*

curl -X GET "http://localhost:8080/bysykkel/ledigsykkel/Nylandsveien" -H "accept: application/json"

*Sjekk Tåsenløkka:*

curl -X GET "http://localhost:8080/bysykkel/ledigsykkel/T%C3%A5senl%C3%B8kka" -H "accept: application/json"
