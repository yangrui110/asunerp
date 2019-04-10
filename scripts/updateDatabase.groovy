import groovy.sql.Sql

/**
 * Created by cl on 2017/2/24.
 */
def sql=Sql.newInstance("jdbc:derby:runtime/data/derby/ofbiz","ofbiz","")
def updateSqlList=[]
sql.eachRow("SELECT p.project_id,r.rule_id FROM WS_PROJECT p ,WS_DOCUMENT d,WS_RULE r WHERE p.PROJECT_ID=d.PROJECT_ID AND d.RULE_ID=r.RULE_ID",){
    if(it.rule_id==~/\d*/){
        updateSqlList<<"update ws_rule set project_id=${it.project_id} where rule_id=${it.rule_id}"
    }
}
 updateSqlList.each {
     sql.executeUpdate(it)
 }
