package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.User;

/**
 * Created by hexiao on 2018/3/28.
 */
public interface IUserService {
	ServerResponse<User> login(String username, String password);

	ServerResponse<User> register(User user);

	ServerResponse<String> checkValid(String str,String type);

	ServerResponse<String> selectQus(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);
}
