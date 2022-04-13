package kr.re.keti.sc.apigw.internalservice.mapper;

import kr.re.keti.sc.apigw.internalservice.model.InternalService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalServiceMapper {
	
	List<InternalService> selectAllServices ();

	InternalService selectService (String serviceId);
	
	void insertService (InternalService service);
	
	void updateService (InternalService service);
	
	void deleteService (String serviceId);
}