package com.shumahe.pethome.Service;

import com.shumahe.pethome.DTO.UserPetDTO;
import com.shumahe.pethome.Domain.UserPet;
import com.shumahe.pethome.Domain.UserPetAlbum;
import com.shumahe.pethome.Domain.UserPetPhoto;
import com.shumahe.pethome.Form.UserPetAlbumForm;
import com.shumahe.pethome.Form.UserPetForm;
import com.shumahe.pethome.Form.UserPetPhotoForm;

import java.util.List;

public interface PetService {


    /**
     * 新增   宠卡
     * @param petForm
     * @return
     */
    UserPet petAdd(UserPetForm petForm);

    /**
     * 新增   宠卡  相册
     * @param albumForm
     * @return
     */
    UserPetAlbum albumAdd(UserPetAlbumForm albumForm);


    /**
     * 新增   宠卡  相册  相片
     * @param photoForm
     * @return
     */
    UserPetPhoto photoAdd(UserPetPhotoForm photoForm);


    /**
     * 宠卡 列表
     * @param openId
     * @return
     */
    List<UserPet> petList(String openId);


    /**
     * 宠物相册 列表
     * @param petId
     * @return
     */
    UserPetDTO albumList(Integer petId);


    /**
     * 用户宠卡列表
     */

    /**
     * 用户认证
     */

    /**
     *检查用户是否认证
     */

}
