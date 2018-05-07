# bysykkel
Finn ledige bysykler i Oslo.


Shellskriptet *bysykkel-start.sh* brukes for å kjøre koden i en docker. Legg inn din API nøkkel og byggepath i skriptet før du kjører det.

Åpne en nettleser og gå til Swagger UI for applikasjonen for å teste med GUI:

http://localhost:8080/bysykkel/


Test applikasjonen med CURL:

*Sjekk Nydalsveien:*

curl -X GET "http://localhost:8080/bysykkel/ledigsykkel/Nylandsveien" -H "accept: application/json"

*Sjekk Tåsenløkka:*
curl -X GET "http://localhost:8080/bysykkel/ledigsykkel/T%C3%A5senl%C3%B8kka" -H "accept: application/json"
