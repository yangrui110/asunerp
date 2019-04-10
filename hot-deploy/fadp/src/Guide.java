import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericPK;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityFieldMap;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.util.EntityListIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Guide {
    Delegator delegator;
    // 添加学生
    public void addStudent() throws GenericEntityException {
        Map<String,Object> map=new HashMap<>();
        map.put("studentId","2018011001");
        map.put("studentName","江豆");
        map.put("sex","男");
        GenericValue value = delegator.makeValue("TestStudent", map);
        delegator.create(value);
    }
    // 添加学生，studentId自增长
    public void addStudentAutoId() throws GenericEntityException {
        String entityName="TestStudent";
        Map<String,Object> map=new HashMap<>();
        map.put("studentId",delegator.getNextSeqId(entityName));
        map.put("studentName","白雪");
        map.put("sex","女");
        GenericValue value = delegator.makeValue(entityName, map);
        delegator.create(value);
    }
    //按主键删除学生
    public void removeStudentByPrimaryKey(String studentId) throws GenericEntityException {
        GenericPK pk = delegator.makePK("TestStudent", "studentId", studentId);
        delegator.removeByPrimaryKey(pk);
    }
    //按条件删除学生
    public void removeStudentByCondition(String studentId) throws GenericEntityException {
        EntityCondition condition = EntityCondition.makeCondition("studentId", studentId);
        delegator.removeByCondition("TestStudent",condition);
    }

    //查询学生
    public void findStudentList() throws GenericEntityException {
        //and查询
        Map<String,Object> map=new HashMap<>();
        map.put("studentName","白雪");
        map.put("sex","女");
        EntityCondition andCondition = EntityCondition.makeCondition(map);
        delegator.findList("TestStudent",andCondition,null,null,null,false);



        //or查询
        EntityCondition orCon1 = EntityCondition.makeCondition("studentId", "2018011001");
        EntityCondition orCon2 = EntityCondition.makeCondition("studentId", "test");
        List<EntityCondition> orConditionList=new ArrayList<>();
        orConditionList.add(orCon1);
        orConditionList.add(orCon2);
        EntityCondition orCondition = EntityCondition.makeCondition(orConditionList,EntityOperator.OR);
        delegator.findList("TestStudent",orCondition,null,null,null,false);


        //in查询
        List<Object> inList=new ArrayList<>();
        inList.add("2018011001");
        inList.add("test");
        EntityCondition inCondition = EntityCondition.makeCondition("studentId", EntityOperator.IN, inList);
        delegator.findList("TestStudent",inCondition,null,null,null,false);


        //分页查询,从结果集第二条开始选取一条
        EntityListIterator iterator = delegator.find("TestStudent", null, null, null, null, null);
        List<GenericValue> list = iterator.getPartialList(  2, 1);

    }



}
