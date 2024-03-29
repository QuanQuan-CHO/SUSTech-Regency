package com.sustech.regency.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sustech.regency.db.dao.CityDao;
import com.sustech.regency.db.dao.ProvinceDao;
import com.sustech.regency.db.dao.RegionDao;
import com.sustech.regency.db.po.*;
import com.sustech.regency.model.vo.Comment;
import com.sustech.regency.model.vo.HotelInfo;
import com.sustech.regency.model.vo.RoomInfo;
import com.sustech.regency.service.ConsumerService;
import com.sustech.regency.service.PublicService;
import com.sustech.regency.web.annotation.DateParam;
import com.sustech.regency.web.annotation.DateTimeParam;
import com.sustech.regency.web.annotation.PathController;
import com.sustech.regency.web.handler.ApiException;
import com.sustech.regency.web.vo.ApiResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@PathController("/public")
public class PublicController {
    @Resource
    private PublicService publicService;

    @Resource
    private ProvinceDao provinceDao;

    @Resource
    private ConsumerService consumerService;

    @ApiOperation("获取所有省")
    @GetMapping("/province/all")
    public ApiResponse<List<Province>> getAllProvinces() {
        List<Province> provinces = provinceDao.selectList(null);
        return ApiResponse.success(provinces);
    }

    @Resource
    private CityDao cityDao;

    @ApiOperation("获取一个省的所有市")
    @GetMapping("/city/all")
    public ApiResponse<List<City>> getAllCities(@RequestParam(required = false) String province) {
        MPJLambdaWrapper<City> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(City.class)
                .innerJoin(Province.class, Province::getId, City::getProvinceId)
                .eq(isNotEmpty(province), Province::getName, province);
        return ApiResponse.success(cityDao.selectJoinList(City.class, wrapper));
    }

    @ApiOperation("用户预定酒店时多参数筛选房间")
    @GetMapping("/hotel/consumer-select-rooms")
    public ApiResponse<List<Room>> getSelectedRooms(@ApiParam(value = "酒店Id", required = true) @RequestParam @javax.validation.constraints.NotNull Integer hotelId,
                                                    @ApiParam(value = "开始时间") @RequestParam(required = false) @DateParam Date startTime,
                                                    @ApiParam(value = "结束时间") @RequestParam(required = false) @DateParam Date endTime,
                                                    @ApiParam(value = "最低价格") @RequestParam(required = false) Integer minPrice,
                                                    @ApiParam(value = "最高价格") @RequestParam(required = false) Integer maxPrice,
                                                    @ApiParam(value = "房型ID") @RequestParam(required = false) Integer roomTypeId) {
        return ApiResponse.success(consumerService.getRoomInfosByCustomerChoice(hotelId, startTime, endTime, minPrice, maxPrice, roomTypeId));
    }

    @Resource
    private RegionDao regionDao;

