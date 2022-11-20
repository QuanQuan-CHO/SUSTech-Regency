package com.sustech.regency.controller;

import com.sustech.regency.model.param.ReserveParam;
import com.sustech.regency.db.po.Order;
import com.sustech.regency.model.vo.HotelInfo;
import com.sustech.regency.model.vo.PayInfo;
import com.sustech.regency.service.ConsumerService;
import com.sustech.regency.web.annotation.PathController;
import com.sustech.regency.web.vo.ApiResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.List;

@PathController("/consumer")
public class ConsumerController {
    @Resource
    private ConsumerService consumerService;


    @ApiOperation(value = "上传评论图片或视频", notes = "为指定的订单(orderId)上传评论图片(jpg,jpeg,png)或视频(mp4),返回文件上传成功后的获取url, 如https://quanquancho.com:8080/public/file/2022/09/30/2d02610787154be1af4816d5450b5ae8.jpg")
    @PostMapping("/comment/upload-media")
    public ApiResponse<Map> uploadCommentMedia(@ApiParam(required = true)
                                               @RequestParam MultipartFile media,

                                               @ApiParam(value = "订单id", required = true)
                                               @NotNull @RequestParam Integer orderId) {
        String url = consumerService.uploadCommentMedia(media, orderId);
        return ApiResponse.success(Map.of("url", url));
    }

    @ApiOperation(value = "删除评论图片或视频", notes = "删除指定的订单评论(orderId)的图片或视频")
    @PostMapping("/comment/delete-media")
    public ApiResponse deleteCommentMedia(@ApiParam(required = true)
                                          @NotNull @RequestParam String mediaId,

                                          @ApiParam(value = "订单id", required = true)
                                          @NotNull @RequestParam Integer orderId) {
        consumerService.deleteCommentMedia(mediaId, orderId);
        return ApiResponse.success();
    }


    @ApiOperation(value = "预订房间",notes = "返回支付二维码的Base64编码，过期时间15分钟")
    @PostMapping("/reserve-room")
    public ApiResponse<PayInfo> reserveRoom(@RequestBody @Validated ReserveParam reserveParam) {
        PayInfo payInfo=consumerService.reserveRoom(reserveParam.getRoomId(),
                                                    reserveParam.getStartTime(),
                                                    reserveParam.getEndTime(),
                                                    reserveParam.getCohabitants());
        return ApiResponse.success(payInfo);
    }

    @ApiOperation(value = "订单退款")
    @PostMapping("/refund")
    public ApiResponse refund(@RequestParam @NotNull Long orderId) {
        consumerService.cancelOrder(orderId);
        return ApiResponse.success();
    }

    @ApiOperation("收藏酒店")
    @PostMapping("/like-hotel")
    public ApiResponse likeHotel(@ApiParam(value = "酒店Id", required = true)
                                 @NotNull @RequestParam Integer hotelId){
        consumerService.like(hotelId);
        return ApiResponse.success();
    }

    @ApiOperation("移出收藏酒店")
    @PostMapping("/dislike-hotel")
    public ApiResponse dislikeHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam Integer hotelId){
        consumerService.dislike(hotelId);
        return ApiResponse.success();
    }

    @ApiOperation("用户查看自己收藏的酒店")
    @GetMapping("/get-likes")
    public ApiResponse<List<HotelInfo>> getLikes() {
        return ApiResponse.success(consumerService.getHotelInfoFromLikes());
    }

    @ApiOperation("用户查看自己订单")
    @GetMapping("/get-orders")
    public ApiResponse<List<Order>> getOrders() {
        return ApiResponse.success(consumerService.getOrders());
    }
}
