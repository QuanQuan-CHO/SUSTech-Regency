package com.sustech.regency.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer roomNum;
    private Integer capacity;
    private Integer toiletNum;
    private Boolean hasLivingRoom;
    private String coverId;
}
