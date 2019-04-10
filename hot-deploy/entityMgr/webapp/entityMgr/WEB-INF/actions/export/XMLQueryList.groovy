
import com.hanlin.fadp.entity.condition.EntityCondition;
import javax.jms.Session;
import com.hanlin.fadp.entity.*;
import java.util.*;
import  java.io.*;
import  java.net.*;
import  java.sql.*;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;
import com.hanlin.fadp.entity.datasource.*;


def myDelegator = DelegatorFactory.getDelegator(parameters.dataSourceName)
def entityResults =myDelegator.getModelEntityMapByGroup(parameters.dataSourceName).keySet().toList()


//以html的形式显示
context.entities = entityResults;


return "success";