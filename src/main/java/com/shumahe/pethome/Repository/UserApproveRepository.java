package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserApprove;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApproveRepository extends JpaRepository<UserApprove, Integer> {

    UserApprove findByUserId(String openId);
}
