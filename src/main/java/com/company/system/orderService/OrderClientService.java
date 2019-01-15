package com.company.system.orderService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.reflect.TypeToken;
import com.xinwei.nnl.common.domain.JsonRequest;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.nnl.common.util.JsonUtil;
import com.xinwei.nnl.common.util.NNLDateFormat;
import com.xinwei.orderDb.Const.OrderDbConst;
import com.xinwei.orderDb.domain.OrderFlow;
import com.xinwei.orderDb.domain.OrderMain;
import com.xinwei.orderDb.domain.OrderMainContext;
import com.xinwei.userOrder.domain.QueryUserOrderRequest;
import com.xinwei.userOrder.domain.UserOrder;

@Service("orderClientService")
public class OrderClientService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected int RESULT_Success = 0;

	@Value("${order.orderServiceUrl}")
	private String orderServiceUrl;

	@Value("${order.orderDbUrl}")
	private String orderDbUrl;

	@Value("${order.idServiceUrl}")
	private String orderIdUrl;

	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	private OrderClientServiceFeign orderClientServiceFeign;

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	// @RequestMapping(method = RequestMethod.POST, value =
	// "/{category}/{dbId}/{orderId}/createOrder")
	public ProcessResult createOrder(OrderMainContext orderMainContext) {
		if (StringUtils.isEmpty(orderMainContext.getOrderId())) {
			String orderid = this.getOrderId(orderMainContext.getCatetory(), orderMainContext.getOwnerKey());
			if (StringUtils.isEmpty(orderid)) {
				ProcessResult ret = new ProcessResult();
				ret.setRetCode(-1);
				ret.setRetMsg("orderid error");
				return ret;
			}
			orderMainContext.setOrderId(orderid);
		}

		ProcessResult result = orderClientServiceFeign.addOrderMain(orderMainContext.getCatetory(),
				orderMainContext.getDbId(orderMainContext.getOrderId()), orderMainContext.getOrderId(),
				orderMainContext);
		return result;
	}

	/**
	 * 向订单中心请求上下文数据
	 * 
	 * @param category
	 * @param dbId
	 * @param orderId
	 * @param keys
	 * @return
	 */
	public ProcessResult getContextData(String category, String orderId, List<String> keys) {

		ProcessResult result = null;
		String dbId = OrderMainContext.getDbId(orderId);

		JsonRequest jsonRequest = new JsonRequest();
		jsonRequest.setJsonString(JsonUtil.toJson(keys));

		result = orderClientServiceFeign.getContextData(category, dbId, orderId, jsonRequest);
		if (result.getRetCode() == RESULT_Success) {
			String jsonStr = (String) result.getResponseInfo();
			Map<String, String> retMap = JsonUtil.fromJson((String) result.getResponseInfo(),
					new TypeToken<Map<String, String>>() {
					}.getType());

			result.setResponseInfo(retMap);
		} else if (result.getRetCode() == OrderDbConst.RESULT_Error_DbError) {
			result.setRetCode(0);
			result.setResponseInfo(new HashMap<String, String>());
		}
		return result;

	}

	/**
	 * 从订单中心获取上下文
	 * 
	 * @param category
	 * @param dbId
	 * @param orderid
	 * @param keys
	 * @return
	 */
	protected Map<String, String> getOrderContextMap(String category, String dbId, String orderid, List<String> keys) {

		JsonRequest jsonRequest = new JsonRequest();
		jsonRequest.setJsonString(JsonUtil.toJson(keys));

		ProcessResult processResult = this.orderClientServiceFeign.getContextData(category, dbId, orderid, jsonRequest);
		if (processResult.getRetCode() == RESULT_Success) {
			Map<String, String> contextMaps = JsonUtil.fromJson((String) processResult.getResponseInfo(),
					new TypeToken<HashMap<String, String>>() {
					}.getType());

			return contextMaps;

		}
		return null;

		// {category}/{dbId}/{orderId}/getContextData

	}

	public ProcessResult putContextData(String category, String orderId, Map<String, String> maps) {

		logger.debug(category + ":" + orderId + ":" + maps.toString());
		ProcessResult result = null;
		String dbId = OrderMainContext.getDbId(orderId);
		JsonRequest jsonRequest = new JsonRequest();
		jsonRequest.setJsonString(JsonUtil.toJson(maps));
		OrderMainContext orderMainContext = new OrderMainContext();
		orderMainContext.setOrderId(orderId);
		orderMainContext.setContextDatas(maps);
		orderMainContext.setCatetory(category);
		result = orderClientServiceFeign.putContextData(category, dbId, orderId, orderMainContext);
		return result;

	}

	/**
	 * 用于步骤跳转
	 * 
	 * @param category
	 * @param dbId
	 * @param orderId
	 * @param stepId
	 * @param flowId
	 * @param currentStatus
	 * @param retCode
	 * @return
	 */
	public ProcessResult manualJumpToNextStep(String category, String dbId, String orderId, String stepId,
			String flowId, int currentStatus, String retCode) {
		OrderFlow orderFlow = new OrderFlow();

		orderFlow.setOrderId(orderId);
		orderFlow.setStepId(stepId);
		orderFlow.setCurrentStatus(currentStatus);
		orderFlow.setRetCode(retCode);
		ProcessResult result = null;
		result = orderClientServiceFeign.manualJumpToNextStep(category, dbId, orderId, orderFlow);
		return result;
	}

	/**
	 * 用于步骤跳转
	 * 
	 * @param category
	 * @param dbId
	 * @param orderId
	 * @param stepId
	 * @param flowId
	 * @param currentStatus
	 * @param retCode
	 * @return
	 */
	public ProcessResult manualJumpToNextStep(String category, String orderId, String retCode) {
		// 获取orderMain
		ProcessResult processResult = this.getOrder(category, orderId);
		if (processResult.getRetCode() != RESULT_Success) {
			return processResult;
		}
		String dbId = OrderMainContext.getDbId(orderId);
		OrderMain orderMain = (OrderMain) processResult.getResponseInfo();
		OrderFlow orderFlow = new OrderFlow();
		orderFlow.setOrderId(orderId);
		orderFlow.setFlowId(orderMain.getFlowId());
		orderFlow.setStepId(orderMain.getCurrentStep());
		orderFlow.setCurrentStatus(orderMain.getCurrentStatus());
		orderFlow.setRetCode(String.valueOf(retCode));
		processResult = orderClientServiceFeign.manualJumpToNextStep(category, dbId, orderId, orderFlow);
		return processResult;
	}

	/**
	 * 发布数据到用户中心
	 * 
	 * @param userDbWriteUrl
	 * @param userOrder
	 * @return
	 */
	public ProcessResult saveUserOrder(String userDbWriteUrl, UserOrder userOrder) {
		ProcessResult result = null;
		String userWebUrl = this.orderDbUrl;
		if (!StringUtils.isEmpty(userDbWriteUrl)) {
			userWebUrl = userDbWriteUrl;
		}		
		result = restTemplate.postForObject(
				userWebUrl + "/" + userOrder.getCategory() + "/" + userOrder.getUserId() + "/configUserOrder",
				userOrder, ProcessResult.class);
		return result;
	}

	/**
	 * 
	 * @param userDbWriteUrl
	 * @param userOrder
	 * @return
	 */
	public ProcessResult delUserOrder(String userDbWriteUrl, UserOrder userOrder) {

		ProcessResult result = null;
		result = restTemplate.postForObject(
				userDbWriteUrl + "/" + userOrder.getCategory() + "/" + userOrder.getUserId() + "/delUserOrder",
				userOrder, ProcessResult.class);
		return result;
	}

	/**
	 * 查询所有的订单按照orderID降序排序
	 * @param userDbWriteUrl
	 * @param category
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public ProcessResult queryAllUserOrderIdAsc(String userDbWriteUrl, String category, String userId, Date startTime,
			Date endTime, int numbers) {

		ProcessResult result = null;
		QueryUserOrderRequest queryUserOrderRequest = new QueryUserOrderRequest();
		queryUserOrderRequest.setCategory(category);
		queryUserOrderRequest.setUserId(userId);
		queryUserOrderRequest.setStartCreateTime(startTime);
		queryUserOrderRequest.setEndCreateTime(endTime);
		queryUserOrderRequest.setPageNum(1);
		queryUserOrderRequest.setPageSize(numbers);

		result = restTemplate.postForObject(userDbWriteUrl + "/" + category + "/" + userId + "/queryUserOrderidAsc",
				queryUserOrderRequest, ProcessResult.class);
		return result;
	}

	public ProcessResult updateUserOrderStatus(String userDbWriteUrl, UserOrder userOrder) {

		ProcessResult result = null;
		String userWebUrl = this.orderDbUrl;
		if (!StringUtils.isEmpty(userDbWriteUrl)) {
			userWebUrl = userDbWriteUrl;
		}	
		String url = userWebUrl + "/" + userOrder.getCategory() + "/" + userOrder.getUserId()
				+ "/updateUserOrderStatus";
		logger.info("updateUserOrderStatus url:" + url);
		logger.info("updateUserOrderStatus param:" + userOrder);
		result = restTemplate.postForObject(url, userOrder, ProcessResult.class);
		return result;
	}

	/**
	 * 获取订单数据
	 * 
	 * @param category
	 * @param orderId
	 * @return
	 */
	public ProcessResult getOrder(String category, String orderId) {
		String dbId = OrderMainContext.getDbId(orderId);
		String url = orderServiceUrl + "/" + category + "/" + dbId + "/" + orderId + "/getOrder";
		logger.info("getOrder url:" + url);
		ProcessResult processResult = restTemplate.postForObject(url, null, ProcessResult.class);
		if (processResult.getRetCode() == RESULT_Success) {
			processResult.setResponseInfo(JsonUtil.fromJson((String) processResult.getResponseInfo(), OrderMain.class));
		}
		return processResult;
	}

	public ProcessResult queryOneOrder(String userDbReadUrl, UserOrder userOrder) {

		ProcessResult result = null;
		String userWebUrl = this.orderDbUrl;
		if (!StringUtils.isEmpty(userDbReadUrl)) {
			userWebUrl = userDbReadUrl;
		}
		String url = userWebUrl + "/" + userOrder.getCategory() + "/" + userOrder.getUserId() + "/queryOneOrder";
		logger.info("queryOneOrder url:" + url);
		logger.info("queryOneOrder param:" + userOrder);
		result = restTemplate.postForObject(url, userOrder, ProcessResult.class);
		if (result.getRetCode() == 0) {
			UserOrder retUserOrder = JsonUtil.fromJson((String) result.getResponseInfo(), UserOrder.class);
			result.setResponseInfo(retUserOrder);
		}
		return result;
	}

	public ProcessResult queryUserOrderByPage(String userDbReadUrl, QueryUserOrderRequest queryUserOrderRequest) {

		ProcessResult result = null;
		String userWebUrl = this.orderDbUrl;
		if (!StringUtils.isEmpty(userDbReadUrl)) {
			userWebUrl = userDbReadUrl;
		}

		result = restTemplate.postForObject(userWebUrl + "/" + queryUserOrderRequest.getCategory() + "/"
				+ queryUserOrderRequest.getUserId() + "/queryUserOrder", queryUserOrderRequest, ProcessResult.class);
		if (result.getRetCode() == 0) {

		}
		return result;
	}

	/**
	 * 
	 * @param category
	 * @param ownerKey
	 * @return null -- if no orderId return;
	 */
	public String getOrderId(String category, String ownerKey) {
		String orderId = null;
		String pOweryKey = ownerKey;
		if (StringUtils.isEmpty(pOweryKey)) {
			pOweryKey = "111111";
		}
		JsonRequest jsonRequest = new JsonRequest();
		logger.debug("orderId:" + orderIdUrl + "/" + category + "/" + pOweryKey + "/createOrderId");

		ProcessResult processResult = restTemplate.postForObject(
				orderIdUrl + "/" + category + "/" + pOweryKey + "/createOrderId", jsonRequest, ProcessResult.class);
		if (processResult.getRetCode() == 0) {
			orderId = (String) processResult.getResponseInfo();
		}
		return orderId;

	}

	public ProcessResult startOrder(String category, String orderId) {

		String dbid = OrderMainContext.getDbId(orderId);

		return this.orderClientServiceFeign.startOrderMain(category, dbid, orderId);

	}

	/**
	 * 用于保存生成的需要付款信息的订单KEY
	 * 
	 * @return
	 */
	public String getOrderWillPayKey() {
		return "willPayment";
	}

	/**
	 * 用户保存付款成功后的订单中的key
	 * 
	 * @return
	 */
	public String getOrderSuccessPayKey() {
		return "succPayment";
	}

	/**
	 * 
	 * @return
	 */
	public String getOrderPayInfoKey(Date payTime) {
		return "infoPayment:" + NNLDateFormat.getStringByDate(payTime);
	}

}
