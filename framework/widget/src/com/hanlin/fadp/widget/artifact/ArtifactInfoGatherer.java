
package com.hanlin.fadp.widget.artifact;

import java.util.Set;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;
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
import com.hanlin.fadp.widget.model.FieldInfo;
import com.hanlin.fadp.widget.model.HtmlWidget;
import com.hanlin.fadp.widget.model.HtmlWidget.HtmlTemplate;
import com.hanlin.fadp.widget.model.HtmlWidget.HtmlTemplateDecorator;
import com.hanlin.fadp.widget.model.HtmlWidget.HtmlTemplateDecoratorSection;
import com.hanlin.fadp.widget.model.IterateSectionWidget;
import com.hanlin.fadp.widget.model.ModelAction;
import com.hanlin.fadp.widget.model.ModelActionVisitor;
import com.hanlin.fadp.widget.model.ModelFieldVisitor;
import com.hanlin.fadp.widget.model.ModelForm;
import com.hanlin.fadp.widget.model.ModelForm.AltTarget;
import com.hanlin.fadp.widget.model.ModelForm.AutoFieldsEntity;
import com.hanlin.fadp.widget.model.ModelForm.AutoFieldsService;
import com.hanlin.fadp.widget.model.ModelFormAction;
import com.hanlin.fadp.widget.model.ModelFormAction.CallParentActions;
import com.hanlin.fadp.widget.model.ModelFormField;
import com.hanlin.fadp.widget.model.ModelFormField.CheckField;
import com.hanlin.fadp.widget.model.ModelFormField.ContainerField;
import com.hanlin.fadp.widget.model.ModelFormField.DateFindField;
import com.hanlin.fadp.widget.model.ModelFormField.DateTimeField;
import com.hanlin.fadp.widget.model.ModelFormField.DisplayEntityField;
import com.hanlin.fadp.widget.model.ModelFormField.DisplayField;
import com.hanlin.fadp.widget.model.ModelFormField.DropDownField;
import com.hanlin.fadp.widget.model.ModelFormField.FieldInfoWithOptions;
import com.hanlin.fadp.widget.model.ModelFormField.FileField;
import com.hanlin.fadp.widget.model.ModelFormField.FormField;
import com.hanlin.fadp.widget.model.ModelFormField.GridField;
import com.hanlin.fadp.widget.model.ModelFormField.HiddenField;
import com.hanlin.fadp.widget.model.ModelFormField.HyperlinkField;
import com.hanlin.fadp.widget.model.ModelFormField.IgnoredField;
import com.hanlin.fadp.widget.model.ModelFormField.ImageField;
import com.hanlin.fadp.widget.model.ModelFormField.LookupField;
import com.hanlin.fadp.widget.model.ModelFormField.MenuField;
import com.hanlin.fadp.widget.model.ModelFormField.PasswordField;
import com.hanlin.fadp.widget.model.ModelFormField.RadioField;
import com.hanlin.fadp.widget.model.ModelFormField.RangeFindField;
import com.hanlin.fadp.widget.model.ModelFormField.ResetField;
import com.hanlin.fadp.widget.model.ModelFormField.ScreenField;
import com.hanlin.fadp.widget.model.ModelFormField.SubmitField;
import com.hanlin.fadp.widget.model.ModelFormField.TextField;
import com.hanlin.fadp.widget.model.ModelFormField.TextFindField;
import com.hanlin.fadp.widget.model.ModelFormField.TextareaField;
import com.hanlin.fadp.widget.model.ModelGrid;
import com.hanlin.fadp.widget.model.ModelMenu;
import com.hanlin.fadp.widget.model.ModelMenuAction;
import com.hanlin.fadp.widget.model.ModelMenuItem;
import com.hanlin.fadp.widget.model.ModelScreen;
import com.hanlin.fadp.widget.model.ModelScreenWidget;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Column;
import com.hanlin.fadp.widget.model.ModelScreenWidget.ColumnContainer;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Container;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Content;
import com.hanlin.fadp.widget.model.ModelScreenWidget.DecoratorScreen;
import com.hanlin.fadp.widget.model.ModelScreenWidget.DecoratorSection;
import com.hanlin.fadp.widget.model.ModelScreenWidget.DecoratorSectionInclude;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Form;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Grid;
import com.hanlin.fadp.widget.model.ModelScreenWidget.HorizontalSeparator;
import com.hanlin.fadp.widget.model.ModelScreenWidget.IncludeScreen;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Label;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Menu;
import com.hanlin.fadp.widget.model.ModelScreenWidget.PlatformSpecific;
import com.hanlin.fadp.widget.model.ModelScreenWidget.PortalPage;
import com.hanlin.fadp.widget.model.ModelScreenWidget.ScreenImage;
import com.hanlin.fadp.widget.model.ModelScreenWidget.ScreenLink;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Screenlet;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Section;
import com.hanlin.fadp.widget.model.ModelScreenWidget.Tree;
import com.hanlin.fadp.widget.model.ModelSingleForm;
import com.hanlin.fadp.widget.model.ModelTree;
import com.hanlin.fadp.widget.model.ModelTree.ModelNode;
import com.hanlin.fadp.widget.model.ModelTree.ModelNode.ModelSubNode;
import com.hanlin.fadp.widget.model.ModelTreeAction;
import com.hanlin.fadp.widget.model.ModelWidgetVisitor;

