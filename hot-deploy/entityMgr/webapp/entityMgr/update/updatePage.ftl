<script src="<@fadpContentUrl>${StringUtil.wrapString('/images/entityMgr/js/jquery.js')}</@fadpContentUrl>" type="text/javascript"></script>
<script src="<@fadpContentUrl>${StringUtil.wrapString('/images/entityMgr/js/bgiframe.js')}</@fadpContentUrl>" type="text/javascript"></script>
<script src="<@fadpContentUrl>${StringUtil.wrapString('/images/entityMgr/js/weebox.js')}</@fadpContentUrl>" type="text/javascript"></script>
<script src="<@fadpContentUrl>${StringUtil.wrapString('/images/entityMgr/js/weetips.js')}</@fadpContentUrl>" type="text/javascript"></script>




<script>
	function check(){	
	
		var num = 0;
		var row = [];

		$("input[name='_check']").each(function(){
			
			if($(this).attr("checked")){
				row.push(this.parentElement.parentElement.rowIndex);
				num++;
			}	
		});
		if(num > 0){
			
			var oTab = document.getElementById("tab").rows;
			//因为post的参数限制，故作此处理
			var record = [];
			
			for(var k=0; k<row.length; k++){
				var temp = new Map();
				for(var i=1; i<oTab[0].cells.length-1; i++){
					temp[oTab[0].cells[i].innerHTML] = oTab[row[k]].cells[i].innerHTML;
				}
				record.push(temp);
			} 
			jsonData = {};
			for(var i=0; i<record.length; i++){
				jsonData[i] = record[i];
			}
			jsonData = JSON.stringify(jsonData);
			$.post('deleteData?groupName=${groupName}&tableName=${tableName}', {'_json':jsonData}, function(data){
				
				if(data.resultState == "success"){
					show('删除成功!');
					//还没有实时更新数据
					$.post('updatePage?databaseName=${groupName}&tableName=${tableName}', {'pageSize':$('#select').val(),'requestPage':${currPage}},function(data){
						$("#Tip").html(data);
					});
				
				}else{
					show('删除失败!');
				}
			}, 'json');

			return false;	
		}else{
			alert("请选择要删除的数据!");
		}
		return false;
	}
</script>

<div id="Tip">
	<div id="div1" style="display:none;">
		<table cellpadding="0" cellspacing="1" width="100%" id="_tab">
			<tbody>
				<#if fieldList?has_content>
					<#list fieldList as field>
						<tr>
							<td style="width: 155px;">${field}</td>
							<#if fieldsType.get(field) == "java.sql.Date" || fieldsType.get(field) == "java.sql.Time" || fieldsType.get(field) == "java.sql.Timestamp">
								<td><input type="text" name="${field}"  onclick="laydate({istime: true, format: 'YYYY-MM-DD hh:mm'})" placeholder="YYYY-MM-DD hh:mm" />
							<#else>
								<td><input type="text" name="${field}"  /></td>
							</#if>
						</tr>
					</#list>
				</#if>
			</tbody>
		</table>
	</div>
	
	<div id="div2" style="display:none;">
		<table cellpadding="0" cellspacing="1" width="100%" id="modefy_tab">
			<tbody>
				<#if pkField?has_content>
					<#list pkField as pk>
						<tr>
							<#-- 对于pk，不需要考虑时间问题-->
							<td style="width: 155px;">${pk}</td>
							<td style="width:224px;float:left;"><input type="text" name="${pk}" readonly="readonly" style="float:left;"  /><span style="float:left; line-height:30px;">pk</span></td>
						</tr>
					</#list>
				</#if>
				<#if nonePk?has_content>
					<#list nonePk as field>
						<tr>
							<td style="width: 155px;">${field}</td>
							<#if fieldsType.get(field) == "java.sql.Date" || fieldsType.get(field) == "java.sql.Time" || fieldsType.get(field) == "java.sql.Timestamp">
								<td><input type="text" name="${field}"  onclick="laydate({istime: true, format: 'YYYY-MM-DD hh:mm'})" placeholder="YYYY-MM-DD hh:mm" />
							<#else>
								<td><input type="text" name="${field}"  /></td>
							</#if>
						</tr>
					</#list>
				</#if>
			</tbody>
		</table>
	</div>
	
