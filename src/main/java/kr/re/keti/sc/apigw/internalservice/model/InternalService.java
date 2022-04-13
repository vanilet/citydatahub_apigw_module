package kr.re.keti.sc.apigw.internalservice.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import kr.re.keti.sc.apigw.common.Constants;
import kr.re.keti.sc.apigw.routingrule.model.ServiceCommunicationMethod;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data public class InternalService {

    private String id;
    private String name;
    private String description;
    private ServiceCommunicationMethod communicationMethod;
    private String address;
    private String fallbackPath;
    private Boolean useYn;
    @JsonFormat(pattern = Constants.CONTENT_DATE_FORMAT, timezone = Constants.CONTENT_DATE_TIMEZONE)
    private Date creationTime;
    @JsonFormat(pattern = Constants.CONTENT_DATE_FORMAT, timezone = Constants.CONTENT_DATE_TIMEZONE)
    private Date modificationTime;
    private String creatorId;
    private String modifierId;
    
    public String resolveUri() {
    	if (address == null) return null;
    	return new StringBuilder(obtainAppliedCommunicationMethod(communicationMethod).getValue().toLowerCase()).append("://").append(address).toString();	
    }
    
    private ServiceCommunicationMethod obtainAppliedCommunicationMethod(ServiceCommunicationMethod communicationMethod) {
    	if (communicationMethod == null) {//Default: HTTP
    		return ServiceCommunicationMethod.Http;
    	}
    	else {
    		return communicationMethod;
    	}
    }
}