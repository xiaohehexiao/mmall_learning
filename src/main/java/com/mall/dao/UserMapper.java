package com.mall.dao;

import com.mall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.plugin.Interceptor;

public interface UserMapper {
    int checkUsername(String username);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkAnswer(String username,String password,String answer);  //密保问题检测

    User selectLogin(@Param("username")String username,@Param("password")String password); //登录校验

    String selectQus(String username);  //重置密码用户是否有密保问题

    int checkEmail(String email); //登录email检查

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew); //未登录重置登录密码

    int resetPassword(@Param("password")String password,@Param("userId")Integer userId); //登录后重置登录密码
}