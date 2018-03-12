package com.shumahe.pethome.Controller;

import com.shumahe.pethome.DTO.UserPetDTO;
import com.shumahe.pethome.Domain.UserPet;
import com.shumahe.pethome.Domain.UserPetAlbum;
import com.shumahe.pethome.Domain.UserPetPhoto;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserPetAlbumForm;
import com.shumahe.pethome.Form.UserPetForm;
import com.shumahe.pethome.Form.UserPetPhotoForm;
import com.shumahe.pethome.Service.PetService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/pet")
public class PetController {


    @Autowired
    private PetService petService;


    /**
     * 新增   宠卡
     *
     * @param petForm
     * @param bindingResult
     * @return
     */


    @PutMapping("/{openId}")
    public ResultVO save(@PathVariable("openId") String openId, @Valid UserPetForm petForm, BindingResult bindingResult) {

        //验证表单数据是否正确
        if (bindingResult.hasErrors()) {
            log.error("【添加宠卡】参数不正确,petForm={}", petForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        petForm.setUserId(openId);
        UserPet pet = petService.petAdd(petForm);
        return ResultVOUtil.success(pet);
    }


    /**
     * 新增   宠卡  相册
     *
     * @param albumForm
     * @param bindingResult
     * @return
     */
    @PutMapping("/album/{openId}")
    public ResultVO albumAdd(@Valid UserPetAlbumForm albumForm, BindingResult bindingResult) {

        //验证表单数据是否正确
        if (bindingResult.hasErrors()) {
            log.error("【添加宠卡相册】参数不正确,albumForm={}", albumForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }


        UserPetAlbum album = petService.albumAdd(albumForm);
        return ResultVOUtil.success(album);
    }


    /**
     * 新增   宠卡  相册
     *
     * @param photoForm
     * @param bindingResult
     * @return
     */
    @PutMapping("/photo/{openId}")
    public ResultVO photoAdd(@Valid UserPetPhotoForm photoForm, BindingResult bindingResult) {

        //验证表单数据是否正确
        if (bindingResult.hasErrors()) {
            log.error("【添加照片】参数不正确,petForm={}", photoForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }


        UserPetPhoto photo = petService.photoAdd(photoForm);
        return ResultVOUtil.success(photo);
    }


    /**
     * 宠卡 列表
     *
     * @param openId
     * @return
     */
    @GetMapping("/{openId}")
    public ResultVO petList(@PathVariable("openId") String openId) {

        List<UserPet> userPets = petService.petList(openId);
        return ResultVOUtil.success(userPets);
    }


    /**
     * 宠卡相册 列表
     *
     * @param petId
     * @return
     */
    @GetMapping("/album/{openId}")
    public ResultVO petList(@RequestParam("petId") Integer petId) {


        UserPetDTO userPets = petService.albumList(petId);
        return ResultVOUtil.success(userPets);
    }


    /**
     * 用户认证
     */

    /**
     *检查用户是否认证
     */


}
