
package com.hanlin.fadp.entity.model;

import java.io.Serializable;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.jdbc.JdbcValueHandler;
import org.w3c.dom.Element;

/**
 * Generic Entity - FieldType model class
 *
 */

@SuppressWarnings("serial")
public class ModelFieldType implements Serializable {

    public static final String module = ModelFieldType.class.getName();

    /** The type of the Field */
    protected String type = null;

    /** The java-type of the Field */
    protected String javaType = null;

    /** The JDBC value handler for this Field */
    protected JdbcValueHandler<?> jdbcValueHandler = null;

    /** The sql-type of the Field */
    protected String sqlType = null;

    /** The sql-type-alias of the Field, this is optional */
    protected String sqlTypeAlias = null;

    /** Default Constructor */
    public ModelFieldType() {}

    /** XML Constructor */
    public ModelFieldType(Element fieldTypeElement) {
        this.type = UtilXml.checkEmpty(fieldTypeElement.getAttribute("type")).intern();
        this.javaType = UtilXml.checkEmpty(fieldTypeElement.getAttribute("java-type")).intern();
        this.sqlType = UtilXml.checkEmpty(fieldTypeElement.getAttribute("sql-type")).intern();
        this.sqlTypeAlias = UtilXml.checkEmpty(fieldTypeElement.getAttribute("sql-type-alias")).intern();
        this.jdbcValueHandler = JdbcValueHandler.getInstance(this.javaType, this.sqlType);
    }

    /** The type of the Field */
    public String getType() {
        return this.type;
    }

    /** The java-type of the Field */
    public String getJavaType() {
        return this.javaType;
    }

    /** Returns the JDBC value handler for this field type */
    public JdbcValueHandler<?> getJdbcValueHandler() {
        return this.jdbcValueHandler;
    }

    /** The sql-type of the Field */
    public String getSqlType() {
        return this.sqlType;
    }

    /** The sql-type-alias of the Field */
    public String getSqlTypeAlias() {
        return this.sqlTypeAlias;
    }

    /** A simple function to derive the max length of a String created from the field value, based on the sql-type
     * @return max length of a String representing the Field value
     */
    public int stringLength() {
       String sqlTypeUpperCase = sqlType.toUpperCase();
        if (sqlTypeUpperCase.indexOf("VARCHAR") >= 0) {
            if (sqlTypeUpperCase.indexOf("(") > 0 && sqlTypeUpperCase.indexOf(")") > 0) {
                String length = sqlTypeUpperCase.substring(sqlTypeUpperCase.indexOf("(") + 1, sqlTypeUpperCase.indexOf(")"));

                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlTypeUpperCase.indexOf("CHAR") >= 0) {
            if (sqlTypeUpperCase.indexOf("(") > 0 && sqlTypeUpperCase.indexOf(")") > 0) {
                String length = sqlTypeUpperCase.substring(sqlTypeUpperCase.indexOf("(") + 1, sqlTypeUpperCase.indexOf(")"));

                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlTypeUpperCase.indexOf("TEXT") >= 0 || sqlTypeUpperCase.indexOf("LONG") >= 0 || sqlTypeUpperCase.indexOf("CLOB") >= 0) {
            return 5000;
        }
        return 20;
    }
}
