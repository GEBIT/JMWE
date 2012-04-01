package com.innovalog.jmwe.plugins.functions;

import java.util.*;

import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This function copies the value from a field to another one.
 */
public class CopyValueFromOtherFieldPostFunction extends AbstractPreserveChangesPostFunction {
    private final WorkflowUtils workflowUtils;

    public CopyValueFromOtherFieldPostFunction(WorkflowUtils workflowUtils) {
        this.workflowUtils = workflowUtils;
    }

    /* (non-Javadoc)
    * @see com.googlecode.jsu.workflow.function.AbstractPreserveChangesPostFunction#executeFunction(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet, com.atlassian.jira.issue.util.IssueChangeHolder)
    */
    @Override
    protected void executeFunction(
            Map<String, Object> transientVars, Map<String, String> args,
            PropertySet ps, IssueChangeHolder holder
    ) throws WorkflowException {
        String fieldFromKey = (String) args.get("sourceField");
        String fieldToKey = (String) args.get("destinationField");
        String oldValue = (String) args.get("oldValue");
        boolean bOldValue = oldValue != null && oldValue.equalsIgnoreCase("yes");
        String appendValues = (String) args.get("appendValues");
        boolean bAppendValues = appendValues != null && appendValues.equalsIgnoreCase("yes");

        Field fieldFrom = (Field) workflowUtils.getFieldFromKey(fieldFromKey);
        Field fieldTo = (Field) workflowUtils.getFieldFromKey(fieldToKey);

        String fieldFromName = (fieldFrom != null) ? fieldFrom.getName() : fieldFromKey;
        String fieldToName = (fieldTo != null) ? fieldTo.getName() : fieldToKey;

        try {
            MutableIssue issue = getIssue(transientVars);
            Object sourceValue = null;
            
            if (bOldValue && issue.getModifiedFields().containsKey(fieldFromKey))
            {
	            ModifiedValue mv = (ModifiedValue) issue.getModifiedFields().get(fieldFromKey);
				if (mv != null)
					sourceValue = mv.getOldValue();
	            else
		            sourceValue = workflowUtils.getFieldValueFromIssue(issue, fieldFrom);
            }
            else
	            sourceValue = workflowUtils.getFieldValueFromIssue(issue, fieldFrom);

            if (bAppendValues) {
            	//get existing destination field value
            	Object destValue = workflowUtils.getFieldValueFromIssue(issue, fieldTo);
            	if (destValue == null)
            		destValue = Collections.EMPTY_LIST;
            	if (! (destValue instanceof Collection))
            	{
            		log.error(String.format("Field '%s' is not multi-valued",fieldToName));
            		return;
            	}
            	else
            	{
            		Collection newVal;
                    if (destValue instanceof Set)
                        newVal = new HashSet();
                    else
                        newVal = new ArrayList();
            		newVal.addAll((Collection)destValue);
            		if (sourceValue instanceof Collection)
            			newVal.addAll((Collection)sourceValue);
            		else
            			newVal.add(sourceValue);
            		sourceValue = newVal;
            	}
	            if (log.isDebugEnabled())
	                log.debug(
	                        String.format(
	                                "Adding value [%s] from issue %s field '%s' to '%s'",
	                                sourceValue, issue.getKey(),
	                                fieldFromName,
	                                fieldToName
	                        )
	                );
            }
            else
            {
	            if (log.isDebugEnabled()) {
	                log.debug(
	                        String.format(
	                                "Copying value [%s] from issue %s field '%s' to '%s'",
	                                sourceValue, issue.getKey(),
	                                fieldFromName,
	                                fieldToName
	                        )
	                );
	            }
            }

            // It set the value to field.
            workflowUtils.setFieldValue(issue, fieldToKey, sourceValue, holder);

            if (log.isDebugEnabled()) {
                log.debug("Value was successfully copied");
            }
        } catch (Exception e) {
            String message = String.format("Unable to copy value from '%s' to '%s'", fieldFromName, fieldToName);

            log.error(message, e);

            throw new WorkflowException(message);
        }
    }
}
