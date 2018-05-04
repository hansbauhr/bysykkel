package no.hansbauhr.bysykkel;

public class Stations {

  private Integer id;
  private String title;
  private String subtitle;
  private Availability availability;
  
  private Stations() {
  }

  public Integer id() {
    return id;
  }
  
  public String title() {
    return title;
  }
  
  public String subtitle() {
    return subtitle;
  }
  
  public Availability availability() {
    return availability;
  }
}
