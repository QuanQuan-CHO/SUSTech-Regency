package com.sustech.regency.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.sustech.regency.db.dao.ChatHistoryDao;
import com.sustech.regency.db.dao.UserDao;
import com.sustech.regency.db.po.ChatHistory;
import com.sustech.regency.db.po.Hotel;
import com.sustech.regency.db.po.Order;
import com.sustech.regency.db.po.User;
import com.sustech.regency.model.vo.HotelInfo;
import com.sustech.regency.service.MerchantService;
import com.sustech.regency.web.annotation.DateParam;
import com.sustech.regency.web.annotation.PathController;
import com.sustech.regency.web.vo.ApiResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sustech.regency.util.VerificationUtil.getUserId;

@PathController("/merchant")
public class MerchantController {
    @Resource
    private MerchantService merchantService;

    @Resource
    private ChatHistoryDao chatHistoryDao;

    @Resource
    private UserDao userDao;

    @ApiOperation("商家创建一个新的酒店")
    @PostMapping("/hotel/add")
    public ApiResponse<Boolean> addNewHotel(@ApiParam(value = "纬度", required = true) @RequestParam @NotNull Float latitude,
                                            @ApiParam(value = "经度", required = true) @RequestParam @NotNull Float longitude,
                                            @ApiParam(value = "所在区ID", required = true) @RequestParam @NotNull Integer regionId,
                                            @ApiParam(value = "酒店名字", required = true) @RequestParam @NotEmpty String name,
                                            @ApiParam(value = "酒店电话", required = true) @RequestParam @NotEmpty String tel,
                                            @ApiParam(value = "区域内详细地址", required = true) @RequestParam @NotEmpty String address
    ) {
        Hotel hotel = Hotel.builder().latitude(latitude)
                .longitude(longitude)
                .regionId(regionId)
                .merchantId(getUserId())
                .name(name)
                .tel(tel)
                .address(address)
                .build();
        return ApiResponse.success(merchantService.addNewHotel(hotel));
    }

