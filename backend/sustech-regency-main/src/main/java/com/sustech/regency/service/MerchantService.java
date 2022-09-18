package com.sustech.regency.service;

import com.sustech.regency.db.po.Hotel;
import com.sustech.regency.db.po.Order;
import com.sustech.regency.db.po.Room;
import com.sustech.regency.db.po.User;
import io.swagger.models.auth.In;
import org.apache.ibatis.jdbc.Null;

import java.lang.reflect.Type;
import java.util.List;

public interface MerchantService {
    //商家登记新酒店
    Boolean addNewHotel(Hotel hotel);
    //得到某个商家旗下的所有酒店
    List<Hotel> getAllHotels(Integer merchantId, Integer cityId, Integer regionId);
    //商家删除酒店
    Boolean deleteHotel(Integer merchantId,Integer hotelId);
    //商家更新酒店信息，如名称
    Boolean updateHotel(Integer hotelId,Float latitude, Float longitude,Integer regionId, Integer merchantId, String name, String tel);
    //得到旗下某个酒店的所有房间信息
    Hotel getOneHotel(Integer hotelId,Float latitude, Float longitude,Integer cityId, Integer merchantId, String name, String tel);
    //查询某个商家的所有订单
    List<Order> getOrders(Integer merchantId,Integer hotelId,Integer roomId,Integer cityId);




}
