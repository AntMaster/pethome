package com.shumahe.pethome.Service;

import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Form.PetSearchForm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SearchService {

    /**
     * 宠物搜索
     * @param petSearchForm
     */
    List<PublishDTO> petSearch(PetSearchForm petSearchForm);

}
