package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserApprove;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApproveRepository extends JpaRepository<UserApprove, Integer> {

    UserApprove findByUserId(String openId);

    Page<UserApprove> findByApproveState(Integer approveState, Pageable pageable);

}
