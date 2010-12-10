package com.innovalog.googlecode.jsu.helpers;

/**
 * @author Gustavo Martin
 *
 * This class represents a Yes/No Type. Its values could be YES or NO.
 * It will be used in Workflow Condition, Validator, or Function.
 *
 */
public class YesNoType {
    public static final YesNoType YES = new YesNoType(1, "Yes");
    public static final YesNoType NO = new YesNoType(2, "No");

    private final int id;
    private final String value;

    /**
     * @param id
     * @param value
     */
    private YesNoType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof YesNoType))
            return false;
        YesNoType other = (YesNoType) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
