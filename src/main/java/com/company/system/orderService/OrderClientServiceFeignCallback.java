package com.company.system.orderService;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import com.xinwei.nnl.common.domain.JsonRequest;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.orderDb.domain.OrderFlow;
import com.xinwei.orderDb.domain.OrderMainContext;

@Component
public class OrderClientServiceFeignCallback implements OrderClientServiceFeign {

	@Override
	public ProcessResult addOrderMain(String category, String dbId, String orderId, OrderMainContext orderMain) {
		// TODO Auto-generated method stub
		return ControllerUtils.getErrorResponse(-1, "call orderAccessFeign error");
		
	}

	@Override
	public ProcessResult startOrderMain(String category, String dbId, String orderId) {
		// TODO Auto-generated method stub
		return ControllerUtils.getErrorResponse(-1, "call orderAccessFeign error");
	}

	@Override
	public ProcessResult putContextData(String category, String dbId, String orderId,
			OrderMainContext orderMainContext) {
		// TODO Auto-generated method stub
		return ControllerUtils.getErrorResponse(-1, "call orderAccessFeign error");
	}

	@Override
	public ProcessResult getContextData(String category, String dbId, String orderId, JsonRequest jsonRequest) {
		// TODO Auto-generated method stub
		return ControllerUtils.getErrorResponse(-1, "call orderAccessFeign error");
	}
	@Override
	public ProcessResult manualJumpToNextStep(String category,String dbId,
			String orderId,OrderFlow orderFlow)
	{
		return ControllerUtils.getErrorResponse(-1, "call orderAccessFeign error");
	}
}
