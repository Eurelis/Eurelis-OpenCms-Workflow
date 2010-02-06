//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1.6-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.06 at 06:35:28 PM CEST 
//


package com.eurelis.opencms.workflows.jaxb.workflowconfig;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for workflowParameterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="workflowParameterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="String"/>
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="long"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="byte"/>
 *     &lt;enumeration value="short"/>
 *     &lt;enumeration value="char"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "workflowParameterType")
@XmlEnum
public enum WorkflowParameterType {

    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("long")
    LONG("long"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("boolean")
    BOOLEAN("boolean"),
    @XmlEnumValue("byte")
    BYTE("byte"),
    @XmlEnumValue("short")
    SHORT("short"),
    @XmlEnumValue("char")
    CHAR("char");
    private final String value;

    WorkflowParameterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WorkflowParameterType fromValue(String v) {
        for (WorkflowParameterType c: WorkflowParameterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
