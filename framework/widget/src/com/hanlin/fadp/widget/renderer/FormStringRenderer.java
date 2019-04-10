
package com.hanlin.fadp.widget.renderer;

import java.io.IOException;
import java.util.Map;

import com.hanlin.fadp.widget.model.ModelForm;
import com.hanlin.fadp.widget.model.ModelFormField;

/**
 * Widget Library - Form/Grid renderer.
 */
public interface FormStringRenderer {
    public void renderDisplayField(Appendable writer, Map<String, Object> context, ModelFormField.DisplayField displayField) throws IOException;
    public void renderHyperlinkField(Appendable writer, Map<String, Object> context, ModelFormField.HyperlinkField hyperlinkField) throws IOException;
    public void renderMenuField(Appendable writer, Map<String, Object> context, ModelFormField.MenuField menuField) throws IOException;

    public void renderTextField(Appendable writer, Map<String, Object> context, ModelFormField.TextField textField) throws IOException;
    public void renderTextareaField(Appendable writer, Map<String, Object> context, ModelFormField.TextareaField textareaField) throws IOException;
    public void renderDateTimeField(Appendable writer, Map<String, Object> context, ModelFormField.DateTimeField dateTimeField) throws IOException;

    public void renderDropDownField(Appendable writer, Map<String, Object> context, ModelFormField.DropDownField dropDownField) throws IOException;
    public void renderCheckField(Appendable writer, Map<String, Object> context, ModelFormField.CheckField checkField) throws IOException;
    public void renderRadioField(Appendable writer, Map<String, Object> context, ModelFormField.RadioField radioField) throws IOException;

    public void renderSubmitField(Appendable writer, Map<String, Object> context, ModelFormField.SubmitField submitField) throws IOException;
    public void renderResetField(Appendable writer, Map<String, Object> context, ModelFormField.ResetField resetField) throws IOException;

    public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String value) throws IOException;
    public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField.HiddenField hiddenField) throws IOException;
    public void renderIgnoredField(Appendable writer, Map<String, Object> context, ModelFormField.IgnoredField ignoredField) throws IOException;

    public void renderFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException;
    public void renderSingleFormFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException;

    public void renderFormOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderMultiFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;

    public void renderFormatListWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatListWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;

    public void renderFormatHeaderRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatHeaderRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatHeaderRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException;
    public void renderFormatHeaderRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException;

    public void renderFormatHeaderRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatHeaderRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatHeaderRowFormCellTitleSeparator(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, boolean isLast) throws IOException;

    public void renderFormatItemRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatItemRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatItemRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException;
    public void renderFormatItemRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException;
    public void renderFormatItemRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatItemRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;

    public void renderFormatSingleWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatSingleWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;

    public void renderFormatFieldRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatFieldRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;
    public void renderFormatFieldRowTitleCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException;
    public void renderFormatFieldRowTitleCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException;
    public void renderFormatFieldRowSpacerCell(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException;
    public void renderFormatFieldRowWidgetCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException;
    public void renderFormatFieldRowWidgetCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException;

    public void renderFormatEmptySpace(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException;

    public void renderTextFindField(Appendable writer, Map<String, Object> context, ModelFormField.TextFindField textField) throws IOException;
    public void renderDateFindField(Appendable writer, Map<String, Object> context, ModelFormField.DateFindField textField) throws IOException;
    public void renderRangeFindField(Appendable writer, Map<String, Object> context, ModelFormField.RangeFindField textField) throws IOException;
    public void renderLookupField(Appendable writer, Map<String, Object> context, ModelFormField.LookupField textField) throws IOException;
    public void renderFileField(Appendable writer, Map<String, Object> context, ModelFormField.FileField textField) throws IOException;
    public void renderPasswordField(Appendable writer, Map<String, Object> context, ModelFormField.PasswordField textField) throws IOException;
    public void renderImageField(Appendable writer, Map<String, Object> context, ModelFormField.ImageField textField) throws IOException;
    public void renderBanner(Appendable writer, Map<String, Object> context, ModelForm.Banner banner) throws IOException;
    public void renderContainerFindField(Appendable writer, Map<String, Object> context, ModelFormField.ContainerField containerField) throws IOException;
    public void renderFieldGroupOpen(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException;
    public void renderFieldGroupClose(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException;
}
