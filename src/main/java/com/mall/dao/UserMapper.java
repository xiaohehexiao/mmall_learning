package com.mall.dao;

import com.mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);  //检查用户是否存在

    User selectLogin(@Param("username") String username,@Param("password") String password);  //mybatis在传入多个参数时 需要使用注解让容器来区分使用那个参数值  在配置SQL的XML时，需要对应注解里面的命名

    int checkEmail(String email);

    String selectQus(String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
}