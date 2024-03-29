package com.sustech.regency.db.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 酒店的图片或视频
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HotelExhibition implements Exhibitable<Hotel>{
	private Integer hotelId;
	private String fileId;

	@Override
	public void setMediaId(String mediaId) {
		fileId=mediaId;
	}

	@Override
	public String getMediaId() {
		return fileId;
	}

	@Override
	public void setDisplayId(Long displayId) {
		hotelId=Math.toIntExact(displayId);
	}

	@Override
	public Long getDisplayId() {
		return Long.valueOf(hotelId);
	}
}
