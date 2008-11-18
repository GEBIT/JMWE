/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowCommentRequiredValidator extends
		AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	@Override
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		getVelocityParamsForView(velocityParams, descriptor);
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	@Override
	protected void getVelocityParamsForInput(Map velocityParams) {
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	@Override
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		velocityParams.put("errorMessage", args.get("errorMessage"));
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map formParams) {
		Map<String, String> params = new HashMap<String, String>();
		String strErrorMessage = extractSingleParam(formParams, "errorMessage");
		
		if ("".equals(strErrorMessage)) {
			throw new IllegalArgumentException("An error message must be provided.");
		}
		
		params.put("errorMessage", strErrorMessage);
		
		return params;
	}

}
