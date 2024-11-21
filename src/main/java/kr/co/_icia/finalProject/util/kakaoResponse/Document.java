package kr.co._icia.finalProject.util.kakaoResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
	public class Document {
        private Long id;
		private Double x;
		private Double y;
		
		@JsonProperty("place_name")
		private String placeName;
		
		private String phone;
		
		@JsonProperty("address_name")
		private String addressName;
		
		@JsonProperty("road_address_name")
		private String roadAddressName;
		
		@JsonProperty("category_group_name")
        private String categoryGroupName;

        @JsonProperty("place_url")
        private String placeUrl;
	}