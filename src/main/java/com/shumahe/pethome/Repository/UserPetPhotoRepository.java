package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserPetAlbum;
import com.shumahe.pethome.Domain.UserPetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPetPhotoRepository  extends JpaRepository<UserPetPhoto,Integer>{


    List<UserPetPhoto> findByAlbumIdInAndShowOrderByCreateTime(int[] albumId,Integer show);


}
