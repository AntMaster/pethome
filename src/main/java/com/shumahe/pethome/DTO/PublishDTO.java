package com.shumahe.pethome.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;

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

    private Integer classifyId;

    private String petName;

    private String petImage;

    private Date lostTime;

    private String lostLocation;

    private Float latitude;

    private Float longitude;

    private String ownerName;

    private String ownerContact;

    private String petDescription;

    private Integer viewCount;

    private Integer shareCount;

    private boolean likeState = false;

    //私信条数
    private Integer privateMsgCount = 0;

    //互动条数
    private Integer publicMsgCount = 0;

    //私信信息
    private List<List<PrivateMsgDTO>> privateTalkDTO;

    //互动信息
    private List<List<PublicMsgDTO>> publicTalkDTO;

}
