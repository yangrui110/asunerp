import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.hanlin.fadp.petrescence.datasource.EntityModelWorker;


def response=(HttpServletResponse)response
response.setContentType("application/json")
def out=response.getOutputStream()
out.withWriter {writer->
//	writer.print JSONObject.fromObject(data).toString()
	writer.print JSONArray.fromObject(EntityModelWorker.readEntityModelFromXML(parameters.dataSourceName,parameters.entityName)["field-name"]).toString()
}
