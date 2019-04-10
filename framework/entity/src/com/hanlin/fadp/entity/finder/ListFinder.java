
package com.hanlin.fadp.entity.finder;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.GetAll;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.LimitRange;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.LimitView;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.OutputHandler;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.UseIterator;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelFieldTypeReader;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.util.EntityListIterator;
import com.hanlin.fadp.entity.util.EntityUtil;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a and
 *
 */
@SuppressWarnings("serial")
public abstract class ListFinder extends Finder {
    public static final String module = ListFinder.class.getName();

    protected String label;

    protected FlexibleStringExpander filterByDateStrExdr;
    protected FlexibleStringExpander distinctStrExdr;
    protected FlexibleStringExpander delegatorNameExdr;
    protected FlexibleMapAccessor<Object> listAcsr;
    protected FlexibleStringExpander resultSetTypeExdr;

    protected List<FlexibleStringExpander> selectFieldExpanderList;
    protected List<FlexibleStringExpander> orderByExpanderList;
    protected OutputHandler outputHandler;

    protected ListFinder(Element element, String label) {
        super(element);
        this.label = label;

        this.filterByDateStrExdr = FlexibleStringExpander.getInstance(element.getAttribute("filter-by-date"));
        this.distinctStrExdr = FlexibleStringExpander.getInstance(element.getAttribute("distinct"));
        this.delegatorNameExdr = FlexibleStringExpander.getInstance(element.getAttribute("delegator-name"));
        if (UtilValidate.isNotEmpty(element.getAttribute("list"))) {
            this.listAcsr = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        } else {
            this.listAcsr = FlexibleMapAccessor.getInstance(element.getAttribute("list-name"));
        }
        this.resultSetTypeExdr = FlexibleStringExpander.getInstance(element.getAttribute("result-set-type"));

        // process select-field
        selectFieldExpanderList = EntityFinderUtil.makeSelectFieldExpanderList(element);

        // process order-by
        List<? extends Element> orderByElementList = UtilXml.childElementList(element, "order-by");
        if (orderByElementList.size() > 0) {
            orderByExpanderList = new ArrayList<FlexibleStringExpander>(orderByElementList.size());
            for (Element orderByElement: orderByElementList) {
                orderByExpanderList.add(FlexibleStringExpander.getInstance(orderByElement.getAttribute("field-name")));
            }
        }

        // process limit-range | limit-view | use-iterator
        Element limitRangeElement = UtilXml.firstChildElement(element, "limit-range");
        Element limitViewElement = UtilXml.firstChildElement(element, "limit-view");
        Element useIteratorElement = UtilXml.firstChildElement(element, "use-iterator");
        if ((limitRangeElement != null && limitViewElement != null) || (limitRangeElement != null && useIteratorElement != null) || (limitViewElement != null && useIteratorElement != null)) {
            throw new IllegalArgumentException("In entity find by " + label + " element, cannot have more than one of the following: limit-range, limit-view, " + label + " use-iterator");
        }
        if (limitRangeElement != null) {
            outputHandler = new LimitRange(limitRangeElement);
        } else if (limitViewElement != null) {
            outputHandler = new LimitView(limitViewElement);
        } else if (useIteratorElement != null) {
            outputHandler = new UseIterator(useIteratorElement);
        } else {
            // default to get all
            outputHandler = new GetAll();
        }
    }

