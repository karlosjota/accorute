/*
 * NamedValue.java
 *
 * Created on 19 December 2004, 08:58
 */

package su.msu.cs.lvk.accorute.http.model;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * @author rogan
 */
public class NamedValue implements Serializable {

    private String name;
    private String value;

    private static Logger logger = Logger.getLogger(NamedValue.class.getName());

    /**
     * Creates a new instance of NamedValue
     * @param name
     * @param value
     */
    
    public NamedValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public NamedValue(NamedValue namedValue) {
        this(namedValue.getName(), namedValue.getValue());
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return name + ": " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedValue that = (NamedValue) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public static NamedValue[] splitNamedValues(String source, String pairSeparator, String nvSeparator) {
        try {
            if (source == null) return new NamedValue[0];
            String[] pairs = source.split(pairSeparator);
            logger.debug("Split \"" + source + "\" into " + pairs.length);
            NamedValue[] values = new NamedValue[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                String[] nv = pairs[i].split(nvSeparator, 2);
                if (nv.length == 2) {
                    values[i] = new NamedValue(nv[0], nv[1]);
                } else if (nv.length == 1) {
                    values[i] = new NamedValue(nv[0], "");
                } else {
                    values[i] = null;
                }
            }
            return values;
        } catch (ArrayIndexOutOfBoundsException aioob) {
            logger.error("Error splitting \"" + source + "\" using '" + pairSeparator + "' and '" + nvSeparator + "'");
        }
        return new NamedValue[0];
    }

    @SuppressWarnings("unchecked")
    public static NamedValue[] find(String name, NamedValue[] headers) {
        if (headers == null || headers.length == 0)
            return headers;
        List found = new LinkedList();
        for (int i = 0; i < headers.length; i++)
            if (headers[i].getName().equalsIgnoreCase(name))
                found.add(headers[i]);
        return (NamedValue[]) found.toArray(new NamedValue[found.size()]);
    }

    public static NamedValue findOne(String name, NamedValue[] nv) {
        NamedValue[] found = find(name, nv);
        if (found == null || found.length == 0) return null;
        if (found.length == 1) return found[0];
        throw new IllegalStateException("More than one result for '" + name + "'");
    }

    @SuppressWarnings("unchecked")
    public static NamedValue[] delete(String name, NamedValue[] headers) {
        if (headers == null || headers.length == 0)
            return headers;
        List left = new LinkedList();
        for (int i = 0; i < headers.length; i++)
            if (!headers[i].getName().equalsIgnoreCase(name))
                left.add(headers[i]);
        return (NamedValue[]) left.toArray(new NamedValue[left.size()]);
    }
}
