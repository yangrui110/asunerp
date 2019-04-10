
package com.hanlin.fadp.webapp.ftl;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.*;

import java.util.List;
import java.util.Map;

/**
 * SetRequestAttributeMethod - Freemarker Method for setting context fields
 */
public class SetContextFieldTransform implements TemplateMethodModelEx {

    public static final String module = SetContextFieldTransform.class.getName();

    /*
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    @SuppressWarnings("unchecked")
    public Object exec(List args) throws TemplateModelException {
        if (args == null || args.size() != 2)
            throw new TemplateModelException("Invalid number of arguements");
        if (!(args.get(0) instanceof TemplateScalarModel))
            throw new TemplateModelException("First argument not an instance of TemplateScalarModel");
        if (!(args.get(1) instanceof BeanModel) && !(args.get(1) instanceof TemplateNumberModel) && !(args.get(1) instanceof TemplateScalarModel))
            throw new TemplateModelException("Second argument not an instance of BeanModel nor TemplateNumberModel nor TemplateScalarModel");

        Environment env = Environment.getCurrentEnvironment();
        BeanModel req = (BeanModel)env.getVariable("context");
        Map context = (Map) req.getWrappedObject();

        String name = ((TemplateScalarModel) args.get(0)).getAsString();
        Object value = null;
        if (args.get(1) instanceof TemplateScalarModel)
            value = ((TemplateScalarModel) args.get(1)).getAsString();
        if (args.get(1) instanceof TemplateNumberModel)
            value = ((TemplateNumberModel) args.get(1)).getAsNumber();
        if (args.get(1) instanceof BeanModel)
            value = ((BeanModel) args.get(1)).getWrappedObject();

        context.put(name, value);
        return new SimpleScalar("");
    }

}
