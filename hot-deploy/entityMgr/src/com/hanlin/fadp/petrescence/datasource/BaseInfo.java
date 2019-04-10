package com.hanlin.fadp.petrescence.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.service.DispatchContext;

public class BaseInfo {
	public static Set<String> timeStampSet=new HashSet<>();
	static{
		timeStampSet.add("lastUpdatedStamp");
		timeStampSet.add("lastUpdatedTxStamp");
		timeStampSet.add("createdStamp");
		timeStampSet.add("createdTxStamp");
	}
	public static Set<String> getFieldSetWidthOutTimeStamp(ModelEntity modelEntity){
		Set<String> fields=new HashSet<>();
		for(ModelField field:modelEntity.getFieldsUnmodifiable()){
			if(!timeStampSet.contains(field.getName())){
				fields.add(field.getName());
			}
		}
		return fields;
	}
	public static Map<String, Object> fileUpLoad(DispatchContext ctx, Map<String, Object> context) {
		return new FastMap<String, Object>();
	}

}
