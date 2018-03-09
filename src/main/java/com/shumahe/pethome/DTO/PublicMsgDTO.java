package com.shumahe.pethome.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicMsgDTO {


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
    private String replierFrom;

    /**
     * 发送人昵称
     */
    private String replierFromName;


    /**
     * 发送人头像
     */
    private String replierFromPhoto;


    /**
     * 接收人ID
     */
    private String replierAccept;


    /**
     * 接收人昵称
     */
    private String replierAcceptName;


    /**
     * 接收人头像
     */
    private String replierAcceptPhoto;

    /**
     * 互动内容
     */
    private String content;


    /**
     * 互动时间
     */
    private Date replyDate;


    /**
     * 阅读状态
     */
    private Integer readState;

}
