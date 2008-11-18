package com.innovalog.jmwe.plugins.functions;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class IncreaseFieldValueFunction extends AbstractJiraFunctionProvider {
	private Logger log = Logger.getLogger(IncreaseFieldValueFunction.class);
	private static final String FIELD = "field";

	public void execute(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		String fieldKey = (String) args.get(FIELD);
		Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);
		if (field == null) {
			log.warn("Error while executing function : field ["+fieldKey+"] not found");
			return;
		}

		// It set the value to field.
		try {
			MutableIssue issue = getIssue(transientVars);
			Object sourceValue = WorkflowUtils.getFieldValueFromIssue(issue, field);
			if (sourceValue != null) {
				log.debug("Current field value = ["+sourceValue+"], of class = ["+sourceValue.getClass().getName()+"]");
				if (sourceValue instanceof Double) {
					Double sourceValueDbl = (Double)sourceValue;
					Double newValueDbl = new Double(sourceValueDbl + 1);
					WorkflowUtils.setFieldValue(issue, field, newValueDbl);
				}
			} else {
				log.debug("Field value = null");
				Double newValueDbl = new Double(1);
				WorkflowUtils.setFieldValue(issue, field, newValueDbl);
			}
		} catch (Exception e) {
			log.warn("Error while executing function : "+e, e);
		}
	}

}
