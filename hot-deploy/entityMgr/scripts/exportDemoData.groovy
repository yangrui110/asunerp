import com.hanlin.fadp.base.util.Base64
import com.hanlin.fadp.base.util.Debug
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.model.ModelField
import com.hanlin.fadp.entity.util.EntityQuery

response.setContentType("application/xml");// 设置response内容的类型
response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("demoData.xml", "ISO8859-1"));// 设置头部信息
OutputStream outputStream = response.getOutputStream();// 获取文件输出IO流
def writer = new PrintWriter(outputStream)

writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
writer.println("<entity-engine-xml>");

def parameters= parameters as Map
def delegator =delegator as Delegator
String entityNameStr = parameters["entityNameArray"]
def entityNameArray = entityNameStr.split("\n")
if (entityNameArray){
    entityNameArray.each {String entityName ->
        entityName=entityName.trim()
        if(entityName&&delegator.getModelEntity(entityName)){
            (from(entityName) as EntityQuery).queryList().each {GenericValue value->
                value.writeXmlText(writer,null)
//                writeXmlText(value,writer,null)
            }
        }

    }

}


writer.println("</entity-engine-xml>")





writer.close()

 def  writeXmlText(GenericValue genericValue,PrintWriter writer, String prefix) {
    int indent = 4;
    StringBuilder indentStrBuf = new StringBuilder();
    for (int i = 0; i < indent; i++) indentStrBuf.append(' ');
    String indentString = indentStrBuf.toString();

    if (prefix == null) prefix = "";

    writer.print(indentString);
    writer.print('<');
    writer.print(prefix);
    writer.print(genericValue.getEntityName());

    // write attributes immediately and if a CDATA element is needed, put those in a Map for now
    Map<String, String> cdataMap = new HashMap<String, String>();

    Iterator<ModelField> modelFields = genericValue.getModelEntity().getFieldsIterator();
    while (modelFields.hasNext()) {
        ModelField modelField = modelFields.next();
        String name = modelField.getName();

        String type = modelField.getType();
        if (type != null && type.equals("blob")) {
            Object obj = get(name);
            boolean b1 = obj instanceof byte [];
            if (b1) {
                byte [] binData = (byte [])obj;
                String strData = new String(Base64.base64Encode(binData));
                cdataMap.put(name, strData);
            } else {
                Debug.logWarning("Field:" + name + " is not of type 'byte[]'. obj: " + obj, module);
            }
        } else {
            String valueStr = genericValue.getString(name);

            if (valueStr != null) {
                StringBuilder value = new StringBuilder(valueStr);
                boolean needsCdata = false;

                // check each character, if line-feed or carriage-return is found set needsCdata to true; also look for invalid characters
                for (int i = 0; i < value.length(); i++) {
                    char curChar = value.charAt(i);
                    /*
                     * Some common character for these invalid values, have seen these are mostly from MS Word, but may be part of some standard:
                     * 5 = ... 18 = apostrophe 19 = left quotation mark 20 = right quotation mark 22 = – 23 = - 25 = tm
                     */

                    switch (curChar) {
                        case '\'':
                            value.replace(i, i+1, "&apos;");
                            break;
                        case '"':
                            value.replace(i, i+1, "&quot;");
                            break;
                        case '&':
                            value.replace(i, i+1, "&amp;");
                            break;
                        case '<':
                            value.replace(i, i+1, "&lt;");
                            break;
                        case '>':
                            value.replace(i, i+1, "&gt;");
                            break;
                        case 0xA: // newline, \n
                            needsCdata = true;
                            break;
                        case 0xD: // carriage return, \r
                            needsCdata = true;
                            break;
                        case 0x9: // tab
                            // do nothing, just catch here so it doesn't get into the default
                            break;
                        case 0x5: // elipses (...)
                            value.replace(i, i+1, "...");
                            break;
                        case 0x12: // apostrophe
                            value.replace(i, i+1, "&apos;");
                            break;
                        case 0x13: // left quote
                            value.replace(i, i+1, "&quot;");
                            break;
                        case 0x14: // right quote
                            value.replace(i, i+1, "&quot;");
                            break;
                        case 0x16: // big(?) dash -
                            value.replace(i, i+1, "-");
                            break;
                        case 0x17: // dash -
                            value.replace(i, i+1, "-");
                            break;
                        case 0x19: // tm
                            value.replace(i, i+1, "tm");
                            break;
                        default:
                            if (curChar < 0x20) {
                                // if it is less that 0x20 at genericValue point it is invalid because the only valid values < 0x20 are 0x9, 0xA, 0xD as caught above
                                Debug.logInfo("Removing invalid character [" + curChar + "] numeric value [" + (int) curChar + "] for field " + name + " of entity with PK: " + genericValue.getPrimaryKey().toString(), module);
                                value.deleteCharAt(i);
                            } else if (curChar > 0x7F) {
//                                // Replace each char which is out of the ASCII range with a XML entity
//                                if(curChar>='啊'&&curChar<='匝'){
//                                    //汉字不处理
//                                }else{
                                    String replacement = "&#" + (int) curChar + ";";
                                    if (Debug.verboseOn()) {
                                        Debug.logVerbose("Entity: " + genericValue.getEntityName() + ", PK: " + genericValue.getPrimaryKey().toString() + " -> char [" + curChar + "] replaced with [" + replacement + "]", module);
                                    }
                                    value.replace(i, i+1, replacement);
//                                }

                            }
                    }
                }

                if (needsCdata) {
                    // use valueStr instead of the escaped value, not needed or wanted in a CDATA block
                    cdataMap.put(name, valueStr);
                } else {
                    writer.print(' ');
                    writer.print(name);
                    writer.print("=\"");
                    // encode the value...
                    writer.print(value.toString());
                    writer.print("\"");
                }
            }
        }
    }

    if (cdataMap.size() == 0) {
        writer.println("/>");
    } else {
        writer.println('>');

        for (Map.Entry<String, String> entry: cdataMap.entrySet()) {
            writer.print(indentString);
            writer.print(indentString);
            writer.print('<');
            writer.print(entry.getKey());
            writer.print("><![CDATA[");
            writer.print(entry.getValue());
            writer.print("]]></");
            writer.print(entry.getKey());
            writer.println('>');
        }

        // don't forget to close the entity.
        writer.print(indentString);
        writer.print("</");
        writer.print(genericValue.getEntityName());
        writer.println(">");
    }
}
