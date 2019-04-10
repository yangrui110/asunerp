
<div  style="font-size:40px;text-align:center;margin-bottom:20px;">
	<#if message?has_content>
		${message}
	</#if>
</div>
<div class="center" style="margin-bottom: 40px;">
	<form id="form2" >
		
		<table cellpadding="0" cellspacing="0" class="input-table"
				width="100%" id="insertMarks">
			<tbody>
				<tr>
					<td class="input-label">数据库</td>
					<td>
						<select id="database-type" name="databaseName" onchange="changedatabase()" style="margin: 0 auto;">
							<#if entityGroups?has_content>
								<#list entityGroups as groupName>
									<option  value="${groupName}" >${groupName}</option>
								</#list>
							</#if>
						</select>
					</td>
					<td class="input-label">表</td>
					<td>
						
						<input type="text" list="_select" id="choose-table" autocomplete="off" disableautocomplete  name="tableName" style="border: 1px solid rgb(229, 229, 225);height:32px;margin: 0 auto; width: 240px;" />
						<datalist id="_select">	
							<#if entityResults_first?has_content>
								<#list entityResults_first as op>
									<option value="${op}">${op}</option>
								</#list>
							</#if>
						</datalist>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<input type="button" value="更新" class="_btn" style="width:120px;" onclick="ckeck()" />
					</td>
					<td colspan="2">
						<input type="reset" value="重置" class="_btn" style="width:120px;" />
					</td>
				</tr>
				
			</tbody>
		</table>
	</from>
	
	<script>
	
	
		var tables = [];
		//验证文件类型
	function ckeck(){

		//先对表的数据进行验证
		var i = 0;
		var tableName = $('#choose-table').val();
		if(tableName == ""){
			alert("请选择导入的表!");
			return false;
		}
		for(i=0; i<tables.length; i++){
			if(tableName == tables[i])
				break;
		}
		if(i >= tables.length && tables.length > 0){
				alert('所要更新之表不存在!');
			return false;
		}
		
		
		//显示真正的更新页面
		
		$.post('updatePage', {'databaseName':$('#database-type').val(), 'tableName':$('#choose-table').val()}, function(data){$('#Tip').html(data);});
	   				
       	return false;
     }
	
	
	
	
		$("#database-type").change(function(){
			//先清除掉上次的查询记录
			$('#_select').html("");
			$('#choose-table').val("");
			//一次性完成加载
			$.post('getDataForCsv', {'value':$(this).val(), 'groupName':$('#database-type').val()}, function(data){
				for(var i=0; i<data.jsonName.length; i++){
					var oP = null;
					oP = document.createElement("option");
					$(oP).val(data.jsonName[i]);
					oP.innerHTML = data.jsonName[i];
					$('#_select').append($(oP));
      				tables.push(data.jsonName[i]);	
				}
			}, "json");	
		});
		
		$(function(){
		
			$.post('getDataForCsv', {'value':$(this).val(), 'groupName':$('#database-type').val()}, function(data){
				for(var i=0; i<data.jsonName.length; i++){
					
      				tables.push(data.jsonName[i]);	
				}
			}, "json");	
			
		})
		
	</script>
</div>

<div id="Tip"></div>
