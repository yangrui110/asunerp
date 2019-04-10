import javax.servlet.http.HttpServletResponse

import org.apache.commons.lang.StringEscapeUtils

import com.hanlin.fadp.entity.model.ModelUtil;

import groovy.sql.*;
def response=response as HttpServletResponse;
response.setContentType("text/xml")
def writer=response.writer;
def gs=Sql.newInstance("jdbc:mysql://win7:3307/information_schema",'root',"root");


def xmlDoc = new groovy.xml.StreamingMarkupBuilder().bind {
	escapeAttributes=false;
	mkp.pi(xml: "version='1.0'  encoding='UTF-8'");
	entitymodel {
		gs.eachRow("select * from `TABLES` where TABLE_SCHEMA='通达oa流程'"){tableRow->
			def tableName=ModelUtil.dbNameToClassName(tableRow.TABLE_NAME);
			entity('entity-name':tableName,'package-name':''){
				gs.eachRow("select * from `COLUMNS` t where TABLE_NAME='${tableRow.TABLE_NAME}'"){ row->
					def columnName=ModelUtil.dbNameToVarName(row.COLUMN_NAME)
					def fieldType=ModelUtil.dbNameToVarName(row.DATA_TYPE)

					def length=row.CHARACTER_MAXIMUM_LENGTH?:row.NUMERIC_SCALE?:0
					def precision=row.NUMERIC_PRECISION?:0;
					def javaFieldType=ModelUtil.induceFieldType(fieldType,length,precision,null);
					field(name:columnName,type:javaFieldType){ description(row.COLUMN_COMMENT) }
				}
			}
		}
	}
}
def xmlStr=StringEscapeUtils.unescapeXml(xmlDoc.toString());
writer.print(xmlStr)
print(xmlStr)
return "success"
/*select t.COLUMN_COMMENT , t.COLUMN_NAME ,t.COLUMN_TYPE ,t.COLUMN_DEFAULT from `COLUMNS` t where TABLE_NAME='flow_type';
 select * from `TABLES` where TABLE_SCHEMA='td_oa'*/