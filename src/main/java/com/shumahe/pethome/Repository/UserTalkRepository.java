package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.PublishTalk;
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
     * 查询当前私信所有来往记录
     *
     * @param publishId
     * @return
     */
    List<UserTalk> findByPublishIdIn(int publishId);


    /**
     * 查询私信条数
     *
     * @param publishId
     * @return
     */
    @Query(value = "select count(id) from UserTalk  where publishId = ? AND  showstate = 1",nativeQuery = true)
    int findPrivateMsgCount(Integer publishId);



    /**
     * 发布者查询所有私信消息
     * @param publishId
     * @param publisherId
     * @return
     */
    List<UserTalk> findByPublishIdAndPublisherIdOrderByLastModify(Integer publishId ,String publisherId);

    /**
     * 查询当前用户是否私信过该主题
     * @param publishId
     * @param publisherId
     * @return
     */
    List<UserTalk> findByPublishIdAndPublisherIdAndUserIdFromOrderByLastModify(Integer publishId ,String publisherId,String openId);











    /**
     * 根据openID查询我未读私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
/*
    @Query(value = "SELECT * FROM UserTalk " +
            "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 AND  ReadState = 0" +
            "ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findMyNotViewTalk(String openIdFrom, String openIdAccept);
*/


}
