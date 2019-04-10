

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.transaction.*;

import javax.jms.Session;

import net.sf.json.*;

response.setHeader("Content-type", "text/csv;charset=UTF-8");
response.setCharacterEncoding("UTF-8");
response.setHeader("Content-Disposition", "attachment;filename=" + parameters.entityName + ".csv");

def outputStream = response.getOutputStream();


def field = parameters.field;
def entityName = parameters.entityName;

JSONObject json = null;
def jsonData = parameters._json;
if(jsonData){
	json = JSONObject.fromObject(jsonData);
	session.setAttribute('json', json);
}else{
	json = JSONObject.fromObject(session.getAttribute('json'));
}

if(field){
	session.setAttribute('field', field);
}else{
	field = session.getAttribute('field');
}


for(def i=0; i<json.size(); i++){
	println json.get(""+i);
}


Debug.log("csvExport :" + entityName);




//
//def tempField = "";
//for(def i=0; i<field.size(); i++){
//	if(field[i] != "#")
//		tempField += field[i] + ",";
//}
//tempField = subLastDel(tempField);


if(parameters.isTrue){
	
	if(json.size() > 0){
		StringBuffer sb=new StringBuffer();
		beganTransaction = TransactionUtil.begin(3600);
		try{
		
			sb.append(field.replace("#", ","));
			sb.append("\n");
		
		
			for(def i=0; i<json.size(); i++){
			
				sb.append(subLastDel(json.get(""+i).replace("#", ",")));
				
				sb.append("\n");
			}
			
			println sb.toString();
			TransactionUtil.commit(beganTransaction);
			
		}catch(Exception e){
			e.printStackTrace();
			errMsg = "Error reading data for CSV export:";
			TransactionUtil.rollback(beganTransaction, errMsg, e);
		}
		
//		PrintWriter printwriter = new PrintWriter(outputStream);
//		printwriter.write(sb.toString(), 0, sb.toString().length());
//		printwriter.close();
		outputStream.write(sb.toString().getBytes("gb2312"));
		session.removeAttribute("json");
		session.removeAttribute("field");
	}else{
	
	}
	
}
	

def subLastDel(data){
	return data.substring(0, data.lastIndexOf(","));
}

return "success";