package com.innovalog.jmwe.plugins.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.innovalog.googlecode.jsu.util.CommonPluginUtils;
import com.innovalog.googlecode.jsu.util.WorkflowFactoryUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;

public class WorkflowIncreaseFieldValueFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private Logger log = Logger.getLogger(WorkflowIncreaseFieldValueFunction.class);

	public static final String FIELD = "field";
	public static final String SELECTED_FIELD = "selectedField";
	public static final String FIELD_LIST = "fieldList";

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		velocityParams.put(SELECTED_FIELD, WorkflowFactoryUtils.getFieldByName(descriptor, FIELD));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
		List<Field> fields = CommonPluginUtils.getAllEditableFields();
		velocityParams.put(FIELD_LIST, Collections.unmodifiableList(fields));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(SELECTED_FIELD, WorkflowFactoryUtils.getFieldByName(descriptor, FIELD));
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map conditionParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String sourceField = extractSingleParam(conditionParams, FIELD);
			params.put(FIELD, sourceField);
		} catch(IllegalArgumentException e) {
			log.warn(e, e);
		}

		return params;
	}
}