    @Override
    public void runFind(Map<String, Object> context, Delegator delegator) throws GeneralException {
        String entityName = this.entityNameExdr.expandString(context);
        String useCacheStr = this.useCacheStrExdr.expandString(context);
        String filterByDateStr = this.filterByDateStrExdr.expandString(context);
        String distinctStr = this.distinctStrExdr.expandString(context);
        String delegatorName = this.delegatorNameExdr.expandString(context);
        ModelEntity modelEntity = delegator.getModelEntity(entityName);
        String resultSetTypeString = this.resultSetTypeExdr.expandString(context);

        if (modelEntity == null) {
            throw new IllegalArgumentException("In find entity by " + label + " could not find definition for entity with name [" + entityName + "].");
        }

        boolean useCache = "true".equals(useCacheStr);
        boolean filterByDate = "true".equals(filterByDateStr);
        boolean distinct = "true".equals(distinctStr);
        int resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
        if ("forward".equals(resultSetTypeString))
            resultSetType = ResultSet.TYPE_FORWARD_ONLY;

        if (UtilValidate.isNotEmpty(delegatorName)) {
            delegator = DelegatorFactory.getDelegator(delegatorName);
        }

        EntityCondition whereEntityCondition = getWhereEntityCondition(context, modelEntity, delegator.getModelFieldTypeReader(modelEntity));
        EntityCondition havingEntityCondition = getHavingEntityCondition(context, modelEntity, delegator.getModelFieldTypeReader(modelEntity));
        if (useCache) {
            // if useCache == true && outputHandler instanceof UseIterator, throw exception; not a valid combination
            if (outputHandler instanceof UseIterator) {
                Debug.logWarning("In find entity by " + label + " cannot have use-cache set to true " + label + " select use-iterator for the output type. Using cache and ignoring use-iterator setting.", module);
                outputHandler = new GetAll();
            }
            if (distinct) {
                throw new IllegalArgumentException("In find entity by " + label + " cannot have use-cache set to true " + label + " set distinct to true.");
            }
            if (havingEntityCondition != null) {
                throw new IllegalArgumentException("In find entity by " + label + " cannot have use-cache set to true and specify a having-condition-list (can only use a where condition with condition-expr or condition-list).");
            }
        }


        // get the list of fieldsToSelect from selectFieldExpanderList
        Set<String> fieldsToSelect = EntityFinderUtil.makeFieldsToSelect(selectFieldExpanderList, context);

        //if fieldsToSelect != null and useCacheBool is true, throw an error
        if (fieldsToSelect != null && useCache) {
            throw new IllegalArgumentException("Error in entity query by " + label + " definition, cannot specify select-field elements when use-cache is set to true");
        }

        // get the list of orderByFields from orderByExpanderList
        List<String> orderByFields = EntityFinderUtil.makeOrderByFieldList(this.orderByExpanderList, context);

        try {
            // if filterByDate, do a date filter on the results based on the now-timestamp
            if (filterByDate && !useCache) {
                EntityCondition filterByDateCondition = EntityUtil.getFilterByDateExpr();
                if (whereEntityCondition != null) {
                    whereEntityCondition = EntityCondition.makeCondition(UtilMisc.toList(whereEntityCondition, filterByDateCondition));
                } else {
                    whereEntityCondition = filterByDateCondition;
                }
            }

            if (useCache) {
                List<GenericValue> results = delegator.findList(entityName, whereEntityCondition, fieldsToSelect, orderByFields, null, true);
                if (filterByDate) {
                    results = EntityUtil.filterByDate(results);
                }
                this.outputHandler.handleOutput(results, context, listAcsr);
            } else {
                boolean useTransaction = true;
                if (this.outputHandler instanceof UseIterator && !TransactionUtil.isTransactionInPlace()) {
                    Exception newE = new Exception("Stack Trace");
                    Debug.logError(newE, "ERROR: Cannot do a by " + label + " find that returns an EntityListIterator with no transaction in place. Wrap this call in a transaction.", module);
                    useTransaction = false;
                }

                EntityFindOptions options = new EntityFindOptions();
                options.setDistinct(distinct);
                options.setResultSetType(resultSetType);
                if (outputHandler instanceof LimitRange) {
                    LimitRange limitRange = (LimitRange) outputHandler;
                    int start = limitRange.getStart(context);
                    int size = limitRange.getSize(context);
                    options.setMaxRows(start + size);
                } else if (outputHandler instanceof LimitView) {
                    LimitView limitView = (LimitView) outputHandler;
                    int index = limitView.getIndex(context);
                    int size = limitView.getSize(context);
                    options.setMaxRows(size * (index + 1));
                }
                boolean beganTransaction = false;
                try {
                    if (useTransaction) {
                        beganTransaction = TransactionUtil.begin();
                    }

                    EntityListIterator eli = delegator.find(entityName, whereEntityCondition, havingEntityCondition, fieldsToSelect, orderByFields, options);
                    this.outputHandler.handleOutput(eli, context, listAcsr);
                } catch (GenericEntityException e) {
                    String errMsg = "Failure in by " + label + " find operation, rolling back transaction";
                    Debug.logError(e, errMsg, module);
                    try {
                        // only rollback the transaction if we started one...
                        TransactionUtil.rollback(beganTransaction, errMsg, e);
                    } catch (GenericEntityException e2) {
                        Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
                    }
                    // after rolling back, rethrow the exception
                    throw e;
                } finally {
                    // only commit the transaction if we started one... this will throw an exception if it fails
                    TransactionUtil.commit(beganTransaction);
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error doing find by " + label + ": " + e.toString();
            Debug.logError(e, module);
            throw new GeneralException(errMsg, e);
        }
    }

    public List<String> getOrderByFieldList(Map<String, Object> context) {
        List<String> orderByFields = EntityFinderUtil.makeOrderByFieldList(this.orderByExpanderList, context);
        return orderByFields;
    }

    public EntityCondition getWhereEntityCondition(Map<String, Object> context, ModelEntity modelEntity, ModelFieldTypeReader modelFieldTypeReader) {
        return null;
    }

    public EntityCondition getHavingEntityCondition(Map<String, Object> context, ModelEntity modelEntity, ModelFieldTypeReader modelFieldTypeReader) {
        return null;
    }
}

