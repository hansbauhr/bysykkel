package no.hansbauhr.bysykkel;

public class BysykkelKlient extends RestKlient {
  
  public static BysykkelKlient nyTilkobling() {
    client = init();
    return new BysykkelKlient();
  }

}

