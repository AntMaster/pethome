package com.shumahe.pethome.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class PrivateMsgDTO {

    /**
     * 互动主键
     */
    private Integer id;

    /**
     * 评论主键
     */
    private Integer talkId;


    /**
     * 发送人ID
     */
    private String userIdFrom;


    /**
     * 发送人昵称
     */
    private String userIdFromName;


    /**
     * 发送人头像
     */
    private String userIdFromPhoto;


    /**
     * 接收人ID
     */
    private String userIdAccept;


    /**
     * 接收人昵称
     */
    private String  userIdAcceptName;


    /**
     * 接收人头像
     */
    private String  userAcceptPhoto;


    /**
     * 互动内容
     */
    private String content;


    /**
     * 互动时间
     */
    private Date talkDate;


    /**
     * 阅读状态
     */
    private Integer readState;
}
