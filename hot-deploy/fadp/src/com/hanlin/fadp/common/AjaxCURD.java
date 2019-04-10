package com.hanlin.fadp.common;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.entity.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilCodec;
import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.condition.EntityComparisonOperator;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityJoinOperator;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityListIterator;
import com.hanlin.fadp.entity.util.EntityQuery;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AjaxCURD {
    public static String module = AjaxCURD.class.getName();


    /**
     * 该api用于查询数据
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      viewIndex: int 分页页号
     *      viewSize: int 每页展示数据条数
     *      noConditionFind: String，选择使用值：'Y' 'N'， 条件为空时是否查询
     *      hasTimestamp: String，选择使用值： 'Y' 'N'， 返回的数据是否带时间戳
     *      condition: Map 查询条件。例如：{conditionList: [{lhs: 'projectId', operator: EntityOperator.EQUALS, rhs: this.prjId}], operator: EntityOperator.AND}
     *      selectField: List 选取字段
     *      orderBy: List, 排序字段。 例如 ['-visitDateTime']
     *      fieldFormat: Map 字段格式化。  例如 {visitDateTime: {format: 'yyyy-MM-dd HH:mm:ss.SSS'}}
     * }
     * api接口返回数据如下
     * {
     *     listSize: int 数据条数
     *     list：List<Map> 数据
     * }
     *
     */
    public static String getPageData(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);
        AjaxUtil.writeJsonToResponse(findListByJsonCondition(delegator, attrMap), response);
        return "success";
    }

    /**
     * 该api用户根据数据的主键查询出唯一一条数据
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      PK:Map 主键。例如：{id:10010}
     * }
     * api接口直接返回实体数据。
     *
     */
    @SuppressWarnings("unchecked")
    public static String getOne(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);
        String entityName = (String) attrMap.get("entityName");
        GenericPK pk = parsePK(delegator, entityName, (Map<String, Object>) attrMap.get("PK"));
        if (UtilValidate.isNotEmpty(pk)) {
            GenericValue one = EntityQuery.use(delegator).from(entityName).where(pk).queryOne();
            AjaxUtil.writeJsonToResponse(one, response);
        } else {
            AjaxUtil.writeErrorJsonToResponse("数据不存在", response);
        }

        return "success";
    }
    /**
     * 该api用户根据数据的条件查询出数据
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      condition:Map 查询条件，与getPageData接口的条件格式相同
     * }
     * api接口直接返回实体数据。
     *
     */
    public static String getFirst(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Delegator delegator = getDelegator(parameters, request);
        String entityName = (String) parameters.get("entityName");
        ParseMapCondition parser = new ParseMapCondition(delegator, entityName);

        EntityCondition condition = parser.parseToCondition((Map<String, Object>) parameters.get("condition"));
        if (UtilValidate.isNotEmpty(condition)) {
            GenericValue one = EntityQuery.use(delegator).from(entityName).where(condition).queryFirst();
            AjaxUtil.writeJsonToResponse(one, response);
        } else {
            AjaxUtil.writeErrorJsonToResponse("查询条件不可为空", response);
        }

        return "success";
    }

    /**
     * 通用数据保存方法，包含新增、修改
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      fieldMap:Map 字段map
     *      autoPK:boolean  是否主键自增长
     *      autoPKFieldName:String 需要自增长的字段，（若该字段为空，则默认主键自增长）
     * }
     * api接口返回数据如下
     * {
     *     value:Map 保存的实体数据
     * }
     */
    public static String genericSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);
        String error = null;
        GenericValue value = null;
        try {
            value = save(delegator, attrMap);
            if (value == null) {
                error = "保存失败";
            }
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
            // e.printStackTrace();
        }
        Map<String, Object> json = UtilMisc.toMap();
        if (error != null) {
            json.put("errorMsg", error);
        } else {
            json.put("value", value);
        }
        AjaxUtil.writeJsonToResponse(json, response);
        return "success";
    }

    /**
     * 通用数据批量保存方法，包含新增、修改
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      fieldMap:Map 字段map
     *      valueList:List 实体列表
     *      autoPK:boolean  是否主键自增长
     *      autoPKFieldName:String 需要自增长的字段，（若该字段为空，则默认主键自增长）
     * }
     * api接口返回空map
     */
    public static String genericSaveAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);
        String error = null;
        try {
            saveAll(delegator, attrMap);
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
            // e.printStackTrace();
        }
        Map<String, Object> json = UtilMisc.toMap();
        if (error != null) {
            json.put("errorMsg", error);
        }
        AjaxUtil.writeJsonToResponse(json, response);
        return "success";
    }

    /**
     * 通用数据删除方法，可批量删除
     * api接口提交数据如下：
     * {
     *      entityName:String 实体名
     *      PK:List 实体主键列表，多个主键
     * }
     * api接口返回空map
     */
    public static String genericDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);

        String error = null;
        try {
            int i = delete(delegator, attrMap);
            List<Map<String, Object>> PKList = (List<Map<String, Object>>) attrMap.get("PK");

            if (i != PKList.size()) {
                error = "部分实体删除失败";
            }
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
        }
        Map<String, Object> json = UtilMisc.toMap();
        if (error != null) {
            json.put("errorMsg", error);
        }
        AjaxUtil.writeJsonToResponse(json, response);
        return "success";
    }


    public static List<Map<String, Object>> getEntityFieldInfoList(Delegator delegator, Map<String, Object> parameters) {
        String entityName = (String) parameters.get("entityName");
        ModelEntity modelEntity = delegator.getModelEntity(entityName);
        HashSet<String> timeStampSet = new HashSet<>(modelEntity.getAutomaticFieldNames());
        List<ModelField> list = modelEntity.getFieldsUnmodifiable();
        List<Map<String, Object>> fieldInfoList = FastList.newInstance();
        for (ModelField modelField : list) {
            if (timeStampSet.contains(modelField.getName()) || modelField.getType().equals("object")) {
                continue;
            }
            Map<String, Object> map = FastMap.newInstance();
            fieldInfoList.add(map);
            map.put("fieldName", modelField.getName());
            map.put("isPK", modelField.getIsPk());

            String fieldDescription = modelField.getDescription();
            if (UtilValidate.isNotEmpty(fieldDescription) && fieldDescription.indexOf(" ") < 0 && fieldDescription.length() < 20) {
                map.put("fieldText", modelField.getDescription());

            } else {

                map.put("fieldText", modelField.getName());
            }
            map.put("fieldType", modelField.getType());
        }
        return fieldInfoList;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> findList(Delegator delegator, Map<String, Object> parameters) throws GenericEntityException {
        Set<String> selectField = UtilMisc.collectionToSet((Collection<String>) parameters.get("selectField"));
        String entityName = UtilGenerics.cast(parameters.get("entityName"));
        String hasTimestamp = UtilGenerics.cast(parameters.get("hasTimestamp"));
        if (selectField == null) {
            selectField = new HashSet<String>();
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            Iterator<ModelField> it = modelEntity.getFieldsIterator();

            while (it.hasNext()) {
                ModelField modelField = it.next();
                if (!UtilValidate.areEqual(modelField.getType(), "object")) {
                    if ("N".equals(hasTimestamp) && modelEntity.getAutomaticFieldNames().contains(modelField.getName())) {
                        //不允许查询出时间戳
                    } else {
                        selectField.add(modelField.getName());
                    }

                }
            }
        }

        boolean distinct = false;
        EntityCondition condition = (EntityCondition) parameters.get("condition");
        List<String> orderBy = (List<String>) parameters.get("orderBy");
        //去重
        if (parameters.get("distinct") != null) {
            if (parameters.get("distinct").equals("true") || (boolean) parameters.get("distinct")) {
                distinct = true;
            }
        }

        int viewIndex = UtilMisc.toInteger(parameters.get("viewIndex"));
        int viewSize = UtilMisc.toInteger(parameters.get("viewSize"));
        if (viewSize==0) {
            viewSize=100;
        }
        int start = viewIndex * viewSize;
        int maxRows = viewSize * (viewIndex + 1);
        boolean begin = TransactionUtil.begin();
        EntityListIterator iterator = EntityQuery.use(delegator).select(selectField).from(entityName).where(condition).orderBy(orderBy).cursorScrollInsensitive().maxRows(maxRows).distinct(distinct).queryIterator();

        List<GenericValue> list = iterator.getPartialList(start + 1, viewSize);
        List<Map<String, Object>> list2 = null;
        Map<String, Map<String, String>> fieldFormat = (Map<String, Map<String, String>>) parameters.get("fieldFormat");
        if (fieldFormat != null) {
            list2 = FastList.newInstance();
            for (GenericValue genericValue : list) {
                Map<String, Object> map = FastMap.newInstance();
                list2.add(map);
                map.putAll(genericValue);

                for (Entry<String, Map<String, String>> entry : fieldFormat.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = genericValue.get(fieldName);
                    String format = entry.getValue().get("format");
                    if (fieldValue == null) {
                        map.put(fieldName, "");
                        continue;
                    } else if (fieldValue instanceof Timestamp) {
                        fieldValue = UtilDateTime.toDateString(new Date(((Timestamp) fieldValue).getTime()), format);
                    } else if (fieldValue instanceof java.sql.Date) {
                        fieldValue = UtilDateTime.toDateString(new Date(((java.sql.Date) fieldValue).getTime()), format);
                    } else if (fieldValue instanceof java.sql.Time) {
                        fieldValue = UtilDateTime.toDateString(new Date(((java.sql.Time) fieldValue).getTime()), format);
                    } else if (fieldValue instanceof String) {
                        fieldValue = new UtilCodec.HtmlEncoder().encode(fieldValue.toString());
                    }
                    map.put(fieldName, fieldValue);
                }
            }
        }
        int listSize = iterator.getResultsSizeAfterPartialList();
        iterator.close();
        TransactionUtil.commit(begin);
        FastMap<String, Object> result = FastMap.newInstance();
        result.put("listSize", listSize);
        if (list2 != null) {
            result.put("list", list2);
        } else {
            result.put("list", list);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> findListByJsonCondition(Delegator delegator, Map<String, Object> parameters) throws GenericEntityException {
        ParseMapCondition parser = new ParseMapCondition(delegator, (String) parameters.get("entityName"));
        EntityCondition condition = parser.parseToCondition((Map<String, Object>) parameters.get("condition"));
        Debug.logInfo("getPageData的查询条件" + condition, module);
        parameters.put("condition", condition);
        return findList(delegator, parameters);
    }

    public static class ParseMapCondition {
        Delegator delegator;
        String entityName;
        List<ModelField> fieldList;

        public ParseMapCondition(Delegator delegator, String entityName) {
            this.delegator = delegator;
            this.entityName = entityName;
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            this.fieldList = modelEntity.getFieldsUnmodifiable();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public EntityCondition parseToCondition(Map<String, Object> map) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("conditionList");
            EntityOperator operator = EntityOperator.lookup((String) map.get("operator"));
            if (list != null) {
                List<EntityCondition> conditionList = FastList.newInstance();
                for (Map<String, Object> childMap : list) {
                    EntityCondition condition = parseToCondition(childMap);
                    if (condition != null) {
                        conditionList.add(condition);
                    }
                }
                return EntityCondition.makeCondition(conditionList, (EntityJoinOperator) operator);
            } else {
                String lhs = (String) map.get("lhs");
                Object rhs = map.get("rhs");
                if (UtilValidate.isEmpty(lhs)) {
                    return null;
                }
                if (UtilValidate.isEmpty(rhs)) {
                    // rhs = "%";
                    return null;
                }


                EntityCondition condition = createSingleCondition(delegator, entityName, lhs, operator, rhs);
                return condition;
            }
        }

        private EntityCondition createSingleCondition(Delegator delegator, String entityName, String lhs, EntityOperator operator, Object rhs) {
            if (!EntityOperator.IN.equals(operator) && !EntityOperator.NOT_IN.equals(operator)) {
                rhs = correctFieldValue(delegator, entityName, lhs, rhs);
            }
            return EntityCondition.makeCondition(lhs, (EntityComparisonOperator) operator, rhs);
        }
    }

    @SuppressWarnings("unchecked")
    public static GenericValue create(Delegator delegator, Map<String, Object> parameters) throws GenericEntityException {
        GenericValue value = parseValue(delegator, (String) parameters.get("entityName"), (Map<String, Object>) parameters.get("fieldMap"));
        Boolean autoPK = (Boolean) parameters.get("autoPK");
        if (autoPK != null && autoPK == true) {
            String autoPKFieldName = (String) parameters.get("autoPKFieldName");
            if (UtilValidate.isEmpty(autoPKFieldName)) {
                // 主键字段名为空，者使用数据库主键字段
                autoPKFieldName = value.getModelEntity().getPkFieldNames().get(0);
            }
            // 虽然设置了自增长属性，但是如果提交了主键字段，还是以提交值为准,所以这里先检查提交的参数。
            Object pkValue = value.get(autoPKFieldName);
            if (UtilValidate.isEmpty(pkValue)) {
                pkValue = value.getDelegator().getNextSeqId(value.getEntityName());
            }
            value.setString(autoPKFieldName, String.valueOf(pkValue));
        }
        GenericValue createdValue = delegator.create(value);
        return createdValue;
        // return value;
    }

    public static List<GenericValue> saveAll(Delegator delegator, Map<String, Object> parameters) throws Exception {
        String entityName = (String) parameters.get("entityName");
        List<Map<String, Object>> valueList = (List<Map<String, Object>>) parameters.get("valueList");
        // 批量修改
        List<GenericValue> resultList = FastList.newInstance();
        for (Map<String, Object> map : valueList) {
            parameters.put("fieldMap", map);
            GenericValue value = save(delegator, parameters);
            resultList.add(value);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public static GenericValue save(Delegator delegator, Map<String, Object> parameters) throws Exception {
        String entityName = (String) parameters.get("entityName");
        Map<String, Object> fieldMap = (Map<String, Object>) parameters.get("fieldMap");
        GenericValue value = parseValue(delegator, entityName, fieldMap);
        boolean isPKComplate = true;
        for (Object v : value.getPrimaryKey().values()) {
            if (v == null) {
                isPKComplate = false;
                break;
            }
        }
        GenericValue oldValue = null;
        if (isPKComplate) {
            //
            oldValue = EntityQuery.use(delegator).from(entityName).where(value.getPrimaryKey()).queryOne();

        }
        if (oldValue == null) {
            return create(delegator, parameters);
        } else {
            delegator.store(value);
            return value;
        }

    }

    @SuppressWarnings("unchecked")
    public static int delete(Delegator delegator, Map<String, Object> parameters) throws Exception {
        String entityName = (String) parameters.get("entityName");
        List<Map<String, Object>> PKList = (List<Map<String, Object>>) parameters.get("PK");
        if (PKList == null || PKList.size() == 0) {
            throw new Exception("请选择");
        }
        int i = 0;
        for (Map<String, Object> PKMap : PKList) {
            GenericPK PK = parsePK(delegator, entityName, PKMap);
            if (PK != null) {
                delegator.removeByPrimaryKey(PK);
                i++;
            }
        }
        return i;
    }

    public static GenericPK parsePK(Delegator delegator, String entityName, Map<String, Object> PKMap) throws GenericEntityException {
        GenericValue PK = parseValue(delegator, entityName, PKMap);
        if (PK.size() == 0) {
            return null;
        }
        return PK.getPrimaryKey();
        //FixMe:这里需要确定pk是不是对的。
//		List<GenericValue> list = EntityQuery.use(delegator).from(entityName).where(PK).select(PK.keySet().iterator().next()).queryList();
//		if (list == null || list.size() != 1) {
//			return null;
//		}
//		return PK;
    }

    private static Object correctFieldValue(Delegator delegator, String entityName, String lhs, Object rhs) {
        GenericValue value = delegator.makeValue(entityName);
        if ("null".equals(rhs) || "[null-field]".equals(rhs)) { // keep [null-field] but it'not used now
            value.set(lhs, null);

        } else {
            if (rhs instanceof Integer) {
                rhs = String.valueOf(rhs);
            }
            if (rhs instanceof String) {
                value.setString(lhs, (String) rhs);
            } else {
                value.set(lhs, rhs);
            }
        }
        return value.get(lhs);

    }

    public static GenericValue parseValue(Delegator delegator, String entityName, Map<String, Object> fieldMap) {

        GenericValue value = delegator.makeValue(entityName);
        if (fieldMap == null) {
            return value;
        }
        for (Map.Entry<String, Object> kv : fieldMap.entrySet()) {
            Object v = kv.getValue();
            String k = kv.getKey();

            if (UtilValidate.isEmpty(v) || !value.getModelEntity().getAllFieldNames().contains(k)) {
                continue;
            }
            if (v instanceof String) {
                try {
                    value.set(k, correctFieldValue(delegator, entityName, k, v));
                } catch (Exception e) {
                    continue;
                }
            } else if (v instanceof Integer
                    || v instanceof Double) {
                value.setString(k, v + "");
            } else {
                value.put(k, v);
            }
        }
        return value;
    }

    public static String getFieldInfoList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = getDelegator(attrMap, request);
        AjaxUtil.writeJsonToResponse(getEntityFieldInfoList(delegator, attrMap), response);
        return "success";
    }


    private static final UtilCache<String, Map<String, Object>> fileCache = UtilCache.createUtilCache("AjaxCURD.fileCache", 0, 5000);

    @SuppressWarnings("unchecked")
    public static String exportCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getMethod().equalsIgnoreCase("post")) {
            Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
            String id = UUID.randomUUID().toString();
            fileCache.put(id, UtilMisc.toMap("fileName", attrMap.get("fileName"), "list", attrMap.get("list")));
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("id", id), response);
        } else {
            String id = request.getParameter("id");
            Map<String, Object> map = fileCache.get(id);
            if (map != null) {
                List<List<Object>> list = (List<List<Object>>) map.get("list");
                response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8");
                Workbook book = new XSSFWorkbook();
                Sheet sheet = book.createSheet();

                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(map.get("fileName").toString(), "UTF-8"));
                for (int i = 0; i < list.size(); i++) {
                    Row row = sheet.createRow(i);
                    List<Object> list2 = list.get(i);
                    for (int j = 0; j < list2.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(list2.get(j) + "");
                    }
                }
                ServletOutputStream out = response.getOutputStream();
                book.write(out);
                out.flush();
                out.close();
            }
        }
        return "success";
    }

    private static Delegator getDelegator(Map<String, Object> attrMap, HttpServletRequest request) {
        String delegatorName = (String) attrMap.get("delegatorName");
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = (String) attrMap.get("dataSourceName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = request.getParameter("delegatorName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = request.getParameter("dataSourceName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            return (Delegator) attrMap.get("delegator");
        }
        return DelegatorFactory.getDelegator(delegatorName);

    }

}
