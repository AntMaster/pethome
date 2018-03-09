package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class ReplyPublishForm {


    @NotBlank(message = "回复人ID必填")
    private String replierFrom;

    @NotNull(message = "被回复人ID必填(评论主题填空字符串)")
    private String replierAccept;


    @NotBlank(message = "评论内容必填")
    private String content;


    @NotNull(message = "互动主题主键必填")
    private int publishId;

    @NotBlank(message = "主题发布人主键必填")
    private String publisherId;

}
