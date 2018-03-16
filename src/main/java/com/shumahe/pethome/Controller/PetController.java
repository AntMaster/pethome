package com.shumahe.pethome.Controller;


import com.shumahe.pethome.DTO.UserPetAlbumDTO;
import com.shumahe.pethome.DTO.UserPetDTO;
import com.shumahe.pethome.Domain.PetVariety;
import com.shumahe.pethome.Domain.UserPet;
import com.shumahe.pethome.Domain.UserPetAlbum;
import com.shumahe.pethome.Domain.UserPetPhoto;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserPetAlbumForm;
import com.shumahe.pethome.Form.UserPetForm;
import com.shumahe.pethome.Form.UserPetPhotoForm;
import com.shumahe.pethome.Repository.PetVarietyRepository;
import com.shumahe.pethome.Repository.UserPetAlbumRepository;
import com.shumahe.pethome.Repository.UserPetPhotoRepository;
import com.shumahe.pethome.Service.PetService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

@RestController
@Slf4j
@RequestMapping("/pet")
public class PetController {


    @Autowired
    private PetService petService;

    @Autowired
    private UserPetAlbumRepository userPetAlbumRepository;

    @Autowired
    private UserPetPhotoRepository userPetPhotoRepository;


    @Autowired
    private PetVarietyRepository petVarietyRepository;


    /**
     * 新增宠卡
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
     * 新增相册
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
     * 修改相册
     */
    @PostMapping("/album/{openId}")
    public ResultVO albumModify(@RequestParam("albumId") Integer albumId,
                                @RequestParam(value = "name", defaultValue = "EMPTY") String name,
                                @RequestParam(value = "description", defaultValue = "EMPTY") String description) {


        if (name.equals("EMPTY") && description.equals("EMPTY")) {

            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "相册名称和相册描述不能同时为空");
        }

        UserPetAlbum album = userPetAlbumRepository.findOne(albumId);
        if (album == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        if (!name.equals("EMPTY"))
            album.setName(name);

        if (!description.equals("EMPTY"))
            album.setDescription(description);

        UserPetAlbum save = userPetAlbumRepository.save(album);
        return ResultVOUtil.success(save);
    }


    /**
     * 删除所有相册
     */
//    @DeleteMapping("/album/{openId}")
//    public ResultVO albumDeleteAll(@RequestParam("albumId") Integer albumId,
//                                @RequestParam(value = "name", defaultValue = "EMPTY") String name,
//                                @RequestParam(value = "description", defaultValue = "EMPTY") String description) {
//
//
//        if (name.equals("EMPTY") && description.equals("EMPTY")) {
//
//            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "相册名称和相册描述不能同时为空");
//        }
//
//        UserPetAlbum album = userPetAlbumRepository.findOne(albumId);
//        if (album == null) {
//            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
//        }
//
//        if (!name.equals("EMPTY"))
//            album.setName(name);
//
//        if (!description.equals("EMPTY"))
//            album.setDescription(description);
//
//        UserPetAlbum save = userPetAlbumRepository.save(album);
//        return ResultVOUtil.success(save);
//    }


    /**
     * 删除相册
     */
    @DeleteMapping("/album/{openId}")
    public ResultVO albumDelete(@RequestParam("albumId") Integer albumId) {

        boolean delete = petService.albumDelete(albumId);
        return ResultVOUtil.success(delete);
    }

    /**
     * 新增相片
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
     * 修改相册
     */
    @PostMapping("/photo/{openId}")
    public ResultVO photoModify(@RequestParam("photoId") Integer albumId,
                                @RequestParam(value = "name", defaultValue = "EMPTY") String name,
                                @RequestParam(value = "description", defaultValue = "EMPTY") String description) {


        if (name.equals("EMPTY") && description.equals("EMPTY")) {

            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "相册名称和相册描述不能同时为空");
        }

        UserPetPhoto photo = userPetPhotoRepository.findOne(albumId);
        if (photo == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        if (!name.equals("EMPTY"))
            photo.setName(name);

        if (!description.equals("EMPTY"))
            photo.setDescription(description);

        UserPetPhoto save = userPetPhotoRepository.save(photo);
        return ResultVOUtil.success(save);
    }

    /**
     * 删除相片
     */
    @DeleteMapping(value = "/photo/{openId}")
    public ResultVO photoDelete(@RequestBody Map map) {

        List<Integer> photoIds = (ArrayList<Integer>) map.get("photoId");
        if (photoIds.isEmpty()) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }
        boolean delete = petService.photoDelete(photoIds);
        return ResultVOUtil.success(123);
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
    public ResultVO albumList(@RequestParam("petId") Integer petId) {


        UserPetDTO userPets = petService.albumList(petId);
        return ResultVOUtil.success(userPets);
    }


    /**
     * 宠卡相册相片 列表
     *
     * @param albumId
     * @return
     */
    @GetMapping("/photo/{openId}")
    public ResultVO photoList(@RequestParam("albumId") Integer albumId) {

        UserPetAlbumDTO photoDTO = petService.photoList(albumId);
        return ResultVOUtil.success(photoDTO);
    }

    /**
     * 获取宠物品种
     *
     * @return
     */
    @GetMapping("/variety")
    public ResultVO petVariety() {

        List<PetVariety> petVarieties = petVarietyRepository.findAll();
        if (petVarieties.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        Map<Integer, List<PetVariety>> varietyMap = petVarieties.stream().collect(Collectors.groupingBy(variety -> variety.getClassifyId()));

        return ResultVOUtil.success(varietyMap);

    }




}