    @ApiOperation("商家删除一个酒店")
    @PostMapping("/hotel/delete")
    public ApiResponse<Boolean> deleteHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam Integer hotelId) {
        return ApiResponse.success(merchantService.deleteHotel(getUserId(), hotelId));
    }

    @ApiOperation("商家更新一个酒店信息")
    @PostMapping("/hotel/update")
    public ApiResponse<Boolean> updateHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam Integer hotelId,
                                            @ApiParam(value = "纬度") @RequestParam(required = false) Float latitude,
                                            @ApiParam(value = "经度") @RequestParam(required = false) Float longitude,
                                            @ApiParam(value = "酒店名字") @RequestParam(required = false) String name,
                                            @ApiParam(value = "酒店电话") @RequestParam(required = false) String tel,
                                            @ApiParam(value = "区域内详细地址") @RequestParam(required = false) String address) {
        return ApiResponse.success(merchantService.updateHotel(hotelId, latitude, longitude, getUserId(), name, tel, address));
    }

    @ApiOperation("商家获取自己下面所有酒店信息")
    @GetMapping("/hotel/all")
    public ApiResponse<List<HotelInfo>> getAllHotels() {
        return ApiResponse.success(merchantService.getAllHotelInfos(getUserId()));
    }

    @ApiOperation("商家多参数查询一个酒店")
    @GetMapping("/hotel/get")
    public ApiResponse<HotelInfo> getOneHotel(@ApiParam(value = "酒店Id") @RequestParam(required = false) Integer hotelId,
                                              @ApiParam(value = "纬度") @RequestParam(required = false) Float latitude,
                                              @ApiParam(value = "经度") @RequestParam(required = false) Float longitude,
                                              @ApiParam(value = "酒店名字") @RequestParam(required = false) String name,
                                              @ApiParam(value = "酒店电话") @RequestParam(required = false) String tel) {
        return ApiResponse.success(merchantService.getOneHotel(hotelId, latitude, longitude, getUserId(), name, tel));
    }

    @ApiOperation(value = "商家上传酒店展示图片或视频", notes = "为指定的酒店(hotelId)上传展示图片(jpg,jpeg,png)或视频(mp4),返回文件上传成功后的获取url, 如https://quanquancho.com:8080/public/file/2022/09/30/2d02610787154be1af4816d5450b5ae8.jpg")
    @PostMapping("hotel/upload-media")
    public ApiResponse<Map> uploadHotelMedia(@ApiParam(required = true)
                                             @RequestParam MultipartFile media,

                                             @ApiParam(value = "酒店id", required = true)
                                             @NotNull @RequestParam Integer hotelId) {
        String url = merchantService.uploadHotelMedia(media, hotelId);
        return ApiResponse.success(Map.of("url", url));
    }

    @ApiOperation(value = "商家上传酒店封面", notes = "为指定的酒店(hotelId)上传封面(jpg,jpeg,png),返回文件上传成功后的获取url, 如https://quanquancho.com:8080/public/file/2022/09/30/2d02610787154be1af4816d5450b5ae8.jpg")
    @PostMapping("hotel/upload-cover")
    public ApiResponse<Map> uploadHotelCover(@ApiParam(required = true)
                                             @RequestParam MultipartFile picture,

                                             @ApiParam(value = "酒店id", required = true)
                                             @NotNull @RequestParam Integer hotelId) {
        String url = merchantService.uploadHotelCover(picture, hotelId);
        return ApiResponse.success(Map.of("url", url));
    }

    @ApiOperation(value = "商家删除酒店图片或视频", notes = "删除指定酒店(hotelId)的图片或视频(mediaId)")
    @PostMapping("hotel/delete-media")
    public ApiResponse deleteHotelMedia(@ApiParam(value = "图片或视频的32位uuid", required = true)
                                        @NotNull @RequestParam String mediaId,

                                        @ApiParam(value = "酒店id", required = true)
                                        @NotNull @RequestParam Integer hotelId) {
        merchantService.deleteHotelMedia(mediaId, hotelId);
        return ApiResponse.success();
    }

    @ApiOperation("商家查询某个酒店的流水")
    @GetMapping("/hotel/get-HistoricalBills")
    public ApiResponse<List<Float>> getHistoricalBills(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId,
                                                       @ApiParam(value = "开始时间", required = true) @RequestParam @DateParam @NotNull Date startTime,
                                                       @ApiParam(value = "结束时间", required = true) @RequestParam @DateParam @NotNull Date endTime,
                                                       @ApiParam(value = "房型ID") @RequestParam(required = false) Integer roomTypeId) {
        return ApiResponse.success(merchantService.getHotelHistoricalBills(hotelId, startTime, endTime, roomTypeId));
    }

    @ApiOperation("商家按条件筛选某个酒店的订单")
    @GetMapping("/hotel/get-selected-orders")
    public ApiResponse<List<Order>> getSelectedOrders(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId,
                                                      @ApiParam(value = "房间ID", required = false) @RequestParam(required = false) Integer roomId,
                                                      @ApiParam(value = "是否有评论", required = false) @RequestParam(required = false) Boolean isComment,
                                                      @ApiParam(value = "开始时间", required = false) @RequestParam(required = false) @DateParam Date startTime,
                                                      @ApiParam(value = "结束时间", required = false) @RequestParam(required = false) @DateParam Date endTime,
                                                      @ApiParam(value = "订单状态", required = false) @RequestParam(required = false) Integer status) {

        return ApiResponse.success(merchantService.selectCustomerOrders(hotelId, roomId, isComment, startTime, endTime, status));
    }

    @ApiOperation("商家对整个酒店或酒店某房型打折")
    @PostMapping("/hotel/on-sale")
    public ApiResponse notifySale(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId,
                                  @ApiParam(value = "房型ID") @RequestParam(required = false) Integer roomTypeId,
                                  @ApiParam(value = "折扣率", required = true) @RequestParam @NotNull Float discount) {
        merchantService.notifySale(hotelId, roomTypeId, discount);
        return ApiResponse.success();
    }

    @ApiOperation("查询当前所有和商家聊天过的用户")
    @GetMapping("/chat-users")
    public ApiResponse<List<String>> getChatUsers(@RequestParam Integer hotelId) {
        Integer merchantId = getUserId();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", merchantId);
        String merchantName = userDao.selectOne(wrapper).getName();
        QueryWrapper<ChatHistory> chatWrapper = new QueryWrapper<>();
        chatWrapper.eq("from_name", merchantName);
        chatWrapper.eq("hotel_id", hotelId);
        Set<String> res = chatHistoryDao.selectList(chatWrapper).stream().map(ChatHistory::getToName).collect(Collectors.toSet());
        chatWrapper = new QueryWrapper<>();
        chatWrapper.eq("to_name", merchantName);
        chatWrapper.eq("hotel_id", hotelId);
        res.addAll(chatHistoryDao.selectList(chatWrapper).stream().map(ChatHistory::getFromName).collect(Collectors.toSet()));
        List<String> users = res.stream().toList();
        return ApiResponse.success(users);
    }


}
