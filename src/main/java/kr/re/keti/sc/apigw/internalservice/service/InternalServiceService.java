package kr.re.keti.sc.apigw.internalservice.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.re.keti.sc.apigw.common.exception.BadRequestException;
import kr.re.keti.sc.apigw.common.exception.NotFoundException;
import kr.re.keti.sc.apigw.internalservice.mapper.InternalServiceMapper;
import kr.re.keti.sc.apigw.routingrule.service.PropagateService;

@Service
public class InternalServiceService {
	
	@Autowired
	private InternalServiceMapper serviceMapper;

	@Autowired
	private PropagateService propagateService;

	
	public List<kr.re.keti.sc.apigw.internalservice.model.InternalService> retrieveServices () {
		return serviceMapper.selectAllServices();
	}
	
	public kr.re.keti.sc.apigw.internalservice.model.InternalService retrieveService (String serviceId) {
		kr.re.keti.sc.apigw.internalservice.model.InternalService service = serviceMapper.selectService(serviceId);
		if (service == null) {
			throw new NotFoundException(String.format("Specified 'id': %s does not Exist", serviceId));
		}
		return service;
	}
	
	public void createService(kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		validateInternalService(service);
		try {
			serviceMapper.insertService(service);
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DuplicateKeyException) {
				throw new BadRequestException(String.format("Specified 'id': %s is duplicated", service.getId()));
			}
		}
		propagateService.propagateRoutes();
	}
	
	@Transactional
	public void updateService(kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		validateInternalService(service);
		if (retrieveService (service.getId()) == null) {
			throw new BadRequestException(String.format("Specified 'id': %s does not Exist", service.getId()));
		}
		serviceMapper.updateService(service);
		propagateService.propagateRoutes();
	}
	
	@Transactional
	public void deleteService(kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		if (retrieveService (service.getId()) == null) {
			throw new BadRequestException(String.format("Specified 'id': %s does not Exist", service.getId()));
		}
		
		try {
			serviceMapper.deleteService(service.getId());
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				throw new BadRequestException(String.format("Unable to delete Service id: %s. Routing Rules related to this Service exist.", service.getId()));
			}
		}
		propagateService.propagateRoutes();
	}
	
	private void validateInternalService(kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		validateMandatoryFields(service);
		validateIntegirtyOfFields(service);
	}
	
	private void validateMandatoryFields (kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		if (service.getId() == null) {
			throw new BadRequestException("'id' must be specified");
		}

		if (service.getAddress() == null) {
			throw new BadRequestException("'address' must be specified");
		}
	}
	
	private void validateIntegirtyOfFields (kr.re.keti.sc.apigw.internalservice.model.InternalService service) {
		try {
			URI.create(service.resolveUri());
	    } catch (Exception e) {
	    	throw new BadRequestException(String.format("Invalid URI format: %s", service.resolveUri()));
	    }
	}
}