/**
 * An object that gathers artifact information from screen widgets.
 */
public final class ArtifactInfoGatherer implements ModelWidgetVisitor, ModelActionVisitor {

    private final ArtifactInfoContext infoContext;

    public ArtifactInfoGatherer(ArtifactInfoContext infoContext) {
        this.infoContext = infoContext;
    }

    @Override
    public void visit(CallParentActions callParentActions) throws Exception {
    }

    @Override
    public void visit(Column column) throws Exception {
    }

    @Override
    public void visit(ColumnContainer columnContainer) throws Exception {
        for (Column column : columnContainer.getColumns()) {
            for (ModelScreenWidget widget : column.getSubWidgets()) {
                widget.accept(this);
            }
        }
    }

    @Override
    public void visit(Container container) throws Exception {
        for (ModelScreenWidget widget : container.getSubWidgets()) {
            widget.accept(this);
        }
    }

    @Override
    public void visit(Content content) throws Exception {
        infoContext.addEntityName("Content");
        if (!content.getDataResourceId().isEmpty()) {
            infoContext.addEntityName("DataResource");
        }
    }

    @Override
    public void visit(DecoratorScreen decoratorScreen) throws Exception {
        for (ModelScreenWidget section : decoratorScreen.getSectionMap().values()) {
            section.accept(this);
        }
    }

    @Override
    public void visit(DecoratorSection decoratorSection) throws Exception {
        for (ModelScreenWidget widget : decoratorSection.getSubWidgets()) {
            widget.accept(this);
        }
    }

    @Override
    public void visit(DecoratorSectionInclude decoratorSectionInclude) throws Exception {
    }

    @Override
    public void visit(EntityAnd entityAnd) throws Exception {
        infoContext.addEntityName(entityAnd.getFinder().getEntityName());
    }

    @Override
    public void visit(EntityCondition entityCondition) throws Exception {
        infoContext.addEntityName(entityCondition.getFinder().getEntityName());
    }

    @Override
    public void visit(EntityOne entityOne) throws Exception {
        infoContext.addEntityName(entityOne.getFinder().getEntityName());
    }

    @Override
    public void visit(Form form) throws Exception {
        String formLocation = form.getLocation().concat("#").concat(form.getName());
        infoContext.addFormLocation(formLocation);
    }

    @Override
    public void visit(Grid grid) throws Exception {
        String gridLocation = grid.getLocation().concat("#").concat(grid.getName());
        infoContext.addFormLocation(gridLocation);
    }

    @Override
    public void visit(GetRelated getRelated) throws Exception {
        infoContext.addEntityName(getRelated.getRelationName());
    }

    @Override
    public void visit(GetRelatedOne getRelatedOne) throws Exception {
        infoContext.addEntityName(getRelatedOne.getRelationName());
    }

    @Override
    public void visit(HorizontalSeparator horizontalSeparator) throws Exception {
    }

    @Override
    public void visit(HtmlTemplate htmlTemplate) throws Exception {
    }

    @Override
    public void visit(HtmlTemplateDecorator htmlTemplateDecorator) throws Exception {
    }

    @Override
    public void visit(HtmlTemplateDecoratorSection htmlTemplateDecoratorSection) throws Exception {
    }

    @Override
    public void visit(HtmlWidget htmlWidget) throws Exception {
    }

    @Override
    public void visit(IncludeScreen includeScreen) throws Exception {
    }

    @Override
    public void visit(IterateSectionWidget iterateSectionWidget) throws Exception {
        for (Section section : iterateSectionWidget.getSectionList()) {
            section.accept(this);
        }
    }

    @Override
    public void visit(Label label) throws Exception {
    }

    @Override
    public void visit(Menu menu) throws Exception {
    }

    @Override
    public void visit(ModelSingleForm modelForm) throws Exception {
        visitModelForm(modelForm);
    }

    @Override
    public void visit(ModelGrid modelGrid) throws Exception {
        visitModelForm(modelGrid);
    }

    @Override
    public void visit(ModelFormAction.Service service) throws Exception {
        infoContext.addServiceName(service.getServiceName());
        // TODO: Look for entityName in performFind service call
    }

