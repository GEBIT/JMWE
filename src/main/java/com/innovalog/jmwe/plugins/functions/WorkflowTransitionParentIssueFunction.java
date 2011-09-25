package com.innovalog.jmwe.plugins.functions;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WorkflowTransitionParentIssueFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private Logger log = Logger.getLogger(WorkflowTransitionParentIssueFunction.class);

	public static final String TRANSITION = "transition";

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		String transitionName = (String) ((FunctionDescriptor) descriptor).getArgs().get(TRANSITION);
		velocityParams.put(TRANSITION, transitionName);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(TRANSITION, (String) ((FunctionDescriptor) descriptor).getArgs().get(TRANSITION));
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map conditionParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String transition = extractSingleParam(conditionParams, TRANSITION);
			params.put(TRANSITION, transition);
		} catch(IllegalArgumentException e) {
			log.warn(e, e);
		}

		return params;
	}
}