<div style="overflow:scroll;">
	<form method="post" onsubmit="return check()" id="download">
		<div style="height:30px;">
			<input type="button" value="添加" onclick="add()" />			
			<div class="funs" style="float:right;">					
				<input type="submit" value="删除选中项" class="_btn" />
			</div>
		</div>
		<table class="table-style" cellpadding="0" cellspacing="1" width="100%" id="tab">
			<thead>	
				<tr>
					<td ><input type="checkbox" id="_checkAll" onclick="selectAll()" /></td>
					<#if fieldList?has_content>
						<#list fieldList as field>
							<td>${field}</td>
						</#list>
					</#if>
					<td>功能</td>
				</tr>
			</thead>
			
			<tbody>
				<#if record?has_content>
					<#list record as c>
						<tr>
							<td ><input type="checkbox" name="_check" /></td>
							<#list fieldList as field>
								<#if pkField?seq_contains(field)?string("yes", "no") == "yes">
									<td class="pk"><#if c.get(field)?has_content>${c.get(field)}</#if></td>
								<#else>
									<td><#if c.get(field)?has_content>${c.get(field)}</#if></td>
								</#if>
								
							</#list>
							<td><input type="button" value="修改" onclick="modefy(this)"/></td>
						</tr>
					</#list>
					<#else>
						<tr>
							<#assign length=fieldList.size() >
							<td colspan="${length+1}" style="font-size:30px;">没有符合条件的数据！</td>
						</tr>
				</#if>
			</tbody>
			
		</table>
	
	</form>
</div>

<div class="pager">
	<div > 
	<select id="select" onchange="setPageSize(this)" style="width:150px; height:30px; line-height:32px;float:left;">
	<#list [10, 20, 50] as pageSize>
	<option <#if pageSize==parameters.pageSize>selected="selected" </#if> value="${pageSize}">${pageSize}</option>
	</#list>
	</select>
	
	<span style="float:left; line-height:32px;">当前页码 ${currPage}  / ${totalPages} , 共 ${totalRow} 条记录 </span></div>
	<div style="float:right; margin-right:20px;">
	<td colspan="3"><button id="button1" type="button" >首页</button></td>
	<td colspan="3"><button id="button4" type="button" >上一页</button></td>
	<td colspan="3"><button id="button2" type="button" >下一页</button></td>
	<td colspan="3"><button id="button3" type="button" >末页</button></td>
	<div>
</div>


