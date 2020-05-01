package com.aspire.blog.inventory.web.rest.errors;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * ResourceNotFoundException is exception for no required data exists
 * 
 * @author aspire45
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ResourceNotFoundException extends AbstractThrowableProblem {

	private static final long serialVersionUID = 1L;

	private final String errorKey;

	private final String entityName;

	public ResourceNotFoundException(String defaultMessage, String entityName, String errorKey) {
		this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
	}

	public ResourceNotFoundException(URI type, String defaultMessage, String entityName, String errorKey) {
		super(type, defaultMessage, Status.OK, null, null, null, getAlertParameters(entityName, errorKey));
		this.entityName = entityName;
		this.errorKey = errorKey;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public String getEntityName() {
		return entityName;
	}

	/**
	 * create collection of parameters from entityName and errorKey
	 * 
	 * @param entityName
	 * @param errorKey
	 * @return returns collection of parameters and message
	 */
	private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("params", entityName);
		parameters.put("message", "error." + errorKey);
		return parameters;
	}
}
