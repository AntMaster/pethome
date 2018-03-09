package com.shumahe.pethome.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Domain.UserDynamic;
import com.shumahe.pethome.Util.serializer.Date2LongSerializer;
import lombok.Data;


import java.util.Date;




//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PublishDTO {

    private Integer id;

    private String publisherId;

    private Integer classifyId;

    private String petName;

    private String petImage;

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date lostTime;

    private String lostLocation;

    private Float latitude;

    private Float longitude;

    private String ownerName;

    private String ownerContact;

    private UserBasic member;

    private CommentDTO commentDTO;

    private UserDTO userDTO;

}
