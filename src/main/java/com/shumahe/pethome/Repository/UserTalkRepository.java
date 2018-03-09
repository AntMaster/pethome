package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserTalk;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserTalkRepository extends JpaRepository<UserTalk, Integer> {

    /**
     * 根据openID查询我的私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    @Query(value = "SELECT * FROM UserTalk " +
            "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 " +
            "ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findPrivateMyTalk(String openIdFrom, String openIdAccept);

    /**
     * 查询跟我私信的人
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    /*@Query(value = "SELECT  * FROM (SELECT DISTINCT UserIDFrom, PublishID AS ID FROM UserTalk " +
                  "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND  UserIDFrom <> ?3 AND ShowState = 1 " +
                    "GROUP BY UserIDFrom,PublishID)a ",nativeQuery = true)
    List<UserTalk> findTalkUser(String openIdFrom, String openIdAccept , String UserIDFrom );
    */

    /**
     * 查询当前私信所有来往记录
     * @param publishId
     * @return
     */
    List<UserTalk> findByPublishIdIn(int publishId);


    /**
     * 根据openID查询我未读私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    @Query(value = "SELECT * FROM UserTalk " +
            "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 AND  ReadState = 0" +
            "ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findMyNotViewTalk(String openIdFrom, String openIdAccept);




}