    @Override
    public void visit(ModelMenu modelMenu) throws Exception {
    }

    @Override
    public void visit(ModelMenuAction.SetField setField) throws Exception {
    }

    @Override
    public void visit(ModelMenuItem modelMenuItem) throws Exception {
    }

    @Override
    public void visit(ModelNode modelNode) throws Exception {
    }

    @Override
    public void visit(ModelScreen modelScreen) throws Exception {
        String screenLocation = modelScreen.getSourceLocation().concat("#").concat(modelScreen.getName());
        infoContext.addScreenLocation(screenLocation);
        modelScreen.getSection().accept(this);
    }

    @Override
    public void visit(ModelSubNode modelSubNode) throws Exception {
    }

    @Override
    public void visit(ModelTree modelTree) throws Exception {
    }

    @Override
    public void visit(ModelTreeAction.EntityAnd entityAnd) throws Exception {
    }

    @Override
    public void visit(ModelTreeAction.EntityCondition entityCondition) throws Exception {
    }

    @Override
    public void visit(ModelTreeAction.Script script) throws Exception {
    }

    @Override
    public void visit(ModelTreeAction.Service service) throws Exception {
    }

    @Override
    public void visit(PlatformSpecific platformSpecific) throws Exception {
    }

    @Override
    public void visit(PortalPage portalPage) throws Exception {
    }

    @Override
    public void visit(PropertyMap propertyMap) throws Exception {
    }

    @Override
    public void visit(PropertyToField propertyToField) throws Exception {
    }

    @Override
    public void visit(ScreenImage image) throws Exception {
    }

    @Override
    public void visit(Screenlet screenlet) throws Exception {
        for (ModelScreenWidget widget : screenlet.getSubWidgets()) {
            widget.accept(this);
        }
    }

