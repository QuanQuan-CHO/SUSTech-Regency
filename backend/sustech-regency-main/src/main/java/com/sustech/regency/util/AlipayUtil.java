package com.sustech.regency.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import static com.sustech.regency.util.QRCodeUtil.encode;

@Component
public class AlipayUtil {

	@Value("${alipay.notify-url}")
	private String notifyUrl;
	@Resource
	private AlipayClient alipayClient;

	/**
	 * @return 支付二维码的Base64编码
	 */
	public String getPayQrCode(AlipayTradePrecreateModel payInfo){
		AlipayTradePrecreateRequest payRequest = new AlipayTradePrecreateRequest();
		payRequest.setBizModel(payInfo); //设置支付信息
		payRequest.setNotifyUrl(notifyUrl); //异步回调地址
		String qrCodeUrl = getResponse(payRequest)
						  .getByPath("alipay_trade_precreate_response.qr_code", String.class);
		return encode(qrCodeUrl,"alipay-logo.png",true);
	}

	public void refund(AlipayTradeRefundModel refundInfo){
		AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
		refundRequest.setBizModel(refundInfo);
		//Todo
	}

	/**
	 * @return Response的body的 {@link JSONObject}
	 */
	@SneakyThrows(AlipayApiException.class)
	public <T extends AlipayResponse> JSONObject getResponse(AlipayRequest<T> request){
		return JSONUtil.parseObj(alipayClient.execute(request).getBody());
	}
}
