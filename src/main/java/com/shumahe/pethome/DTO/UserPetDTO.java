package com.shumahe.pethome.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shumahe.pethome.Domain.UserPetAlbum;
import com.shumahe.pethome.Enums.ContraceptionStateEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserPetDTO {


    private String headImgUrl;


    private String nickName;


    private Integer classifyId;


    private Integer sex;


    private Date birthday;

    private Integer contraception;

    private String description;


    private String chipNo;


    private Date createTime;


    /**
     * 相册数量
     */
    private Integer albumCount;

    List<UserPetAlbumDTO> petAlbumDTOS;


}
