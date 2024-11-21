package kr.co._icia.finalProject.util.weatherRespone;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class WeatherForecast {
  
  private List<OpenWeather> list;
  
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  public static class OpenWeather{
    private WheatherMain main;
    private List<Wheather> weather;
    private Clouds clouds;
    private Wind wind;
    private String visibility;
    private String pop;
    private Sys sys;
    @JsonProperty("dt_txt")
    private String dtTxt;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class WheatherMain{
      private String temp;
      @JsonProperty("feels_like")
      private String feelsLike;
      @JsonProperty("temp_min")
      private String tempMin;
      @JsonProperty("temp_max")
      private String tempMax;
      private String humidity;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Wheather{
      private Long id;
      private String main;
      private String description;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Clouds{
      private String all;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Wind{
      private String speed;
      private String deg;
      private String gust;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Sys{
      private String pod;
    }
    
  }

}
