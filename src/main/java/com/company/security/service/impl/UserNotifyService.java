package com.company.security.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.company.security.domain.SecurityUser;
import com.company.system.orderService.ControllerUtils;
import com.company.system.orderService.OrderClientService;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.nnl.common.util.JsonUtil;
import com.xinwei.orderDb.domain.OrderMainContext;

@Service("userNotifyService")
public class UserNotifyService {


	public static final String key_userRegisterNoty ="userRegNotify";
	public static final String key_userId ="userId";
	public static final String key_code ="inviteNo";
	
	private String category_modify_user = "category_modifyUser";
	@Autowired
	private OrderClientService orderClientService ;
	
	 private Logger logger = LoggerFactory.getLogger(getClass());
	
	 
	 @Async
	public void nodifyModifyUserAsync(SecurityUser securityUser)
	{
		 for(int i=0;i<3;i++)
		 {
			 ProcessResult ret = notifyModifyUser(securityUser);
			 if(ret.getRetCode()==0)
			 {
				 return ;
			 }
			 try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	}
	
	 @Async
	public void nodifyRegisterUserAsync(long userId,String inviteNo)
		{
			 for(int i=0;i<3;i++)
			 {
				 ProcessResult ret = notifyRegisterUser(userId,inviteNo);
				 if(ret.getRetCode()==0)
				 {
					 return ;
				 }
				 try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
	protected ProcessResult notifyModifyUser(SecurityUser securityUser)
	{
		
		String userId = Long.toString(securityUser.getUserId());
		String orderId= this.orderClientService.getOrderId(category_modify_user, userId);
		if(orderId ==null)
		{
			logger.error("get orderId error:"+ securityUser.toString());
			return ControllerUtils.getErrorResponse(-1, "get orderId error");
		}
		OrderMainContext orderMainContext = new OrderMainContext();
		orderMainContext.setCatetory(category_modify_user);
		orderMainContext.setOwnerKey(userId);
		orderMainContext.setOrderId(orderId);
		Map<String,String>contextDatas = new HashMap<String,String>();
		contextDatas.put("userId", userId);
		contextDatas.put("notifyFlag", "modifyUser");
		orderMainContext.setContextDatas(contextDatas);
		ProcessResult ret = this.orderClientService.createOrder(orderMainContext);
		if(ret.getRetCode()!=0)
		{
			return ret;
		}
		ret = this.orderClientService.startOrder(category_modify_user, orderId);
		return ret;
	}
	
	protected ProcessResult notifyRegisterUser(long lUserId,String inviteNo)
	{
		
		String userId = Long.toString(lUserId);
		String orderId= this.orderClientService.getOrderId(category_modify_user, userId);
		if(orderId ==null)
		{
			logger.error("get orderId error:"+ userId);
			return ControllerUtils.getErrorResponse(-1, "get orderId error");
		}
		OrderMainContext orderMainContext = new OrderMainContext();
		orderMainContext.setCatetory(category_modify_user);
		orderMainContext.setOwnerKey(userId);
		orderMainContext.setOrderId(orderId);
		Map<String,String>contextDatas = new HashMap<String,String>();
		
		Map<String,String> notifyMap = new HashMap<String,String>();
		notifyMap.put(this.key_code, inviteNo);
		notifyMap.put(this.key_userId, userId);
		contextDatas.put(this.key_userRegisterNoty, JsonUtil.toJson(notifyMap));
		contextDatas.put("notifyFlag", "register");
		orderMainContext.setContextDatas(contextDatas);
		ProcessResult ret = this.orderClientService.createOrder(orderMainContext);
		if(ret.getRetCode()!=0)
		{
			return ret;
		}
		ret = this.orderClientService.startOrder(category_modify_user, orderId);
		return ret;
	}
}
