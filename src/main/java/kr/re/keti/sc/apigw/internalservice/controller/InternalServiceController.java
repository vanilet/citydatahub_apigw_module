package kr.re.keti.sc.apigw.internalservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.re.keti.sc.apigw.common.ManagementInterfaceSecurity;
import kr.re.keti.sc.apigw.internalservice.model.InternalService;
import kr.re.keti.sc.apigw.internalservice.service.InternalServiceService;
import kr.re.keti.sc.apigw.protocol.http.AccessTokenHttpUtils;
import kr.re.keti.sc.apigw.protocol.http.HttpConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RestControllerEndpoint(id= "services")
public class InternalServiceController {
	
	@Autowired
	ManagementInterfaceSecurity managementInterfaceSecurity;

	@Autowired
	private InternalServiceService service;

	/* 전체 서비스 조회 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	private List <InternalService> selectServices(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue)
			throws Exception {
		log.debug("'selectServices' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		return service.retrieveServices();
	}
	
	/* 개별 서비스 조회 */
	@RequestMapping(value = "/{serviceId}", method = RequestMethod.GET)
	@ResponseBody
	private InternalService selectService(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@PathVariable(HttpConstants.Path.SERVICE_ID) String serviceId)
			throws Exception {
		log.debug("'selectService' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);		
		return service.retrieveService(serviceId);
	}
	
	/* 개별 서비스 생성 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	private void createRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue, 
			@RequestBody InternalService service)
			throws Exception {
		log.debug("'createService' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		this.service.createService(service);
	}
	
	/* 개별 서비스 갱신 */
	@RequestMapping(value = "/{serviceId}", method = RequestMethod.PUT)
	@ResponseBody
	private void updateRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@PathVariable(HttpConstants.Path.SERVICE_ID) String serviceId,
			@RequestBody InternalService service)
			throws Exception {
		log.debug("'updateService' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		service.setId(serviceId);
		this.service.updateService(service);
	}
	
	/* 개별 서비스 삭제 */
	@RequestMapping(value = "/{serviceId}", method = RequestMethod.DELETE)
	@ResponseBody
	private void deleteRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@PathVariable(HttpConstants.Path.SERVICE_ID) String serviceId)
			throws Exception {
		log.debug("'deleteService' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		InternalService service = new InternalService();
		service.setId(serviceId);
		this.service.deleteService(service);
	}
}
