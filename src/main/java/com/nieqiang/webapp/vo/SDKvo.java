/**
*ProjectName:TencentSMS
*Author:NieQiang 船长
*QQ:2548841623
*CreationeDate:2020年5月1日 下午12:09:40
**/
package com.nieqiang.webapp.vo;

public class SDKvo {
	// 短信应用 SDK AppID
	int appid = 1400; // SDK AppID 以1400开头
	// 短信应用 SDK AppKey
	String appkey = "";
	/**
	 * appid和appkey获取地址
	 * https://console.cloud.tencent.com/smsv2/app-manage/detail/1400362791
	 */
	
	// 短信模板 ID，需要在短信应用中申请    地址https://console.cloud.tencent.com/smsv2/csms-template
	int templateId = 8; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请
	
	// 签名     获取地址https://console.cloud.tencent.com/smsv2/csms-sign
	String smsSign = ""; // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是示例，真实的签名需要在短信控制台申请

	public int getAppid() {
		return appid;
	}
	public String getAppkey() {
		return appkey;
	}
	public int getTemplateId() {
		return templateId;
	}
	public String getSmsSign() {
		return smsSign;
	}
}