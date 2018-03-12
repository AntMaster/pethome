package com.shumahe.pethome.Service.Impl;


import com.shumahe.pethome.DTO.UserPetAlbumDTO;
import com.shumahe.pethome.DTO.UserPetDTO;
import com.shumahe.pethome.Domain.*;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Enums.ShowStateEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserPetAlbumForm;
import com.shumahe.pethome.Form.UserPetForm;
import com.shumahe.pethome.Form.UserPetPhotoForm;
import com.shumahe.pethome.Repository.UserPetAlbumRepository;
import com.shumahe.pethome.Repository.UserPetPhotoRepository;
import com.shumahe.pethome.Repository.UserPetRepository;
import com.shumahe.pethome.Service.PetService;
import javafx.util.converter.DateStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PetServiceImpl implements PetService {


    @Autowired
    UserPetRepository userPetRepository;

    @Autowired
    UserPetAlbumRepository userPetAlbumRepository;


    @Autowired
    UserPetPhotoRepository userPetPhotoRepository;

    /**
     * 新增   宠卡
     *
     * @param petForm
     * @return
     */
    @Override
    public UserPet petAdd(UserPetForm petForm) {

        UserPet userPet = new UserPet();
        BeanUtils.copyProperties(petForm, userPet);
        userPet.setBirthday(new DateStringConverter().fromString(petForm.getBirthday()));
        UserPet save = userPetRepository.save(userPet);
        return save;
    }

    /**
     * 新增   宠卡  相册
     *
     * @param albumForm
     * @return
     */
    @Override
    public UserPetAlbum albumAdd(UserPetAlbumForm albumForm) {

        UserPet pet = userPetRepository.findOne(albumForm.getPetId());
        if (pet == null) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "宠物不存在");
        }


        UserPetAlbum petAlbum = new UserPetAlbum();
        BeanUtils.copyProperties(albumForm, petAlbum);

        UserPetAlbum save = userPetAlbumRepository.save(petAlbum);
        return save;

    }

    /**
     * 新增   宠卡  相册  相片
     *
     * @param photoForm
     * @return
     */
    @Override
    public UserPetPhoto photoAdd(UserPetPhotoForm photoForm) {


        UserPetAlbum album = userPetAlbumRepository.findOne(photoForm.getAlbumId());
        if (album == null) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "宠物相册不存在");
        }


        UserPetPhoto petPhoto = new UserPetPhoto();
        BeanUtils.copyProperties(photoForm, petPhoto);
        petPhoto.setPetId(album.getPetId());

        UserPetPhoto save = userPetPhotoRepository.save(petPhoto);
        return save;
    }

    /**
     * 宠卡 列表
     *
     * @param openId
     * @return
     */
    @Override
    public List<UserPet> petList(String openId) {

        List<UserPet> pets = userPetRepository.findByUserIdOrderByCreateTime(openId);
        if (pets == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        return pets;
    }

    /**
     * 宠物相册 列表
     *
     * @param petId
     * @return
     */
    @Override
    public UserPetDTO albumList(Integer petId) {

        UserPet pet = userPetRepository.findOne(petId);
        if (pet == null) {
            throw new PetHomeException(ResultEnum.FAILURE.getCode(), "宠物不存在");
        }

        List<UserPetAlbum> albums = userPetAlbumRepository.findByPetIdAndShowOrderByCreateTime(petId, ShowStateEnum.SHOW.getCode());
        if (albums == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

     /*   int[] albumIds = albums.stream().mapToInt(e -> e.getId()).toArray();
        List<UserPetPhoto> photos = userPetPhotoRepository.findByAlbumIdInAndShowOrderByCreateTime(albumIds, ShowStateEnum.SHOW.getCode());

        */

        UserPetDTO petDTO = new UserPetDTO();
        BeanUtils.copyProperties(pet, petDTO);
        petDTO.setAlbumCount(albums.size());

        List<UserPetAlbumDTO> albumDTOS = albums.stream().map(e -> {

            UserPetAlbumDTO albumDTO = new UserPetAlbumDTO();
            BeanUtils.copyProperties(e, albumDTO);
            return albumDTO;
        }).collect(Collectors.toList());

        petDTO.setPetAlbumDTOS(albumDTOS);

        return petDTO;
    }
}
