/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.innovalog.googlecode.jsu.util.CommonPluginUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowFieldHasSingleValueValidator extends
		AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {

	public static final String FIELDKEY = "fieldKey";
	public static final String SELECTED_FIELD = "FHSVselectedField";
	public static final String FIELD_LIST = "fieldList";

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		this.getVelocityParamsForView(velocityParams, descriptor);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
		List<Field> fields = CommonPluginUtils.getAllEditableFields();
		velocityParams.put(FIELD_LIST, Collections.unmodifiableList(fields));
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		String strFieldKey = (String)args.get(FIELDKEY);
		velocityParams.put(SELECTED_FIELD, ManagerFactory.getFieldManager().getField(strFieldKey));
		
		String excludingSubtasks = (String) validatorDescriptor.getArgs().get("jira.excludingSubtasks");
		velocityParams.put("excludingSubtasks", excludingSubtasks);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map formParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String sourceField = extractSingleParam(formParams, "field");
			params.put(FIELDKEY, sourceField);
		} catch(IllegalArgumentException e) {
		}
		try{
			String excludingSubtasks = extractSingleParam(formParams, "excludingSubtasks");
			params.put("jira.excludingSubtasks", excludingSubtasks);
		}
		catch (IllegalArgumentException e)
		{
			params.put("jira.excludingSubtasks","no");
		}

		return params;
	}
}
