package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Created by hexiao on 2018/3/28.
 */
@Controller
@RequestMapping("/user/")  //requestmapping 相当于是一个访问过滤器，访问路径是user下的都进行拦截进入此类（此类定位与公共的方法，所以在前端）
public class UserController {
	@Autowired
	private IUserService iUserService;  //将service注入的 拿出来使用

	/**
	 * 用户登录
	 *
	 * @param username
	 * @param password
	 * @param session
	 */

	@RequestMapping(value = "login.do", method = RequestMethod.POST)  //限制只有post请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		//service-->mybatis-->dao
		ServerResponse<User> response = iUserService.login("username", "password");  // 调用ServerResponse的login方法进行登录
		if (response.isSuccess()) {
			 	session.setAttribute(Const.CURRENT_USER,response.getData());
		}
		return response;
	}

	@RequestMapping(value = "logout.do", method = RequestMethod.GET)  //限制只有GET请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	/**
	 * 用户注销接口
	 * 删除用户的session就行了
	 */
	public ServerResponse<User> logout(HttpSession session){
			session.removeAttribute(Const.CURRENT_USER);
			return ServerResponse.createBySuccess();
	}

	/**
	 * 用户注册功能
	 * 传入的是一个User对象，相当于是一个数据绑定，存在多个值  如；username ,password ,email,address
	 * 先检测用户的 email 和 用户名是否存在
	 */
	@RequestMapping(value = "register.do", method = RequestMethod.POST)  //限制只有GET请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<User> register(User user){
    	return iUserService.register(user);
	}

	/**
	 *判断用户输入的是 emai 还是 用户名
	 */
	@RequestMapping(value = "checkValid.do", method = RequestMethod.GET)  //限制只有GET请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<String> checkValid(String str,String type){
		return iUserService.checkValid(str,type);
	}

	/**
	 * 获取登录用户信息
	 *
	 */
	@RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)  //限制只有GET请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public  ServerResponse<User> getUserInfo(HttpSession session){

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user != null){
			return ServerResponse.createBySuccess("获取成功",user);
		}
		return  ServerResponse.createByErrorMessage("登录失效,获取用户信息失败！");
	}

	@RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)  //限制只有post请求能进来
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<String> forgetGetQus(String username){
		 return  iUserService.selectQus(username);  //传入用户名查找此用户设置的重置支付密码问题
	}

	@RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return  iUserService.checkAnswer(username,question,answer);
	}

	/*
* 未登录状态获取重置支付密码
 */
	@RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
	}

	/*
* 登录状态获取重置支付密码
*/
	@RequestMapping(value = "reset_password.do",method = RequestMethod.GET)
	@ResponseBody   //此注解表示 数据在返回的时候自动通过springmvc 的 jackson的插件将我们的返回值序列化为json
	public ServerResponse<String> forgetRestPassword(HttpSession session,String password,String passwordNew){
		User user = (User) session.getAttribute(Const.CURRENT_USER);//获取当前用户的session
		if (user == null){
			ServerResponse.createByErrorMessage("用户未登录！");
		}
       return  iUserService.resetPassword(password,passwordNew,user);
	}
}
