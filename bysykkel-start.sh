#!/bin/bash
#

mvn clean install

docker run --rm -it -e API_NOKKEL='<DIN_KLIENT_IDENTIFIER>' -e API_URL_OSLO_BYSYKKEL='https://oslobysykkel.no/api/v1' -p 8080:8080 -v <PATH_TIL_DIN_ARBEIDSMAPPE>/bysykkel/target/oslo-bysykkel-1.0.0-SNAPSHOT.war:/usr/local/tomcat/webapps/bysykkel.war tomcat:8.5
