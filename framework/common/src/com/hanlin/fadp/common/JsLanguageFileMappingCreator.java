

package com.hanlin.fadp.common;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.template.FreeMarkerWorker;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.ServiceUtil;

// Use the createJsLanguageFileMapping service to create or update the JsLanguageFilesMapping.java. You will still need to compile thereafter

public class JsLanguageFileMappingCreator {

    private static final String module = JsLanguageFileMappingCreator.class.getName();

    public static Map<String, Object> createJsLanguageFileMapping(DispatchContext ctx, Map<String, ?> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String encoding = (String) context.get("encoding"); // default value: UTF-8

        List<Locale> localeList = UtilMisc.availableLocales();
        Map<String, Object> jQueryLocaleFile = new LinkedHashMap<String, Object>();
        Map<String, String> dateJsLocaleFile = new LinkedHashMap<String, String>();
        Map<String, String> validationLocaleFile = new LinkedHashMap<String, String>();
        Map<String, String> dateTimePickerLocaleFile = new LinkedHashMap<String, String>();

        // setup some variables to locate the js files
        String componentRoot = "component://images/webapp";
        String jqueryUiLocaleRelPath = "/images/jquery/ui/i18n/";
        String dateJsLocaleRelPath = "/images/jquery/plugins/datejs/";
        String validateRelPath = "/images/jquery/plugins/validate/localization/";
        String dateTimePickerJsLocaleRelPath = "/images/jquery/plugins/datetimepicker/localization/";
        String jsFilePostFix = ".js";
        String dateJsLocalePrefix = "date-";
        String validateLocalePrefix = "messages_";
        //String validateMethLocalePrefix = "methods__";
        String jqueryUiLocalePrefix = "jquery.ui.datepicker-";
        String dateTimePickerPrefix = "jquery-ui-timepicker-";
        String defaultLocaleDateJs = "en-US";
        String defaultLocaleJquery = "en"; // Beware to keep the OFBiz specific jquery.ui.datepicker-en.js file when upgrading...

        for (Locale locale : localeList) {
            String displayCountry = locale.toString();
            String modifiedDisplayCountry = null;
            String modifiedDisplayCountryForValidation = null;
            if (displayCountry.contains("_")) {
                modifiedDisplayCountry = displayCountry.replace("_", "-");
                modifiedDisplayCountryForValidation = displayCountry; // messages*.js use "_" not "-" as others
            } else {
                modifiedDisplayCountry = displayCountry;
            }

            String strippedLocale = locale.getLanguage();

            File file = null;
            String fileUrl = null;

            /*
             * Try to open the date-js language file
             */
            String fileName = componentRoot + dateJsLocaleRelPath + dateJsLocalePrefix + modifiedDisplayCountry + jsFilePostFix;
            file = FileUtil.getFile(fileName);

            if (file.exists()) {
                fileUrl = dateJsLocaleRelPath + dateJsLocalePrefix + modifiedDisplayCountry + jsFilePostFix;
            } else {
                // Try to guess a language
                String tmpLocale = strippedLocale + "-" + strippedLocale.toUpperCase();
                fileName = componentRoot + dateJsLocaleRelPath + dateJsLocalePrefix + tmpLocale + jsFilePostFix;
                file = FileUtil.getFile(fileName);
                if (file.exists()) {
                    fileUrl = dateJsLocaleRelPath + dateJsLocalePrefix + tmpLocale + jsFilePostFix;
                } else {
                    // use language en-US as fallback
                    fileUrl = dateJsLocaleRelPath + dateJsLocalePrefix + defaultLocaleDateJs + jsFilePostFix;
                }
            }
            dateJsLocaleFile.put(displayCountry, fileUrl);

            /*
             * Try to open the jquery validation language file
             */
            if (modifiedDisplayCountryForValidation != null) { // Try 1st lang_country
                fileName = componentRoot + validateRelPath + validateLocalePrefix + modifiedDisplayCountryForValidation + jsFilePostFix;
                file = FileUtil.getFile(fileName);
                if (file.exists()) {
                    fileUrl = validateRelPath + validateLocalePrefix + modifiedDisplayCountryForValidation + jsFilePostFix;
                } else { // lang only
                    fileName = componentRoot + validateRelPath + validateLocalePrefix + strippedLocale + jsFilePostFix;
                    file = FileUtil.getFile(fileName);
                    if (file.exists()) {
                        fileUrl = validateRelPath + validateLocalePrefix + strippedLocale + jsFilePostFix;
                    } else {
                        // use default language en as fallback
                        fileUrl = validateRelPath + validateLocalePrefix + defaultLocaleJquery + jsFilePostFix;
                    }
                }
            } else { // Then try lang only
                fileName = componentRoot + validateRelPath + validateLocalePrefix + strippedLocale + jsFilePostFix;
                file = FileUtil.getFile(fileName);
                if (file.exists()) {
                    fileUrl = validateRelPath + validateLocalePrefix + strippedLocale + jsFilePostFix;
                } else {
                    // use default language en as fallback
                    fileUrl = validateRelPath + validateLocalePrefix + defaultLocaleJquery + jsFilePostFix;
                }
            }
            validationLocaleFile.put(displayCountry, fileUrl);

            /*
             * Try to open the jquery timepicker language file
             */
            fileName = componentRoot + jqueryUiLocaleRelPath + jqueryUiLocalePrefix + strippedLocale + jsFilePostFix;
            file = FileUtil.getFile(fileName);

            if (file.exists()) {
                fileUrl = jqueryUiLocaleRelPath + jqueryUiLocalePrefix + strippedLocale + jsFilePostFix;
            } else {
                // Try to guess a language
                fileName = componentRoot + jqueryUiLocaleRelPath + jqueryUiLocalePrefix + modifiedDisplayCountry + jsFilePostFix;
                file = FileUtil.getFile(fileName);
                if (file.exists()) {
                    fileUrl = jqueryUiLocaleRelPath + jqueryUiLocalePrefix + modifiedDisplayCountry + jsFilePostFix;
                } else {
                    // use default language en as fallback
                    fileUrl = jqueryUiLocaleRelPath + jqueryUiLocalePrefix + defaultLocaleJquery + jsFilePostFix;
                }
            }
            jQueryLocaleFile.put(displayCountry, fileUrl);

            /*
             * Try to open the datetimepicker language file
             */
            fileName = componentRoot + dateTimePickerJsLocaleRelPath + dateTimePickerPrefix + strippedLocale + jsFilePostFix;
            file = FileUtil.getFile(fileName);

            if (file.exists()) {
                fileUrl = dateTimePickerJsLocaleRelPath + dateTimePickerPrefix + strippedLocale + jsFilePostFix;
            } else {
                // Try to guess a language
                fileName = componentRoot + dateTimePickerJsLocaleRelPath + dateTimePickerPrefix + modifiedDisplayCountry + jsFilePostFix;
                file = FileUtil.getFile(fileName);
                if (file.exists()) {
                    fileUrl = dateTimePickerJsLocaleRelPath + dateTimePickerPrefix + modifiedDisplayCountry + jsFilePostFix;
                } else {
                    // use default language en as fallback
                    fileUrl = dateTimePickerJsLocaleRelPath + dateTimePickerPrefix + defaultLocaleJquery + jsFilePostFix;
                }
            }
            dateTimePickerLocaleFile.put(displayCountry, fileUrl);
        }

        // check the template file
        String template = "framework/common/template/JsLanguageFilesMapping.ftl";
        String output = "framework/common/src/org/ofbiz/common/JsLanguageFilesMapping.java";
        Map<String, Object> mapWrapper = new HashMap<String, Object>();
        mapWrapper.put("datejs", dateJsLocaleFile);
        mapWrapper.put("jquery", jQueryLocaleFile);
        mapWrapper.put("validation", validationLocaleFile);
        mapWrapper.put("dateTime", dateTimePickerLocaleFile);

        // some magic to create a new java file: render it as FTL
        Writer writer = new StringWriter();
        try {
            FreeMarkerWorker.renderTemplateAtLocation(template, mapWrapper, writer);
            // write it as a Java file
            File file = new File(output);
            FileUtils.writeStringToFile(file, writer.toString(), encoding);
        }
        catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("The Outputfile could not be created: " + e.getMessage());
        }

        return result;
    }

}
