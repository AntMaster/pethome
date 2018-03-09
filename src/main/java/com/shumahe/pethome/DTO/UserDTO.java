package com.shumahe.pethome.DTO;

import lombok.Data;

@Data
public class UserDTO {

    private String openId;

    private String nickName;

    private String headImageUrl;

    public UserDTO(String openId, String nickName, String headImageUrl) {

        this.openId = openId;
        this.nickName = nickName;
        this.headImageUrl = headImageUrl;
    }

}
