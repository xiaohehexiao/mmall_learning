package com.mall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by hexiao on 2018/4/2.
 */
public class TokenCache {
	private  static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenCache.class);  //生成一个静态Log对象

	// 本地缓存流程，创建本地缓存>>CacheBuilder.newBuilder()，初始化本地缓存容量>>> inittialCapacity(1000),设置最大容量>>>maximumSize(10000)
	//采用调用了方法,意思就是new后面的方法调用可以不管顺序   ，本地缓存超过最大容量之后采用  LRU算法  最少使用算法》》》根据在缓存里面使用量的多少来去除缓存数据，
	private  static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, String>() {
				//默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
				@Override
				public String load(String s) throws Exception {
					return "null";
				}
			});

	public  static  void  setKey(String key,String value){
		localCache.put(key,value);
	}

	public  static  String getKey(String key){
		String value = null ;
		try {
			value = localCache.get(key);
			if ("null".equals(value)){
				return  null;
			}
			return value;
		}catch (Exception e){
			logger.error("local cache get error",e);  //如果获取缓存失败，抛出错误
		}
		return  null;
	}

}
