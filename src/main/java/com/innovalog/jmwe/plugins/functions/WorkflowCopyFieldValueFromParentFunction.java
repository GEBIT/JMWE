package com.innovalog.jmwe.plugins.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import org.apache.log4j.Logger;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;

public class WorkflowCopyFieldValueFromParentFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private Logger log = Logger.getLogger(WorkflowCopyFieldValueFromParentFunction.class);

	public static final String FIELD = "field";
	public static final String SELECTED_FIELD = "selectedField";
	public static final String FIELD_LIST = "fieldList";

    private final FieldCollectionsUtils fieldCollectionsUtils;
    private final WorkflowUtils workflowUtils;

    public WorkflowCopyFieldValueFromParentFunction(FieldCollectionsUtils fieldCollectionsUtils, WorkflowUtils workflowUtils) {
        this.fieldCollectionsUtils = fieldCollectionsUtils;
        this.workflowUtils = workflowUtils;
    }

    @SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		velocityParams.put(SELECTED_FIELD, workflowUtils.getFieldFromDescriptor(descriptor, FIELD));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
		List<Field> fields = fieldCollectionsUtils.getAllEditableFields();
		velocityParams.put(FIELD_LIST, Collections.unmodifiableList(fields));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(SELECTED_FIELD, workflowUtils.getFieldFromDescriptor(descriptor, FIELD));
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
