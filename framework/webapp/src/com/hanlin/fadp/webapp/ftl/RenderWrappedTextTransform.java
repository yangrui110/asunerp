
package com.hanlin.fadp.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateTransformModel;

import com.hanlin.fadp.base.util.Debug;
import static com.hanlin.fadp.base.util.UtilGenerics.checkMap;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.template.FreeMarkerWorker;

/**
 * RenderWrappedTextTransform - Freemarker Transform for URLs (links)
 */
public class RenderWrappedTextTransform implements  TemplateTransformModel {

    public static final String module = RenderWrappedTextTransform.class.getName();

    public Writer getWriter(final Writer out, Map args) {
        final Environment env = Environment.getCurrentEnvironment();
        Map<String, Object> ctx = checkMap(FreeMarkerWorker.getWrappedObject("context", env), String.class, Object.class);
        final String wrappedFTL = FreeMarkerWorker.getArg(checkMap(args, String.class, Object.class), "wrappedFTL", ctx);

        return new Writer(out) {

            @Override
            public void write(char cbuf[], int off, int len) {
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void close() throws IOException {
                if (UtilValidate.isNotEmpty(wrappedFTL)) {
                        out.write(wrappedFTL);
                } else {
                    Debug.logInfo("wrappedFTL was empty. skipping write.", module);
                }
            }
        };
    }
}
