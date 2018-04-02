package com.mall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by hexiao on 2018/3/28.
 * Serializable 序列化接口
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//json数据为空的不返回，保证序列化json的时候，如果是nll的对象，key也会消失
public class ServerResponse<T> implements Serializable{
	private  int status;  //状态
	private  String msg;
	private T data;  //泛型对象

	private ServerResponse (int status){
		this.status=status;
	}

	private  ServerResponse (int status,T data){
		this.status=status;
		this.data=data;
	}

	private  ServerResponse(int status,String msg,T data){
		this.status=status;
		this.data=data;
		this.msg=msg;
	}

	private  ServerResponse(int status,String msg){
		this.status=status;
		this.msg=msg;
	}
	@JsonIgnore   //返回数据会讲当前方法加入到json数据里面，此注解表示不加入     使之不在json序列化之内
	public boolean isSuccess(){
		return  this.status == ResponseCode.SUCCESS.getCode();
	}

	public  String getMsg(){
		return  msg;
	}

	public  int getStatus(){
		return status;
	}

	public T getData(){
		return  data;
	}

	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}

	public static <T> ServerResponse<T> createBySuccessMessage(String msg){
		return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	public static <T> ServerResponse<T> createBySuccess(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	public static <T> ServerResponse<T> createBySuccessMessage(T data){
		return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}

	public static <T> ServerResponse<T> createBySuccess(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}

	public static  <T> ServerResponse<T> createByError(){
		return  new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
	}
	public static  <T> ServerResponse<T> createByError(String msg){
		return  new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
	}

	public static  <T> ServerResponse<T> createByErrorMessage(String errorMessage){
		return  new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}

	public static  <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
		return new ServerResponse<T>(errorCode,errorMessage);
	}
}

