package com.userdetailsservice.app.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userdetailsservice.app.model.UserDetails;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// get(String key, Class<UserDetails> userdetailsClass)
	// making this generic type
	public <T> T get(String key, Class<T> userdetailsClass) {

		try {
			Object o = redisTemplate.opsForValue().get(key);
			if( o == null) {
				return null;
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(o.toString(), userdetailsClass);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void set(String key, Object o, Long ttl) {

		try {
			ObjectMapper mapper =  new ObjectMapper();
			String jsonValue = mapper.writeValueAsString(o);
			 redisTemplate.opsForValue().set(key, jsonValue,ttl,TimeUnit.SECONDS);
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
