
package com.hanlin.fadp.widget.model;

import com.hanlin.fadp.widget.model.AbstractModelAction.EntityAnd;
import com.hanlin.fadp.widget.model.AbstractModelAction.EntityCondition;
import com.hanlin.fadp.widget.model.AbstractModelAction.EntityOne;
import com.hanlin.fadp.widget.model.AbstractModelAction.GetRelated;
import com.hanlin.fadp.widget.model.AbstractModelAction.GetRelatedOne;
import com.hanlin.fadp.widget.model.AbstractModelAction.PropertyMap;
import com.hanlin.fadp.widget.model.AbstractModelAction.PropertyToField;
import com.hanlin.fadp.widget.model.AbstractModelAction.Script;
import com.hanlin.fadp.widget.model.AbstractModelAction.Service;
import com.hanlin.fadp.widget.model.AbstractModelAction.SetField;
import com.hanlin.fadp.widget.model.ModelFormAction.CallParentActions;

/**
 * An object that generates XML from widget models.
 * The generated XML is unformatted - if you want to
 * "pretty print" the XML, then use a transformer.
 *
 */
public class XmlWidgetActionVisitor extends XmlAbstractWidgetVisitor implements ModelActionVisitor {

    public static final String module = XmlWidgetActionVisitor.class.getName();

    public XmlWidgetActionVisitor(Appendable writer) {
        super(writer);
    }

    @Override
    public void visit(CallParentActions callParentActions) throws Exception {
        writer.append("<call-parent-actions/>");
    }

    @Override
    public void visit(EntityAnd entityAnd) throws Exception {
        writer.append("<entity-and/>");
        // TODO: Create ByAndFinder visitor
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelTreeAction.EntityAnd entityAnd) throws Exception {
        writer.append("<entity-and/>");
        // TODO: Create ByAndFinder visitor
    }

    @Override
    public void visit(EntityCondition entityCondition) throws Exception {
        writer.append("<entity-condition/>");
        // TODO: Create ByConditionFinder visitor
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelTreeAction.EntityCondition entityCondition) throws Exception {
        writer.append("<entity-condition/>");
        // TODO: Create ByConditionFinder visitor
    }

    @Override
    public void visit(EntityOne entityOne) throws Exception {
        writer.append("<entity-one/>");
        // TODO: Create PrimaryKeyFinder visitor
    }

    @Override
    public void visit(GetRelated getRelated) throws Exception {
        writer.append("<get-related");
        visitAttribute("value-field", getRelated.getValueNameAcsr());
        visitAttribute("list", getRelated.getListNameAcsr());
        visitAttribute("relation-name", getRelated.getRelationName());
        visitAttribute("map", getRelated.getMapAcsr());
        visitAttribute("order-by-list", getRelated.getOrderByListAcsr());
        visitAttribute("use-cache", getRelated.getUseCache());
        writer.append("/>");
    }

    @Override
    public void visit(GetRelatedOne getRelatedOne) throws Exception {
        writer.append("<get-related-one");
        visitAttribute("value-field", getRelatedOne.getValueNameAcsr());
        visitAttribute("to-value-field", getRelatedOne.getToValueNameAcsr());
        visitAttribute("relation-name", getRelatedOne.getRelationName());
        visitAttribute("use-cache", getRelatedOne.getUseCache());
        writer.append("/>");
    }

    @Override
    public void visit(PropertyMap propertyMap) throws Exception {
        writer.append("<property-map");
        visitAttribute("resource", propertyMap.getResourceExdr());
        visitAttribute("map-name", propertyMap.getMapNameAcsr());
        visitAttribute("global", propertyMap.getGlobalExdr());
        writer.append("/>");
    }

    @Override
    public void visit(PropertyToField propertyToField) throws Exception {
        writer.append("<property-map");
        visitAttribute("resource", propertyToField.getResourceExdr());
        visitAttribute("property", propertyToField.getPropertyExdr());
        visitAttribute("field", propertyToField.getFieldAcsr());
        visitAttribute("default", propertyToField.getDefaultExdr());
        visitAttribute("no-locale", propertyToField.getNoLocale());
        visitAttribute("arg-list-name", propertyToField.getArgListAcsr());
        visitAttribute("global", propertyToField.getGlobalExdr());
        writer.append("/>");
    }

    @Override
    public void visit(Script script) throws Exception {
        writer.append("<script");
        visitAttribute("location", script.getLocation());
        visitAttribute("method", script.getMethod());
        writer.append("/>");
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelTreeAction.Script script) throws Exception {
        writer.append("<script");
        visitAttribute("location", script.getLocation());
        visitAttribute("method", script.getMethod());
        writer.append("/>");
    }

    @Override
    public void visit(Service service) throws Exception {
        writer.append("<service");
        visitAttribute("service-name", service.getServiceNameExdr());
        visitAttribute("result-map", service.getResultMapNameAcsr());
        visitAttribute("auto-field-map", service.getAutoFieldMapExdr());
        writer.append(">");
        // TODO: Create Field Map visitor
        writer.append("<field-map/>");
        writer.append("</service>");
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelFormAction.Service service) throws Exception {
        writer.append("<service");
        visitAttribute("service-name", service.getServiceNameExdr());
        visitAttribute("result-map", service.getResultMapNameAcsr());
        visitAttribute("auto-field-map", service.getAutoFieldMapExdr());
        visitAttribute("result-map-list", service.getResultMapListNameExdr());
        visitAttribute("ignore-error", service.getIgnoreError());
        writer.append(">");
        // TODO: Create Field Map visitor
        writer.append("<field-map/>");
        writer.append("</service>");
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelTreeAction.Service service) throws Exception {
        writer.append("<service");
        visitAttribute("service-name", service.getServiceNameExdr());
        visitAttribute("result-map", service.getResultMapNameAcsr());
        visitAttribute("auto-field-map", service.getAutoFieldMapExdr());
        visitAttribute("result-map-list", service.getResultMapListNameExdr());
        visitAttribute("result-map-value", service.getResultMapValueNameExdr());
        visitAttribute("value", service.getValueNameExdr());
        writer.append(">");
        // TODO: Create Field Map visitor
        writer.append("<field-map/>");
        writer.append("</service>");
    }

    @Override
    public void visit(SetField setField) throws Exception {
        writer.append("<set");
        visitAttribute("field", setField.getField());
        visitAttribute("from-field", setField.getFromField());
        visitAttribute("value", setField.getValueExdr());
        visitAttribute("default-value", setField.getDefaultExdr());
        visitAttribute("global", setField.getGlobalExdr());
        visitAttribute("type", setField.getType());
        visitAttribute("to-scope", setField.getToScope());
        visitAttribute("from-scope", setField.getFromScope());
        writer.append("/>");
    }

    @Override
    public void visit(com.hanlin.fadp.widget.model.ModelMenuAction.SetField setField) throws Exception {
        writer.append("<set");
        visitAttribute("field", setField.getField());
        visitAttribute("from-field", setField.getFromField());
        visitAttribute("value", setField.getValueExdr());
        visitAttribute("default-value", setField.getDefaultExdr());
        visitAttribute("global", setField.getGlobalExdr());
        visitAttribute("type", setField.getType());
        visitAttribute("to-scope", setField.getToScope());
        visitAttribute("from-scope", setField.getFromScope());
        writer.append("/>");
    }
}
