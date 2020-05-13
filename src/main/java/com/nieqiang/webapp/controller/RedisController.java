/**
 *@CreateTime:2020年5月12日上午9:48:46;
 *@User:Administrator;
 *@ProjectName:TencentSMS;
 *@ClassName:RedisController;
 *@Author:NieQiang;
 *@QQ:2548841623;
 *@Email:2548841623@qq.com;
 **/
package com.nieqiang.webapp.controller;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.nieqiang.webapp.vo.SDKvo;

@RestController
@RequestMapping("/redis")
public class RedisController {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	SDKvo sdk = new SDKvo();
	ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
	
	@GetMapping("/getCode")
	public String reg(String phone) {
		try {
			phone.equals(null);
			if(phone.trim()=="") {
				return "手机号不能为空";
			}
		}catch(Exception e) {
			return "手机号不能为空";
		}
		String beginIndex = String.valueOf((int)(Math.random()*1000));
		String endIndex = (String.valueOf(System.currentTimeMillis()))
				.substring(String.valueOf(System.currentTimeMillis()).length()-3);
		String verificationCode = beginIndex+endIndex;//生成验证码  前三位随机数+后三位毫秒值组成5到6位的验证码
		
		redisTemplate.opsForValue().set(verificationCode, phone, 300,TimeUnit.SECONDS);
		
		try {
			phone.equals(null);
			SmsSingleSender ssender = new SmsSingleSender(sdk.getAppid(), sdk.getAppkey());
			String[] params = {verificationCode,"5"};
			SmsSingleSenderResult result = ssender.sendWithParam("86", phone,sdk.getTemplateId(), params, "聂强个人项目", "", "");
			System.out.println(result);
		} catch (HTTPException e) {
			  // HTTP 响应码错误	e.printStackTrace();
			return "网络出现异常";
		} catch (JSONException e) {
			  // JSON 解析错误	e.printStackTrace();
			return "解析出现异常";
		} catch (IOException e) {
			  // 网络 IO 错误	e.printStackTrace();
			return "网络错误！";
		}
		return "已成功发送验证码到:【健：："+redisTemplate.keys(verificationCode)+"】值："
			+redisTemplate.opsForValue().get(verificationCode);
	}
	
	@GetMapping("/reg")
	public String login(String phone,String ver) {
		try {
			phone.equals(null);
			ver.equals(null);
			if(phone.trim()=="") {
				return "手机号不能为空";
			}
			if(ver.trim()=="") {
				return "验证码不能为空";
			}
		}catch(Exception e) {
			return "手机号或验证码不能为空";
		}

		/**
		 * 根据用户传过来的验证码作为键在redis中进行查询,查询结果为redisPhone
		 * 如果查到的结果不为空 并且 用户输入的电话号(phone)与redis中存的电话号(redisPhone)匹配则该用户输入的电话号和验证码为正确的
		 * 		此时删除redis中的键值
		 * 否则就是用户输入的手机号或验证码有误(验证码与手机号不对应)
		 */
		String redisPhone = redisTemplate.opsForValue().get(ver);//值：手机号
		if(redisPhone != null && phone.equals(redisPhone)) {
			redisTemplate.delete(ver);
			return "验证码正确:"+ver+">>>手机号正确："+redisPhone;
		}
//		return "验证码错误和手机号不匹配！";
		return "验证码错误和手机号不匹配！你输入的验证码为:"+ver+">>>你输入的手机号为："+phone+">>>>正确的应该是"+
		redisPhone;
	}
}
