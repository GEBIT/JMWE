package com.innovalog.jmwe.plugins.functions;

import static com.innovalog.googlecode.jsu.util.WorkflowFactoryUtils.getFieldByName;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.innovalog.googlecode.jsu.util.CommonPluginUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * This class defines the parameters available for Copy Value From Other Field
 * Post Function.
 * 
 * @author Gustavo Martin.
 */
public class WorkflowCopyValueFromOtherFieldPostFunctionPluginFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#
	 * getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List<Field> sourceFields = CommonPluginUtils.getCopyFromFields();
		List<Field> destinationFields = CommonPluginUtils.getCopyToFields();

		velocityParams.put("val-sourceFieldsList", Collections.unmodifiableList(sourceFields));
		velocityParams.put("val-destinationFieldsList", Collections.unmodifiableList(destinationFields));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#
	 * getVelocityParamsForEdit(java.util.Map,
	 * com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		getVelocityParamsForView(velocityParams, descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#
	 * getVelocityParamsForView(java.util.Map,
	 * com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		Field sourceFieldId = getFieldByName(descriptor, "sourceField");
		Field destinationField = getFieldByName(descriptor, "destinationField");

		velocityParams.put("val-sourceFieldSelected", sourceFieldId);
		velocityParams.put("val-destinationFieldSelected", destinationField);

		FunctionDescriptor conditionDescriptor = (FunctionDescriptor) descriptor;
		String oldValue = (String) conditionDescriptor.getArgs().get("oldValue");
		velocityParams.put("oldValue", oldValue);
		String appendValues = (String) conditionDescriptor.getArgs().get("appendValues");
		velocityParams.put("appendValues", appendValues);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams
	 * (java.util.Map)
	 */
	public Map<String, String> getDescriptorParams(Map conditionParams) {
		Map<String, String> params = new HashMap<String, String>();

		try {
		String sourceField = extractSingleParam(conditionParams, "sourceFieldsList");
		String destinationField = extractSingleParam(conditionParams, "destinationFieldsList");

		params.put("sourceField", sourceField);
		params.put("destinationField", destinationField);
		}
		catch (IllegalArgumentException iae) {
		}
		try {
			params.put("oldValue", extractSingleParam(conditionParams, "oldValue"));
		} catch (IllegalArgumentException iae) {
			params.put("oldValue", "no");
		}
		try {
			params.put("appendValues", extractSingleParam(conditionParams, "appendValues"));
		} catch (IllegalArgumentException iae) {
			params.put("appendValues", "no");
		}

		return params;
	}
}
