package com.mall.common;

/**
 * Created by hexiao on 2018/3/28.
 * 响应编码的枚举类 定义的是一些状态信息
 */
public enum ResponseCode {
	/**
	 * SUCCESS 成功
	 * ERROR 失败
	 * NEED_LOGIN 是否需要登录
	 *
	 */
	SUCCESS(0,"SUCCESS"),
	ERROR(1,"ERROR"),
	NEED_LOGIN(10,"NEED_LOGIN"),
	ILLEGAL_ARGUMENT(2,"ILLRGAL_ARGUMENT");

	private  final  int code;
	private  final  String desc;

	ResponseCode(int code,String desc){
		this.code=code;
		this.desc=desc;
	}

	public String getDesc(){
		return  desc;
	}

	public int getCode(){
		return code;
	}
}
