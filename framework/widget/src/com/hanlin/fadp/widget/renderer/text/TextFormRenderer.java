
package com.hanlin.fadp.widget.renderer.text;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.widget.WidgetWorker;
import com.hanlin.fadp.widget.model.ModelForm;
import com.hanlin.fadp.widget.model.ModelFormField;
import com.hanlin.fadp.widget.model.ModelFormField.CheckField;
import com.hanlin.fadp.widget.model.ModelFormField.ContainerField;
import com.hanlin.fadp.widget.model.ModelFormField.DateFindField;
import com.hanlin.fadp.widget.model.ModelFormField.DateTimeField;
import com.hanlin.fadp.widget.model.ModelFormField.DisplayField;
import com.hanlin.fadp.widget.model.ModelFormField.DropDownField;
import com.hanlin.fadp.widget.model.ModelFormField.FieldInfoWithOptions;
import com.hanlin.fadp.widget.model.ModelFormField.FileField;
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
import com.hanlin.fadp.widget.model.ModelFormField.SubmitField;
import com.hanlin.fadp.widget.model.ModelFormField.TextField;
import com.hanlin.fadp.widget.model.ModelFormField.TextFindField;
import com.hanlin.fadp.widget.model.ModelFormField.TextareaField;
import com.hanlin.fadp.widget.renderer.FormStringRenderer;


/**
 * Widget Library - Csv Form Renderer implementation
 *
 */
public class TextFormRenderer implements FormStringRenderer {

    public static final String module = TextFormRenderer.class.getName();

    HttpServletRequest request;
    HttpServletResponse response;

    protected TextFormRenderer() {}

    public TextFormRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void appendWhitespace(Appendable writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.append("\r\n");
        //writer.append(' ');
    }

    private void makeTextString(Appendable writer, String widgetStyle, String text) throws IOException {
        // TODO: escape characters here
        writer.append(text);
    }

