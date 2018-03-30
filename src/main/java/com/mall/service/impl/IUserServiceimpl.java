package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hexiao on 2018/3/28.
 */
@Service("iUserService")  //注入到controller
public class IUserServiceimpl implements IUserService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);  //检查登录的用户名是否存在
        if( resultCount == 0){
			return  ServerResponse.createByErrorMessage("用户名不存在！");
		}

		//to do 密码登录MD5
		String md5password = MD5Util.MD5EncodeUtf8(password);  //进行MD5加密操作

		User user = userMapper.selectLogin(username,md5password);
		if (user == null){
			return ServerResponse.createByErrorMessage("密码错误！");
		}

		user.setPassword(StringUtils.EMPTY);  //查询成功后将用户的敏感信息重置为空
		return  ServerResponse.createBySuccess("登录成功",user);
	}

	/**
	 * 用户注册功能
	 * 1--检测用户名是否存在
	 * 2.检测email是否存在
	 *
	 * @param user
	 * @return
	 */
	public  ServerResponse<User> register(User user){
//		int resultCount = userMapper.checkUsername(user.getUsername());  //检查登录的用户名是否存在
//		if( resultCount > 0){
//			return  ServerResponse.createByErrorMessage("用户名已存在！");
//		}

		//
//		resultCount = userMapper.checkEmail(user.getEmail());
//		if( resultCount > 0){
//			return  ServerResponse.createByErrorMessage("邮箱已存在！");
//		}

		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);  //检测用户是否存在
		if (!validResponse.isSuccess()){
			return ServerResponse.createByErrorMessage("用户名已存在！");
		}

		validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
		if (!validResponse.isSuccess()){
			return ServerResponse.createByErrorMessage("邮箱已存在！");
		}


		user.setRole(Const.Role.ROLE_CUSTOMER);

		//MD5加密操作
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

		int  resultCount = userMapper.insert(user); //执行Insert操作
		if (resultCount == 0){
			return  ServerResponse.createByError("注册失败！");
		}
		return  ServerResponse.createBySuccessMessage("注册成功！");
	}

	public ServerResponse<String> checkValid(String str,String type){
     	if (StringUtils.isNoneBlank(type)){
			//开始校验
			if (Const.USERNAME.equals(type)){
				int resultCount = userMapper.checkUsername(str);  //检查登录的用户名 or email是否存在
				if( resultCount > 0){
					return  ServerResponse.createByErrorMessage("用户名已存在！");
				}
			}
			if (Const.EMAIL.equals(type)){
				int resultCount = userMapper.checkEmail(str);  //检查登录的用户名 or email是否存在
				if( resultCount > 0){
					return  ServerResponse.createByErrorMessage("邮箱已存在！");
				}
			}

		}else{
            return  ServerResponse.createByErrorMessage("参数错误！");
		}
		return ServerResponse.createBySuccessMessage("校验成功！");
	}

}
