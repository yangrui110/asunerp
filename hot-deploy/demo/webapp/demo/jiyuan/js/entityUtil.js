/**
 * 对于此类整体的说明
 * 创建一个符合要求的JSON实体对象，满足后台的查询
 * 参数详解：
 * @param entityName 实体名，必填，例：ActivesView
 * @param viewIndex 分页查询时候的页码数，例2
 * @param viewSize 分页查询时每页展示的条数
 * @param noConditionFind 选择使用值：'Y' 'N'， 条件为空时是否查询
 * @param hasTimestamp 选择使用值： 'Y' 'N'， 返回的数据是否带时间戳
 * @param condition 查询条件 例如：{conditionList: [{lhs: 'projectId', operator: EntityOperator.EQUALS, rhs: this.prjId}], operator: EntityOperator.AND},为空时填{}
 * @param orderBy 排序 list形式,为空就填[]
 * @param fieldFormat 对应map集合,用来格式化字段，例如：{visitDateTime: {format: 'yyyy-MM-dd HH:mm:ss.SSS'}}。为空时填写null
 * */
var EntityUtil = (function () {
    return {
        createEntity:function(entityName,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:{},orderBy:null,fieldFormat:null};
        },
        createEntityWithCondition: function(entityName,condition,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:condition,orderBy:null,fieldFormat:null};
        },
        createEntityWithConditionOrderBy: function(entityName,condition,orderBy,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:condition,orderBy:orderBy,fieldFormat:null};
        },
        createEntityWithConditionSelectField: function(entityName,condition,selectField,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:condition,orderBy:null,fieldFormat:null};
        },
        createEntityWithConditionSelectFieldOrderBy: function(entityName,condition,selectField,ordeBy,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:condition,orderBy:ordeBy,fieldFormat:null};
        },
        createEntityWithConditionSelectFieldOrderByFieldFormat: function(entityName,condition,selectField,ordeBy,fieldFormat,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:condition,orderBy:ordeBy,fieldFormat:fieldFormat};
        },
        createEntityWithSelectFiled: function(entityName,selectField,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:{},orderBy:null,fieldFormat:null};
        },
        createEntityWithSelectFiledOrderBy: function(entityName,selectField,orderBy,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:{},orderBy:orderBy,fieldFormat:null};
        },
        createEntityWithSelectFiledOrderByFieldFormat: function(entityName,selectField,orderBy,fieldFormat,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:selectField,condition:{},orderBy:orderBy,fieldFormat:fieldFormat};
        },
        createEntityWithOrderBy: function(entityName,orderBy,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:{},orderBy:orderBy,fieldFormat:null};
        },
        createEntityWithOrderByFileldFormat: function(entityName,orderBy,fieldFormat,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:{},orderBy:orderBy,fieldFormat:fieldFormat};
        },
        createEntityWithFileldFormat: function(entityName,fieldFormat,page,size){
            return { entityName:entityName,viewIndex:page,viewSize:size,noConditionFind:'Y',hasTimestamp:'N',selectField:null,condition:{},orderBy:null,fieldFormat:fieldFormat};
        },
    }
})();

var Operator =(function(){

    return {
        EQUALS:'EQUALS',
        NOT_EQUAL:'NOT_EQUAL',
        LESS_THAN:'LESS_THAN',
        GREATER_THAN:'GREATER_THAN',
        LESS_THAN_EQUAL_TO:'LESS_THAN_EQUAL_TO',
        GREATER_THAN_EQUAL_TO:'GREATER_THAN_EQUAL_TO',
        IN:'IN',
        BETWEEN:'BETWEEN',
        NOT:'NOT',
        AND:'AND',
        OR:'OR',
        LIKE:'LIKE',
        NOT_LIKE:'NOT_LIKE',
        NOT_IN:'NOT_IN',
    }

})();