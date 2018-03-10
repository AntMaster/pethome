package com.shumahe.pethome.Form;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class ReplyPublishForm {


    private Integer talkId;

    @NotBlank(message = "发表人ID必填")
    private String replierFrom;


    private String replierAccept;


    @NotBlank(message = "评论内容必填")
    private String content;


}
