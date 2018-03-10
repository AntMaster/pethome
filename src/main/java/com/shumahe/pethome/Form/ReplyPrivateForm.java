package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;


@Data
public class ReplyPrivateForm {


    private Integer talkId;

    @NotBlank(message = "私信发起人必填")
    private String userIdFrom;

    @NotBlank(message = "私信接受人必填")
    private String userIdAccept;


    @NotBlank(message = "私信内容必填")
    private String content;


}