    @Override
    public void visit(ScreenLink link) throws Exception {
        String target = link.getTarget(null);
        String urlMode = link.getUrlMode();
        try {
            Set<String> controllerLocAndRequestSet = ConfigXMLReader.findControllerRequestUniqueForTargetType(target, urlMode);
            if (controllerLocAndRequestSet != null) {
                for (String requestLocation : controllerLocAndRequestSet) {
                    infoContext.addRequestLocation(requestLocation);
                }
            }
        } catch (GeneralException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(Script script) throws Exception {
    }

    @Override
    public void visit(Section section) throws Exception {
        for (ModelAction action : section.getActions()) {
            action.accept(this);
        }
        for (ModelScreenWidget subWidget : section.getSubWidgets()) {
            subWidget.accept(this);
        }
        for (ModelScreenWidget subWidget : section.getFailWidgets()) {
            subWidget.accept(this);
        }
    }

    @Override
    public void visit(Service service) throws Exception {
        infoContext.addServiceName(service.getServiceNameExdr().getOriginal());
        // TODO: Look for entityName in performFind service call
    }

    @Override
    public void visit(SetField setField) throws Exception {
    }

    @Override
    public void visit(Tree tree) throws Exception {
    }

    private class FieldInfoGatherer implements ModelFieldVisitor {

        private void addRequestLocations(String target, String urlMode) {
            try {
                Set<String> controllerLocAndRequestSet = ConfigXMLReader
                        .findControllerRequestUniqueForTargetType(target, urlMode);
                if (controllerLocAndRequestSet != null) {
                    for (String requestLocation : controllerLocAndRequestSet) {
                        infoContext.addRequestLocation(requestLocation);
                    }
                }
            } catch (GeneralException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void visit(CheckField checkField) {
        }

        @Override
        public void visit(ContainerField containerField) {
        }

        @Override
        public void visit(DateFindField dateTimeField) {
        }

        @Override
        public void visit(DateTimeField dateTimeField) {
        }

        @Override
        public void visit(DisplayEntityField displayField) {
            if (displayField.getSubHyperlink() != null) {
                String target = displayField.getSubHyperlink().getTarget(null);
                String urlMode = displayField.getSubHyperlink().getUrlMode();
                addRequestLocations(target, urlMode);
            }
        }

        @Override
        public void visit(DisplayField displayField) {
        }

        @Override
        public void visit(DropDownField dropDownField) {
            if (dropDownField.getSubHyperlink() != null) {
                String target = dropDownField.getSubHyperlink().getTarget(null);
                String urlMode = dropDownField.getSubHyperlink().getUrlMode();
                addRequestLocations(target, urlMode);
            }
        }

        @Override
        public void visit(FileField textField) {
            if (textField.getSubHyperlink() != null) {
                String target = textField.getSubHyperlink().getTarget(null);
                String urlMode = textField.getSubHyperlink().getUrlMode();
                addRequestLocations(target, urlMode);
            }
        }

        @Override
        public void visit(HiddenField hiddenField) {
        }

        @Override
        public void visit(HyperlinkField hyperlinkField) {
            String target = hyperlinkField.getTarget(null);
            String urlMode = hyperlinkField.getUrlMode();
            addRequestLocations(target, urlMode);
        }

        @Override
        public void visit(MenuField menuField) {
            //TODO
        }
        
        @Override
        public void visit(FormField formField) {
            //TODO
        }
        
        @Override
        public void visit(GridField gridField) {
            //TODO
        }

        @Override
        public void visit(IgnoredField ignoredField) {
        }

        @Override
        public void visit(ImageField imageField) {
            if (imageField.getSubHyperlink() != null) {
                String target = imageField.getSubHyperlink().getTarget(null);
                String urlMode = imageField.getSubHyperlink().getUrlMode();
                addRequestLocations(target, urlMode);
            }
        }

        @Override
        public void visit(LookupField textField) {
        }

        @Override
        public void visit(PasswordField textField) {
        }

        @Override
        public void visit(RadioField radioField) {
        }

        @Override
        public void visit(RangeFindField textField) {
        }

        @Override
        public void visit(ResetField resetField) {
        }
        
        @Override
        public void visit(ScreenField screenField) {
            //TODO
        }

        @Override
        public void visit(SubmitField submitField) {
        }

        @Override
        public void visit(TextareaField textareaField) {
        }

        @Override
        public void visit(TextField textField) {
        }

        @Override
        public void visit(TextFindField textField) {
        }
    }

    public void visitModelForm(ModelForm modelForm) throws Exception {
        if (modelForm.getActions() != null) {
            for (ModelAction action : modelForm.getActions()) {
                action.accept(this);
            }
        }
        if (modelForm.getRowActions() != null) {
            for (ModelAction action : modelForm.getRowActions()) {
                action.accept(this);
            }
        }
        for (AutoFieldsEntity autoFieldsEntity : modelForm.getAutoFieldsEntities()) {
            infoContext.addEntityName(autoFieldsEntity.entityName);
        }
        for (AutoFieldsService autoFieldsService : modelForm.getAutoFieldsServices()) {
            infoContext.addServiceName(autoFieldsService.serviceName);
        }
        if (modelForm.getAltTargets() != null) {
            for (AltTarget altTarget : modelForm.getAltTargets()) {
                String target = altTarget.targetExdr.getOriginal();
                String urlMode = "intra-app";
                try {
                    Set<String> controllerLocAndRequestSet = ConfigXMLReader.findControllerRequestUniqueForTargetType(target,
                            urlMode);
                    if (controllerLocAndRequestSet != null) {
                        for (String requestLocation : controllerLocAndRequestSet) {
                            infoContext.addTargetLocation(requestLocation);
                        }
                    }
                } catch (GeneralException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!modelForm.getTarget().isEmpty()) {
            String target = modelForm.getTarget();
            String urlMode = UtilValidate.isNotEmpty(modelForm.getTargetType()) ? modelForm.getTargetType() : "intra-app";
            if (target.indexOf("${") < 0) {
                try {
                    Set<String> controllerLocAndRequestSet = ConfigXMLReader.findControllerRequestUniqueForTargetType(target,
                            urlMode);
                    if (controllerLocAndRequestSet != null) {
                        for (String requestLocation : controllerLocAndRequestSet) {
                            infoContext.addTargetLocation(requestLocation);
                        }
                    }
                } catch (GeneralException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        FieldInfoGatherer fieldInfoGatherer = new FieldInfoGatherer();
        for (ModelFormField modelFormField : modelForm.getFieldList()) {
            if (UtilValidate.isNotEmpty(modelFormField.getEntityName())) {
                infoContext.addEntityName(modelFormField.getEntityName());
            }
            if (modelFormField.getFieldInfo() instanceof ModelFormField.DisplayEntityField) {
                infoContext.addEntityName(((ModelFormField.DisplayEntityField) modelFormField.getFieldInfo()).getEntityName());
            }
            if (modelFormField.getFieldInfo() instanceof FieldInfoWithOptions) {
                for (ModelFormField.OptionSource optionSource : ((FieldInfoWithOptions) modelFormField
                        .getFieldInfo()).getOptionSources()) {
                    if (optionSource instanceof ModelFormField.EntityOptions) {
                        infoContext.addEntityName(((ModelFormField.EntityOptions) optionSource).getEntityName());
                    }
                }
            }
            if (UtilValidate.isNotEmpty(modelFormField.getServiceName())) {
                infoContext.addServiceName(modelFormField.getServiceName());
            }
            FieldInfo fieldInfo = modelFormField.getFieldInfo();
            if (fieldInfo != null) {
                fieldInfo.accept(fieldInfoGatherer);
            }
        }
    }
}
