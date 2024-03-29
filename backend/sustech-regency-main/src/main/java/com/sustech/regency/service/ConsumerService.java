package com.sustech.regency.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sustech.regency.db.po.Order;
import com.sustech.regency.db.po.Room;
import com.sustech.regency.model.vo.HotelInfo;
import com.sustech.regency.model.vo.OrderInfo;
import com.sustech.regency.model.vo.PayInfo;
import com.sustech.regency.model.vo.RoomInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface ConsumerService {
    /**
     * @return 文件上传成功后的获取URL
     */
    String uploadCommentMedia(MultipartFile media, Long orderId);

    void deleteCommentMedia(String mediaId, Long orderId);

    void cancelOrder(Long orderId);

    /**
     * @return 支付二维码图片的Base64编码
     */
    PayInfo reserveRoom(Integer roomId, Date startTime, Date endTime);

    void roomPayed(Long orderId, Date payTime);

    void like(Integer hotelId);

    void dislike(Integer hotelId);

    List<HotelInfo> getHotelInfoFromLikes();

    IPage<HotelInfo> getHotelInfoFromLikes(Integer pageNum, Integer pageSize);

    List<OrderInfo> getOrders();

    List<OrderInfo> selectCustomerOrders(Boolean isComment, Date startTime, Date EndTime, Integer status);

    List<Room> getRoomInfosByCustomerChoice(Integer hotelId, Date startTime, Date EndTime, Integer minPrice, Integer maxPrice, Integer roomTypeId);

    void uploadComment(Long orderId, String comment);

    void uploadCommentStar(Long orderId, Float star);


}
