package com.shumahe.pethome.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
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


    private Integer petSex;

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
    private String varietyName = "不告诉你哟~";

    private String petName;

    private String petImage;

    //@JsonSerialize(using = Date2LongSerializer.class)
    //@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date lostTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String lostLocation;

    private Float latitude;

    private Float longitude;

    private String ownerName;

    private String ownerContact;

    private String petDescription;

    private boolean likeState ;//关注状态

    //状态
    private Integer findState ;

    /**
     * 浏览
     */
    private Integer viewCount = 0;

    /**
     * 转发
     */
    private Integer shareCount = 0;


    /**
     *  关注
     */
    private Integer likeCount = 0;


    /**
     * 互动
     */
    private Integer publicMsgCount = 0;


    /**
     * 私信
     */
    private Integer privateMsgCount = 0;


    //私信信息
    private List<List<PrivateMsgDTO>> privateTalk;

    //互动信息
    private List<List<PublicMsgDTO>> publicTalk;

    /**
     * 用户认证状态
     */
    private Integer approveState = 0;


    private Integer approveType = 0;

}


