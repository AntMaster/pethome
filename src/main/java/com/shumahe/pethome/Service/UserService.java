package com.shumahe.pethome.Service;

import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.UserApprove;
import com.shumahe.pethome.Form.UserApproveForm;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public interface UserService {



    UserDTO findMyInfo(String openId);


    UserApprove saveOrganization(UserApproveForm userApproveForm);

    /**
     * 查询 我的私信
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    List<Map<String, Object>> findMyPrivateTalk(String openId, PageRequest pageRequest);


    /**
     * 查询 我的互动
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    List<List<Map<String, String>>> findMyPublicTalk(String openId, PageRequest pageRequest);

}
