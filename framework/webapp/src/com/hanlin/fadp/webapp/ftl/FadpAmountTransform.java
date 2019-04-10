
package com.hanlin.fadp.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.NumberModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateTransformModel;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilFormatOut;
import com.hanlin.fadp.base.util.UtilHttp;

/**
 * OfbizAmountTransform - Freemarker Transform for content links
 */
public class FadpAmountTransform implements TemplateTransformModel {

    public static final String module = FadpAmountTransform.class.getName();
    public static final String SPELLED_OUT_FORMAT = "spelled-out";

    private static String getArg(Map args, String key) {
        String  result = "";
        Object o = args.get(key);
        if (o != null) {
            if (Debug.verboseOn()) Debug.logVerbose("Arg Object : " + o.getClass().getName(), module);
            if (o instanceof TemplateScalarModel) {
                TemplateScalarModel s = (TemplateScalarModel) o;
                try {
                    result = s.getAsString();
                } catch (TemplateModelException e) {
                    Debug.logError(e, "Template Exception", module);
                }
            } else {
              result = o.toString();
            }
        }
        return result;
    }
    private static Double getAmount(Map args, String key) {
        if (args.containsKey(key)) {
            Object o = args.get(key);
            if (Debug.verboseOn()) Debug.logVerbose("Amount Object : " + o.getClass().getName(), module);

            // handle nulls better
            if (o == null) {
                o = Double.valueOf(0.00);
            }

            if (o instanceof NumberModel) {
                NumberModel s = (NumberModel) o;
                return Double.valueOf(s.getAsNumber().doubleValue());
            }
            if (o instanceof SimpleNumber) {
                SimpleNumber s = (SimpleNumber) o;
                return Double.valueOf(s.getAsNumber().doubleValue());
            }
            if (o instanceof SimpleScalar) {
                SimpleScalar s = (SimpleScalar) o;
                return Double.valueOf(s.getAsString());
            }
            return Double.valueOf(o.toString());
        }
        return Double.valueOf(0.00);
    }
    public Writer getWriter(final Writer out, Map args) {
        final StringBuilder buf = new StringBuilder();

        final Double amount = FadpAmountTransform.getAmount(args, "amount");
        final String locale = FadpAmountTransform.getArg(args, "locale");
        final String format = FadpAmountTransform.getArg(args, "format");

        return new Writer(out) {
            @Override
            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void close() throws IOException {
                try {
                    if (Debug.verboseOn()) Debug.logVerbose("parms: " + amount + " " + format + " " + locale, module);
                    Locale localeObj = null;
                    if (locale.length() < 1) {
                        // Load the locale from the session
                        Environment env = Environment.getCurrentEnvironment();
                        BeanModel req = (BeanModel) env.getVariable("request");
                        if (req != null) {
                            HttpServletRequest request = (HttpServletRequest) req.getWrappedObject();
                            localeObj = UtilHttp.getLocale(request);
                        } else {
                            localeObj = env.getLocale();
                        }
                    } else {
                        localeObj = new Locale(locale);
                    }
                    if (format.equals(FadpAmountTransform.SPELLED_OUT_FORMAT)) {
                        out.write(UtilFormatOut.formatSpelledOutAmount(amount.doubleValue(), localeObj));
                    } else {
                        out.write(UtilFormatOut.formatAmount(amount.doubleValue(), localeObj));
                    }
                } catch (TemplateModelException e) {
                    throw new IOException(e.getMessage());
                }
            }
        };
    }
}
