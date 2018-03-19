package com.shumahe.pethome.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shumahe.pethome.Util.serializer.Date2LongSerializer;
import lombok.Data;


import java.util.Date;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PublishDTO {

    private Integer id;


    private String publisherId;


    private Integer publishType;


    //发布人昵称
    private String publisherName;

    //发布人头像
    private String publisherPhoto;

    //显示状态
    private Integer publishState;

    //类别
    private Integer classifyId;

    //品种
    private Integer varietyId;

    //品种名称
    private String varietyName;

    private String petName;

    private String petImage;

    //@JsonSerialize(using = Date2LongSerializer.class)
    //@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lostTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String lostLocation;

    private Float latitude;

    private Float longitude;

    private String ownerName;

    private String ownerContact;

    private String petDescription;

    private Integer viewCount;


    private Integer shareCount;


    private boolean likeState = false;

    //状态
    private boolean findState;

    //私信条数
    private Integer privateMsgCount = 0;

    //互动条数
    private Integer publicMsgCount = 0;

    //私信信息
    private List<List<PrivateMsgDTO>> privateTalk;

    //互动信息
    private List<List<PublicMsgDTO>> publicTalk;

}


