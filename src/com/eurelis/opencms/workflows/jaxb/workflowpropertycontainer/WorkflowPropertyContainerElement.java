//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1.6-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.06 at 06:35:29 PM CEST 
//


package com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="actionName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="actionDate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cmsUserUUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isAutoExecuted" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comment" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "WorkflowPropertyContainerElement")
public class WorkflowPropertyContainerElement {

    @XmlAttribute(required = true)
    protected String actionName;
    @XmlAttribute(required = true)
    protected String actionDate;
    @XmlAttribute(required = true)
    protected String cmsUserUUID;
    @XmlAttribute(required = true)
    protected String isAutoExecuted;
    @XmlAttribute(required = true)
    protected String comment;

    /**
     * Gets the value of the actionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Sets the value of the actionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionName(String value) {
        this.actionName = value;
    }

    /**
     * Gets the value of the actionDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionDate() {
        return actionDate;
    }

    /**
     * Sets the value of the actionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionDate(String value) {
        this.actionDate = value;
    }

    /**
     * Gets the value of the cmsUserUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCmsUserUUID() {
        return cmsUserUUID;
    }

    /**
     * Sets the value of the cmsUserUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCmsUserUUID(String value) {
        this.cmsUserUUID = value;
    }

    /**
     * Gets the value of the isAutoExecuted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsAutoExecuted() {
        return isAutoExecuted;
    }

    /**
     * Sets the value of the isAutoExecuted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsAutoExecuted(String value) {
        this.isAutoExecuted = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

}