package com.shumahe.pethome.Service;

import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.UserApprove;
import com.shumahe.pethome.Form.UserApproveForm;
import org.springframework.stereotype.Service;

@Service
public interface UserService {



    UserDTO findMyInfo(String openId);


    UserApprove saveOrganization(UserApproveForm userApproveForm);
}