    public void renderDisplayField(Appendable writer, Map<String, Object> context, DisplayField displayField) throws IOException {
        ModelFormField modelFormField = displayField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), displayField.getDescription(context));
    }

    public void renderHyperlinkField(Appendable writer, Map<String, Object> context, HyperlinkField hyperlinkField) throws IOException {
        ModelFormField modelFormField = hyperlinkField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), hyperlinkField.getDescription(context));
    }

    public void renderMenuField(Appendable writer, Map<String, Object> context, MenuField menuField) throws IOException {
        menuField.renderFieldString(writer, context, null);
    }

    public void renderTextField(Appendable writer, Map<String, Object> context, TextField textField) throws IOException {
        ModelFormField modelFormField = textField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textField.getDefaultValue(context)));
    }

    public void renderTextareaField(Appendable writer, Map<String, Object> context, TextareaField textareaField) throws IOException {
        ModelFormField modelFormField = textareaField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textareaField.getDefaultValue(context)));
    }

    public void renderDateTimeField(Appendable writer, Map<String, Object> context, DateTimeField dateTimeField) throws IOException {
        ModelFormField modelFormField = dateTimeField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, dateTimeField.getDefaultValue(context)));
    }

    public void renderDropDownField(Appendable writer, Map<String, Object> context, DropDownField dropDownField) throws IOException {
        ModelFormField modelFormField = dropDownField.getModelFormField();
        String currentValue = modelFormField.getEntry(context);
        List<ModelFormField.OptionValue> allOptionValues = dropDownField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
        // if the current value should go first, display it
        if (UtilValidate.isNotEmpty(currentValue) && "first-in-list".equals(dropDownField.getCurrent())) {
            String explicitDescription = dropDownField.getCurrentDescription(context);
            if (UtilValidate.isNotEmpty(explicitDescription)) {
                this.makeTextString(writer, modelFormField.getWidgetStyle(), explicitDescription);
            } else {
                this.makeTextString(writer, modelFormField.getWidgetStyle(), FieldInfoWithOptions.getDescriptionForOptionKey(currentValue, allOptionValues));
            }
        } else {
            for (ModelFormField.OptionValue optionValue: allOptionValues) {
                String noCurrentSelectedKey = dropDownField.getNoCurrentSelectedKey(context);
                if ((UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey()) && "selected".equals(dropDownField.getCurrent())) ||
                        (UtilValidate.isEmpty(currentValue) && noCurrentSelectedKey != null && noCurrentSelectedKey.equals(optionValue.getKey()))) {
                    this.makeTextString(writer, modelFormField.getWidgetStyle(), optionValue.getDescription());
                    break;
                }
            }
        }
    }

    public void renderCheckField(Appendable writer, Map<String, Object> context, CheckField checkField) {
    }

    public void renderRadioField(Appendable writer, Map<String, Object> context, RadioField radioField) {
    }

    public void renderSubmitField(Appendable writer, Map<String, Object> context, SubmitField submitField) {
    }

    public void renderResetField(Appendable writer, Map<String, Object> context, ResetField resetField) {
    }

    public void renderHiddenField(Appendable writer, Map<String, Object> context, HiddenField hiddenField) {
    }

    public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String value) {
    }

    public void renderIgnoredField(Appendable writer, Map<String, Object> context, IgnoredField ignoredField) {
    }

    public void renderFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getTitle(context));
    }

    public void renderSingleFormFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        renderFieldTitle(writer, context, modelFormField);
    }

    public void renderFormOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderMultiFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatListWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatListWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatHeaderRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatHeaderRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        this.appendWhitespace(writer);
    }

    public void renderFormatHeaderRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) {
    }

    public void renderFormatHeaderRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) {
    }

    public void renderFormatHeaderRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatHeaderRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatHeaderRowFormCellTitleSeparator(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, boolean isLast) {
    }

    public void renderFormatItemRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatItemRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        this.appendWhitespace(writer);
    }

    public void renderFormatItemRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) {
    }

    public void renderFormatItemRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) {
    }

    public void renderFormatItemRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatItemRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatSingleWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatSingleWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatFieldRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatFieldRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFormatFieldRowTitleCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) {
    }

    public void renderFormatFieldRowTitleCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) {
    }

    public void renderFormatFieldRowSpacerCell(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) {
    }

    public void renderFormatFieldRowWidgetCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) {
    }

    public void renderFormatFieldRowWidgetCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) {
    }

    public void renderFormatEmptySpace(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
        // TODO
    }

    public void renderTextFindField(Appendable writer, Map<String, Object> context, TextFindField textFindField) throws IOException {
        ModelFormField modelFormField = textFindField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textFindField.getDefaultValue(context)));
    }

    public void renderRangeFindField(Appendable writer, Map<String, Object> context, RangeFindField rangeFindField) throws IOException {
        ModelFormField modelFormField = rangeFindField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, rangeFindField.getDefaultValue(context)));
    }

    public void renderDateFindField(Appendable writer, Map<String, Object> context, DateFindField dateFindField) throws IOException {
        ModelFormField modelFormField = dateFindField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, dateFindField.getDefaultValue(context)));
    }

    public void renderLookupField(Appendable writer, Map<String, Object> context, LookupField lookupField) throws IOException {
        ModelFormField modelFormField = lookupField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, lookupField.getDefaultValue(context)));
    }

    public void renderNextPrev(Appendable writer, Map<String, Object> context, ModelForm modelForm) {
    }

    public void renderFileField(Appendable writer, Map<String, Object> context, FileField textField) throws IOException {
        ModelFormField modelFormField = textField.getModelFormField();
        this.makeTextString(writer, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textField.getDefaultValue(context)));
    }

    public void renderPasswordField(Appendable writer, Map<String, Object> context, PasswordField passwordField) {
    }

    public void renderImageField(Appendable writer, Map<String, Object> context, ImageField imageField) {
        // TODO
    }

    public void renderFieldGroupOpen(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) {
        // TODO
    }

    public void renderFieldGroupClose(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) {
        // TODO
    }

    public void renderBanner(Appendable writer, Map<String, Object> context, ModelForm.Banner banner) {
        // TODO
    }

    public void renderHyperlinkTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String titleText) {
    }

    public void renderContainerFindField(Appendable writer, Map<String, Object> context, ContainerField containerField) throws IOException {
    }
}