<script type="text/javascript">	   
	   /*js必须出现在这些html元素之后*/
		/*首页监听事件*/
		$('#button1').click(function() {getList(1);});
		/*上一页监听事件*/
		$('#button4').click(function() {getList(${currPage}-1);});
		/*下一页监听事件*/
		$('#button2').click(function() {getList(${currPage}+1);});
		/*末页监听事件*/
		$('#button3').click(function() {getList(${totalPages})});
		//改变pageSize
		function setPageSize(select){
			$.post('updatePage?databaseName=${groupName}&tableName=${tableName}', {'pageSize':$(select).val(),'requestPage':1},function(returnedData){console.log(returnedData);$('#Tip').html(returnedData);});			
		
		}
	
		function getList(requestPage){ 
			
			if(requestPage>${totalPages}||requestPage<1)return false;
			$.post('updatePage?databaseName=${groupName}&tableName=${tableName}', {'pageSize':$('#select').val(),'requestPage':requestPage},function(data){
				$("#Tip").html(data);
				});
				//位什么会弹出一个页面			
		}
		
		/*选中所有*/
		function selectAll(){
			//prop函数存在兼容性问题
			if($('#_checkAll').attr('checked')){
				$("input[name='_check']").each(function(){
					$(this).attr('checked', true);
				})
			}else{
				$("input[name='_check']").each(function(){
					$(this).attr('checked', false);
				})
			}
		}
		
		/*更新数据*/
	
		
		/*添加数据*/
		function add(){
			//觉得可以让其刷新
			$.weeboxs.open('#div1', {
				title: '添加',
				position: 'center',
				modal: false,
				trigger: 'select-condition',
				width: 400,
				height: 200,
				clickClose: false,
				type: 'wee',
				onok: function(){
					var row = document.getElementById('_tab').rows;
					var jsonData = {};
					for(var i=0; i<row.length; i++){
						jsonData[row[i].cells[0].innerHTML] = row[i].cells[1].childNodes[0].value;
					}
					jsonData = JSON.stringify(jsonData);
					$.post('addData?tableName=${tableName}&groupName=${groupName}', {'_json':jsonData}, function(data){
						if(data.result == "success"){
							show('添加成功!');
							$.post('updatePage?databaseName=${groupName}&tableName=${tableName}', {'pageSize':$('#select').val(),'requestPage':${currPage}},function(data){
								$("#Tip").html(data);
							});
						}else{
							show('添加失败!');
						}
					}, 'json');
					
					$.weeboxs.close();
				}
			//----------------------------	
			})
			//---------------------结束
		}
			
		/* 修改数据 */
		function modefy(dom){
			
/*			var pk = [];				------只回显主键
			//应为该顺序相同，故用数组即可
			for(var i=1; i<$(dom).parent().parent().children().length - 1; i++){
				
				if($(dom).parent().parent().children().eq(i).attr('class') == "pk"){
					pk.push($(dom).parent().parent().children().eq(i).html());
				}
				oldData += $(dom).parent().parent().children().eq(i).html() + "#";
			}	*/
			
			//回显所有字段
			var all = [];
			for(var i=1; i<$(dom).parent().parent().children().length - 1; i++){
				all.push($(dom).parent().parent().children().eq(i).html());
			}
			
		
			$.weeboxs.open('#div2', {
				title: '修改',
				position: 'center',
				modal: false,
				trigger: 'select-condition',
				width: 400,
				height: 200,
				clickClose: false,
				type: 'wee',
				
				onok: function(){
					
					var row = document.getElementById('modefy_tab').rows;
					
					var jsonData = {};
				
					for(var i=0; i<row.length; i++){
						jsonData[row[i].cells[0].innerHTML] = row[i].cells[1].childNodes[0].value;
					}
					jsonData = JSON.stringify(jsonData);
					
					$.post('modefyData?tableName=${tableName}&groupName=${groupName}', {'_json':jsonData}, function(data){
				
						if(data.result == "success"){
					//		alert('修改成功!');
							show('修改成功!');
							$.post('updatePage?databaseName=${groupName}&tableName=${tableName}', {'pageSize':$('#select').val(),'requestPage':${currPage}},function(data){
								$("#Tip").html(data);
							});
						}else{
							//alert('修改失败!');
							show('修改失败!');
						}
					}, 'json');
					
					$.weeboxs.close();
					
					
				}
			//----------------------------	
			})	
			//---------------------结束
			//显示主键
	/*		var row = document.getElementById('modefy_tab').rows;
			var k = 0;
			for(var i=0; i<row.length; i++){
				if(row[i].cells[1].children[0].readOnly){
					row[i].cells[1].children[0].value = pk[k];
					k++;	
				}
			}		*/
			
			//回显所有字段
			var row = document.getElementById('modefy_tab').rows;
			for(var i=0; i<row.length; i++){
				row[i].cells[1].children[0].value = all[i];
			}
			
			
		}
		
		function show(title){
			 var dialog = art.dialog({
			    title: '提示框',
			    fixed: true,
			    resize: false,
			    drag: false,
			    width: 175,
			    height: 50,
			    content: title,
			    icon: 'succeed',
			    ok: function(){
			    }
			});
		}
</script>
</div>
