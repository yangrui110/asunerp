import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.model.ModelEntity

import javax.servlet.http.HttpServletResponse

def delegator=delegator as GenericDelegator
def response=response as HttpServletResponse

def model=null
if(!parameters.dataSourceName){
	parameters.dataSourceName="default"
}
def title=parameters.entityName+" "
def myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
if(myDelegator.getModelReader().getEntityNames().contains(parameters.entityName)){
	def entity = myDelegator.getModelEntity(parameters.entityName)
	title+=entity.getDescription()
	content="""
<div id="test"></div>

<script>
    var dataTable = \$('#test');

    var table = fadp.dataTable(dataTable, {dataSourceName:"${parameters.dataSourceName}",entityName: "${parameters.entityName}"});
</script>

"""
}else{
	content="""<h2>实体不存在</h2>"""
}
response.writer.print("""<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8" ><!--ie8 JSON 未定义-->
    <meta charset="UTF-8">
    <title>$title</title>
         <script src="/fadp/js/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="/fadp/js/jquery-ui-latest.js" type="text/javascript"></script>
    <script src="/fadp/js/jquery.form.js" type="text/javascript"></script>
    <script src="/fadp/js/jquery.layout-latest.js" type="text/javascript"></script>
    <script src="/fadp/js/jstree/jstree.min.js" type="text/javascript"></script>
    <script src="/fadp/js/validator/jquery.form-validator.js" type="text/javascript"></script>
    <script src="/fadp/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>

    <script src="/fadp/js/plupload/js/plupload.full.min.js"></script>
    <script src="/fadp/js/plupload/js/jquery.ui.plupload/jquery.ui.plupload.min.js" type="text/javascript"></script>
    <script src="/fadp/js/plupload/js/i18n/zh_CN.js"></script>
    <link rel="stylesheet" href="/fadp/js/plupload/js/jquery.ui.plupload/css/jquery.ui.plupload.css">



    <script src="/fadp/js/jquery.fadp.plugin.js" type="text/javascript"></script>

    <link rel="stylesheet" href="/fadp/css/fadp.css" type="text/css"/>
    <link rel="stylesheet" href="/fadp/css/jquery-ui.css" type="text/css"/>
    <!--<link rel="stylesheet" href="/fadp/css/artDialog.css" type="text/css"/>-->
    <link rel="stylesheet" href="/fadp/css/jstree/default/style.css">
    <link rel="stylesheet" href="/fadp/css/validator.css">
    <link rel="stylesheet" href="/fadp/css/jquery.addOn.css">


</head>
<body>
<h2>$title</h2>
${content}

</body>
</html>


""")