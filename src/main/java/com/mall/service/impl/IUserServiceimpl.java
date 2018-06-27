package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
	@Override
	/*
	* 用户注册功能
	 */
	public  ServerResponse<User> register(User user){
		int resultCount = userMapper.checkUsername(user.getUsername());  //检查登录的用户名是否存在
		if( resultCount > 0){
			return  ServerResponse.createByErrorMessage("用户名已存在！");
		}


		resultCount = userMapper.checkEmail(user.getEmail());
		if( resultCount > 0){
			return  ServerResponse.createByErrorMessage("邮箱已存在！");
		}

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

		  resultCount = userMapper.insert(user); //执行Insert操作
		if (resultCount == 0){
			return  ServerResponse.createByError("注册失败！");
		}
		return  ServerResponse.createBySuccessMessage("注册成功！");
	}
	@Override
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
	@Override
	public ServerResponse<String> selectQus(String username){
		ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(!validResponse.isSuccess()){
   			ServerResponse.createByErrorMessage("此用户名不存在！");
		}
       String SQues  = userMapper.selectQus(username);
		if (StringUtils.isNotBlank(SQues)){
			return  ServerResponse.createBySuccessMessage(SQues);
		}
		return ServerResponse.createByErrorMessage("此用户没有设置过支付密码问题！");
	}

	public ServerResponse<String> checkAnswer(String username,String question,String answer){
		int checkCont = userMapper.checkAnswer(username,question,answer);
		if(checkCont > 0){
			//当前用户的 重置问题  答案都正确
			String forgetToken = UUID.randomUUID().toString();  //生成一个字符串ID = bc95f546-2e8d-4557-8374-d6b69322a2c3
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);//获取Tokencache里面的常量
			return ServerResponse.createBySuccess(forgetToken);
		}
		return  ServerResponse.createByErrorMessage("密保问题错误！");
	}
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		//必输项判断
		if (StringUtils.isBlank(forgetToken)){
             ServerResponse.createByErrorMessage("参数错误，token为空");
		}
		//用户名检测
		ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
		if(!validResponse.isSuccess()){
			ServerResponse.createByErrorMessage("此用户名不存在！");
		}
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		if (StringUtils.isBlank(token)){
             ServerResponse.createByErrorMessage("token失效或者过期");
		}
		if (StringUtils.equals(forgetToken,token)){
            String  md5Password = MD5Util.MD5EncodeUtf8(passwordNew); //登录密码转加密
			int rowCount = userMapper.updatePasswordByUsername(username,md5Password);  //新密码表数据存放MD5密文
			if (rowCount > 0){
				ServerResponse.createBySuccessMessage("密码重置成功！");
			}else {
				return  ServerResponse.createByError("token错误，请重新获取重置密码的token");
			}
		}
		return  ServerResponse.createByErrorMessage("密码重置失败");
	}

	public  ServerResponse<String> resetPassword(String password,String passwordNew,User user){
		     //防止横向越权，要校验一下这个用户的旧密码，一定是要是当前用户的，因为查询语句是一个count(1),如果不指定id,那么结果就是true count > 0
           int resultCount = userMapper.resetPassword(MD5Util.MD5EncodeUtf8(password),user.getId());
		if (resultCount == 0){
           return  ServerResponse.createByErrorMessage("原始 密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));//新密码加密，之后往usr对象set值
		 int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if (updateCount > 0){
			return  ServerResponse.createBySuccessMessage("重置成功");
		}
		return  ServerResponse.createByErrorMessage("重置失败");
	}
}
