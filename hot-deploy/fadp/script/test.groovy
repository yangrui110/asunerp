import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition

import javax.servlet.http.HttpServletResponse;

def delegator =delegator as Delegator
delegator.storeByCondition('EntityFieldInfo',[entityName:'PurchasePlanItem4GenOrder'],EntityCondition.makeCondition([entityName:'PurchasePlanItemView']))
w = (response as HttpServletResponse).writer

w.print """<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>OK</h1>
</body>
</html>"""