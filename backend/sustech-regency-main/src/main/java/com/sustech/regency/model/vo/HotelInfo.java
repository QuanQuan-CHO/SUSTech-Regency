package com.sustech.regency.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelInfo {
	private Integer id;
	private Float latitude;
	private Float longitude;
	private String name;
	private String tel;
	private String address;
	private String provinceName;
	private String cityName;
	private String regionName;
	private Float stars;
	private String coverUrl;
	private List<String> videoUrls;
	private List<String> pictureUrls;
	private Float minPrice;
	private Integer commentNum;
	private Integer likes_num;
	private String description;
}
