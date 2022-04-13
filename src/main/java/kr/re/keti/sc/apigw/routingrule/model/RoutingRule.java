package kr.re.keti.sc.apigw.routingrule.model;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import kr.re.keti.sc.apigw.common.Constants;
import kr.re.keti.sc.apigw.internalservice.model.InternalService;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data public class RoutingRule {
	
	private String id;
	private String name;
	private String description;
	private Boolean useYn;
	private Integer order;
	@JsonFormat(pattern = Constants.CONTENT_DATE_FORMAT, timezone = Constants.CONTENT_DATE_TIMEZONE)
	private Date creationTime;
	@JsonFormat(pattern = Constants.CONTENT_DATE_FORMAT, timezone = Constants.CONTENT_DATE_TIMEZONE)
	private Date modificationTime;
	private String creatorId;
	private String modifierId;
	@JsonDeserialize(using = HttpMethodDeserializer.class)
	private HttpMethod method;
	private Boolean authorizationYn;
	private String targetServiceId;
	private ArrayList<String> authorizationRoleIds;
	private String newPath;
	
	@JsonIgnore
	private InternalService targetService;
	private ArrayList<RoutingRuleDetail> routingRuleDetails;
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data public static class RoutingRuleDetail {
		private Integer sequence;
		private RoutingRuleDetailType type;
		private String attributeName;
		private RoutingRuleDetailAttributeOperation attributeOperation;
		private String attributeValue;
		private LogicalOperation logicalOperation;
	}
}