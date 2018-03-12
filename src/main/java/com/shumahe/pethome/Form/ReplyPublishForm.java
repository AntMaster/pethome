package com.shumahe.pethome.Form;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class ReplyPublishForm {


    private Integer talkId;

    @NotBlank(message = "回复人replierFrom必填")
    private String replierFrom;


    private String replierAccept;


    @NotBlank(message = "评论内容content必填")
    private String content;


}