    @ApiOperation("获取一个城市的所有区")
    @GetMapping("/region/all")
    public ApiResponse<List<Region>> getAllRegions(@RequestParam(required = false) String province,
                                                   @RequestParam(required = false) String city) {
        MPJLambdaWrapper<Region> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Region.class)
                .innerJoin(City.class, City::getId, Region::getCityId)
                .innerJoin(Province.class, Province::getId, City::getProvinceId)
                .eq(isNotEmpty(province), Province::getName, province)
                .eq(isNotEmpty(city), City::getName, city);
        return ApiResponse.success(regionDao.selectJoinList(Region.class, wrapper));
    }

    @Value("${file-root-path}")
    private String fileRootPath; //保存文件的根路径

    @SuppressWarnings("CommentedOutCode")
    @ApiOperation(value = "获取文件", hidden = true)
    @GetMapping("/file/**")
    public void getFile(HttpServletResponse response, HttpServletRequest request) {
        String path = request.getRequestURI().replace("/public/file", "");
        File file = new File(fileRootPath + path);
        if (!file.exists()) {
            throw ApiException.badRequest("文件不存在");
        }
        response.reset();
        response.setContentLength((int) file.length());
        response.setContentType("application/octet-stream"); //MIME类型，这里表示除文本文件外的默认值
//		response.setCharacterEncoding("utf-8");
//		response.setHeader("Content-Disposition","inline"); //在浏览器中直接打开而不下载，但是对application/octet-stream的MIME类型无效
//		response.setHeader("Content-Disposition","attachment; filename="+newName); //作为附件下载，并重新命名为newName

        byte[] bytes = FileUtil.readBytes(file);
        try {
            response.getOutputStream().write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw ApiException.internalServerError("无法获取文件");
        }
    }

    @ApiOperation("根据省市区酒店名字分页查询获得酒店信息")
    @GetMapping("/get-hotels-by-location")
    public ApiResponse<IPage<HotelInfo>> getHotels(@ApiParam(value = "省份名字") @RequestParam(required = false) String ProvinceName,
                                                   @ApiParam(value = "城市名字") @RequestParam(required = false) String CityName,
                                                   @ApiParam(value = "区的名字") @RequestParam(required = false) String RegionName,
                                                   @ApiParam(value = "酒店名字") @RequestParam(required = false) String HotelName,
                                                   @ApiParam(value = "页数") Integer pageNum,
                                                   @ApiParam(value = "页面大小") Integer pageSize) {
        IPage<HotelInfo> hotelsByLocation = publicService.getHotelsByLocation(ProvinceName, CityName, RegionName, HotelName, pageNum, pageSize);
        List<HotelInfo> hotels = hotelsByLocation.getRecords();
        for (HotelInfo hotelInfo : hotels) {
            hotelInfo.setMinPrice(publicService.getMinPriceOfHotel(hotelInfo.getId()));
            hotelInfo.setCommentNum(publicService.getCommentsNumberByHotel(hotelInfo.getId()));
            hotelInfo.setLikes_num(publicService.getLikesNumByHotelId(hotelInfo.getId()));
        }
        return ApiResponse.success(hotelsByLocation);
    }

    @ApiOperation("根据酒店ID获取对应所有的房间")
    @GetMapping("/get-rooms-by-hotel")
    public ApiResponse<List<Room>> getRoomsByHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId,
                                                   @ApiParam(value = "房型ID") @RequestParam(required = false) Integer roomTypeId) {
        return ApiResponse.success(publicService.getRoomsByHotel(hotelId, roomTypeId));
    }

    @ApiOperation("根据酒店ID获取房间最低价格")
    @GetMapping("/get-min_price-by-hotel")
    public ApiResponse<Float> getMinPriceByHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getMinPriceOfHotel(hotelId));
    }

    @ApiOperation("根据酒店ID获取评论数量")
    @GetMapping("/get-comment_number-by-hotel")
    public ApiResponse<Integer> getCommentOfHotel(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getCommentsNumberByHotel(hotelId));
    }

    @ApiOperation("根据房间Id获取房间信息")
    @GetMapping("/get-roomInfo-by-roomId")
    public ApiResponse<RoomInfo> getRoomInfoByRoomId(@ApiParam(value = "房间Id", required = true) @RequestParam @NotNull Integer roomId) {
        return ApiResponse.success(publicService.getRoomInfoByRoomId(roomId));
    }

    @ApiOperation("根据酒店Id获得所有房型信息")
    @GetMapping("/get-roomTypes-by-HotelId")
    public ApiResponse<List<RoomType>> getRoomTypesByHotelId(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getRoomTypesByHotelId(hotelId));
    }

    @ApiOperation("根据酒店Id获取商家用户名")
    @GetMapping("/merchant-username")
    public ApiResponse<String> getMerchantUsernameByHotelId(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        String username = publicService.getMerchantUsernameByHotelId(hotelId);
        if (username == null) {
            return ApiResponse.badRequest("No such hotel");
        } else {
            return ApiResponse.success(username);
        }
    }

    @ApiOperation("根据酒店Id获取所有评论")
    @GetMapping("/get-hotelComments")
    public ApiResponse<List<Comment>> getCommentsByHotelId(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getCommentsByHotelId(hotelId));
    }

    @ApiOperation("根据酒店Id获取酒店信息")
    @GetMapping("/get-hotelInfo-byId")
    public ApiResponse<HotelInfo> getHotelInfoByHotelId(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        HotelInfo hotelInfo = publicService.getOneHotelByHotelId(hotelId);
        if (hotelInfo == null) {
            return ApiResponse.badRequest("No such hotel");
        }
        hotelInfo.setMinPrice(publicService.getMinPriceOfHotel(hotelId));
        hotelInfo.setCommentNum(publicService.getCommentsNumberByHotel(hotelId));
        hotelInfo.setLikes_num(publicService.getLikesNumByHotelId(hotelId));
        return ApiResponse.success(hotelInfo);
    }

    @ApiOperation("根据酒店ID和房号获得房间ID")
    @GetMapping("/get-roomId-byHotelWithRoomNUm")
    public ApiResponse<Integer> getRoomIdByHotelIdWithRoomNum(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId,
                                                              @ApiParam(value = "房间号", required = true) @RequestParam @NotNull Integer roomId) {
        return ApiResponse.success(publicService.getRoomIdByHotelWithRoomNum(hotelId, roomId));
    }

    @ApiOperation("根据酒店ID获得房间types")
    @GetMapping("/get-roomTypes-byHotelId")
    public ApiResponse<List<RoomType>> getRoomIdByHotelIdWithRoomNum(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getRoomTypesByHotelId(hotelId));
    }

    @ApiOperation("根据酒店Id获取所有评论平均星级打分")
    @GetMapping("/get-hotel-Comments-avgStar")
    public ApiResponse<Float> getAvgStarsByHotelId(@ApiParam(value = "酒店Id", required = true) @RequestParam @NotNull Integer hotelId) {
        return ApiResponse.success(publicService.getAvgStarsByHotelId(hotelId));
    }

    /**
     * 事实上，该接口很容易被攻击者调用，需要保证安全性，包括但不限于：
     * <ul>
     *      <li>【重要】验证签名，详见<a href="https://opendocs.alipay.com/common/02mse7">支付宝官方文档</a></li>
     *      <li>验证out_trade_no是否为系统中创建的订单号</li>
     *      <li>验证total_amount支付金额</li>
     *      <li>验证seller_id(seller_email))是否为out_trade_no这笔单据的对应的操作方(一个商户可能有多个seller_id/seller_email）</li>
     *      <li>验证app_id是否正确</li>
     *      <li>验证trade_status是否为TRADE_SUCCESS或TRADE_FINISHED</li>
     * </ul>
     */
    @ApiOperation(value = "用户扫码支付后，支付宝的通知API", hidden = true)
    @PostMapping("/payed")
    public void payed(@RequestParam("gmt_create") @DateTimeParam Date payTime,
                      @RequestParam("out_trade_no") Long orderId) {
        consumerService.roomPayed(orderId, payTime);
    }
}
