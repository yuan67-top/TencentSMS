/**
*ProjectName:TencentSMS
*Author:NieQiang 船长
*QQ:2548841623
*CreationeDate:2020年4月30日 下午11:01:12
**/
package com.nieqiang.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.nieqiang.webapp.vo.SDKvo;

@RestController
@RequestMapping("/session")
public class SessionController {
	SDKvo sdk = new SDKvo();
	ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
	
	@GetMapping("/getCode")
	@SuppressWarnings("unchecked")
	public String reg(String phone,HttpServletRequest request) {
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
		
		request.getSession().setAttribute(verificationCode, phone);
//		request.getSession().setAttribute("phone", phone);
		try {
			phone.equals(null);
			SmsSingleSender ssender = new SmsSingleSender(sdk.getAppid(), sdk.getAppkey());
			String[] params = {verificationCode,"5"};
			SmsSingleSenderResult result = ssender.sendWithParam("86", phone,sdk.getTemplateId(), params, "聂强个人项目", "", "");
			System.out.println(result);
		} catch (HTTPException e) {
			  // HTTP 响应码错误
			  e.printStackTrace();
		} catch (JSONException e) {
			  // JSON 解析错误
			e.printStackTrace();
		} catch (IOException e) {
			  // 网络 IO 错误
			e.printStackTrace();
		}
		
		//清除时根据时间戳判断是不是这个任务对应的验证码
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
        scheduledExecutorService.schedule(new Thread(()->{
            Map<String, String> codeMapS = (Map<String, String>) request.getSession().getAttribute("code");
            System.out.println(codeMapS);
            if(Objects.nonNull(codeMapS) && timeStamp.equals(codeMapS.get("timestamp"))){
                request.getSession().removeAttribute("verificationCode");
                System.out.println("清除session" + codeMapS);
            }
        }), 5, TimeUnit.SECONDS);
		
		return "已成功发送验证码到:【"+request.getSession().getAttribute(phone)+"】"
			+request.getSession().getAttribute(verificationCode)+">>>>"+request.getSession().getAttributeNames();
	}
	
	@GetMapping("/reg")
	public String login(HttpServletRequest request,String phone,String ver) {
		System.out.println(phone+">>>>"+ver);
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
			return "手机号不能为空";
		}
		String phoned = (String)request.getSession().getAttribute(phone);
		System.out.println(phoned+"<<<");
		if(!phone.equals(phoned)) {
			return "该手机号验证码发送失败，请重新申请发送！";
		}
		
		if(phoned.equals(phone)) {
			request.getSession().removeAttribute("verificationCode");
			request.getSession().removeAttribute("phone");
			return "验证码正确:"+ver+">>>手机号正确："+phone;
		}else {
			return "验证码错误和手机号不匹配！你输入的验证码为:"+ver+">>>你输入的手机号为："+phone+">>>>正确的应该是"+
				phoned+"手机号匹配验证码"+phoned;
		}
	}
}
