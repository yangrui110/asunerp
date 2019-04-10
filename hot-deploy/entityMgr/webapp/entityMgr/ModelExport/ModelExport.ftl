<div style="text-align: center;">
	<div style="margin-bottom:20px;">
		<span style="font-size:30px;">数据模型导出 :</span>
		<br>
	</div>
	<div  style="margin-bottom:20px;">
		<#if helperNames?has_content>
					<select id="select-operate" style="margin:auto;">
						<#list helperNames.keySet() as helperName>
							<option value="	${helperName}" >
								${helperName} 
							</option>
						</#list>
					</select>
		</#if>
	</div>
	<input id="submit" type="submit" value="提交" style="margin-bottom:20px;"/>
	<br>
	<span style="font-size:20px;">导出配置在EntityEngine.xml中的Datasource(例如: localderby)</span>
</div>
<script>
	$('#submit').click(function(){
			 var helperName=$('#select-operate').val().trim(); 
		        <#list helperNames.keySet() as name> 
			        var name='${name}';
			        var group='${helperNames.get(name)}';
			        if(name==helperName)
			        {
			          window.location.href ='Model_Export?helperName='+name+'&group='+group;
			        }
		        </#list>  
	       return fasle;
	})
</script>