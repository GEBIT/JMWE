package com.innovalog.jmwe.plugins.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.innovalog.googlecode.jsu.util.FieldCollectionsUtils;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 *
 */
public class WorkflowSetFieldFromUserPropFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	public static final String VELOPARAM = "userPropKey";
	public static final String FUNCPARAM = "user.property.key";
	public static final String FIELD = "field";
	public static final String SELECTED_FIELD = "selectedField";
	public static final String FIELD_LIST = "fieldList";

    private final WorkflowUtils workflowUtils;
    private final FieldCollectionsUtils fieldCollectionsUtils;

    public WorkflowSetFieldFromUserPropFunction(FieldCollectionsUtils fieldCollectionsUtils, WorkflowUtils workflowUtils) {
        this.fieldCollectionsUtils = fieldCollectionsUtils;
        this.workflowUtils = workflowUtils;
    }

    @SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
		velocityParams.put(VELOPARAM,"");
		List<Field> fields = fieldCollectionsUtils.getAllEditableFields();
		velocityParams.put(FIELD_LIST, Collections.unmodifiableList(fields));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String commentString = (String) functionDescriptor.getArgs().get(FUNCPARAM);
        velocityParams.put(VELOPARAM, commentString);
		velocityParams.put(SELECTED_FIELD, workflowUtils.getFieldFromDescriptor(descriptor, FIELD));
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map velocityParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String sourceField = extractSingleParam(velocityParams, FIELD);
			params.put(FIELD, sourceField);
			String value1 = extractSingleParam(velocityParams, VELOPARAM);
			params.put(FUNCPARAM, value1);
		} catch(IllegalArgumentException e) {
		}
		return params;
	}
}
