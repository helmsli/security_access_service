package com.company.system.orderService;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.xinwei.nnl.common.domain.JsonRequest;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.orderDb.domain.OrderFlow;
import com.xinwei.orderDb.domain.OrderMainContext;

// 服务名称不能使用下划线
@FeignClient(value = "${coojisu.order-access}", fallback = OrderClientServiceFeignCallback.class)
public interface OrderClientServiceFeign {

	// @PathVariable注解必须使用value属性指定路径
	@PostMapping(value = "/orderGateway/{category}/{dbId}/{orderId}/createOrder")
	public ProcessResult addOrderMain(@PathVariable(value = "category") String category, @PathVariable(value = "dbId") String dbId, @PathVariable(value = "orderId") String orderId,
			@RequestBody OrderMainContext orderMain);

	@GetMapping(value = "/orderGateway/{category}/{dbId}/{orderId}/startOrder")
	public ProcessResult startOrderMain(@PathVariable(value = "category") String category, @PathVariable(value = "dbId") String dbId,
			@PathVariable(value = "orderId") String orderId);

	@PostMapping(value = "/orderGateway/{category}/{dbId}/{orderId}/putContextData")
	public ProcessResult putContextData(@PathVariable(value = "category") String category, @PathVariable(value = "dbId") String dbId,
			@PathVariable(value = "orderId") String orderId, @RequestBody OrderMainContext orderMainContext);

	@PostMapping(value = "/orderGateway/{category}/{dbId}/{orderId}/getContextData")
	public ProcessResult getContextData(@PathVariable(value = "category") String category, @PathVariable(value = "dbId") String dbId,
			@PathVariable(value = "orderId") String orderId, @RequestBody JsonRequest jsonRequest);

	@PostMapping(value = "/orderGateway/{category}/{dbId}/{orderId}/mJumpToNext")
	public ProcessResult manualJumpToNextStep(@PathVariable(value = "category") String category, @PathVariable(value = "dbId") String dbId,
			@PathVariable(value = "orderId") String orderId, @RequestBody OrderFlow orderFlow);

}
