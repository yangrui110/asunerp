<script>
	function check(){
		var num = 0;
		var row = [];

		$("input[name='_check']").each(function(){
			if($(this).prop("checked")){
				row.push(this.parentElement.parentElement.rowIndex);
				num++;
			}	
		});
		if(num > 0){
			var oTab = document.getElementById("tab").rows;
		
			var record = "";
			var field = "";
			for(var j=1; j<oTab[0].cells.length; j++){
				field += oTab[0].cells[j].innerHTML + "#";
			}
			
			for(var k=0; k<row.length; k++){
				
				for(var i=1; i<oTab[0].cells.length; i++){
					record += oTab[row[k]].cells[i].innerHTML + "#";
				}
				record += "&";
			}
			//alert(record);
			$(function(){
				
				$.post('CSVExportList?entityName=${entityName}', {'record':record, 'field':field}, function(){
					
				});
			})
			
			return false;
		}
		alert("请输入要导出的数据!");
		return false;
	}
	
	function setPageSize(select){
			$.post('CSVExport?entityName=${entityName}', {'pageSize':$(select).val(),'requestPage':1},function(returnedData){console.log(returnedData);$('#Tip').html(returnedData);});			
		
		}
	
		function getList(requestPage){ 
			if(requestPage>${totalPages}||requestPage<1)return false;
			$.post('CSVExport?entityName=${entityName}', {'requestPage':requestPage},function(data){
				$("#Tip").html(data);
				});		
		}
	//--------------
	$(function(){
	
		$("#select-condition").click(function(){
				var diag = new Dialog();
				diag.Width = 500;
				diag.Height =550;
				diag.Title = "条件查询";
				diag.InvokeElementId="div1"
				diag.OKEvent = function(){};
				diag.show();
		})
		
		//全选、全不选
		$("#_checkAll").click(function(){
			$("input[name = '_check']").each(function(){
				if($("#checkAll").prop("checked")){
					$(this).prop("checked", true);
				}else{
					$(this).prop("checked", false);
				}
			})
		});
		
		/*首页监听事件*/
		$('#button1').click(function() {getList(1);});
		/*上一页监听事件*/
		$('#button4').click(function() {getList(${currPage}-1);});
		/*下一页监听事件*/
		$('#button2').click(function() {getList(${currPage}+1);});
		/*末页监听事件*/
		$('#button3').click(function() {getList(${totalPages})});
		//改变pageSize
		
	})
</script>
<div id="div1" style="display:none;">
	<form id="select-form">
	<table class="table-style" cellpadding="0" cellspacing="1" width="100%" id="_tab">
		<tbody>
			<#if fieldList?has_content>
				<#list fieldList as field>
					<tr>
						<td>${field}</td>
						<td>
							<#if fieldsType.get(field) == "String">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value="like"> like </option>
								</select>
							<#elseif fieldsType.get(field) == "Double">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value=">"> > </option>
									<option value="<"> < </option>
									<option value=">="> >= </option>
									<option value="<="> <= </option>
								</select>
							<#elseif fieldsType.get(field) == "Long">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value=">"> > </option>
									<option value="<"> < </option>
									<option value=">="> >= </option>
									<option value="<="> <= </option>
								</select>
							<#elseif fieldsType.get(field) == "java.math.BigDecimal">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value=">"> > </option>
									<option value="<"> < </option>
									<option value=">="> >= </option>
									<option value="<="> <= </option>
								</select>
							<#elseif fieldsType.get(field) == "java.sql.Date">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value=">"> > </option>
									<option value="<"> < </option>
									<option value=">="> >= </option>
									<option value="<="> <= </option>
								</select>
							<#elseif fieldsType.get(field) == "java.sql.Time">
								<select id="select-operate">
									<option value="=" selected="selected"> = </option>
									<option value=">"> > </option>
									<option value="<"> < </option>
									<option value=">="> >= </option>
									<option value="<="> <= </option>
								</select>
							</#if>
							
						</td>
						<td><input type="text" name="${field}" /></td>
					</tr>
				</#list>
			</#if>
		</tbody>
	</table>
	</form>
</div>
<div id="Tip">
	<div style="overflow:scroll;">
	<form method="post" onsubmit="return check()">
		<div class="dht2">
			<input type="button" class="on-checked" value="筛选条件" id="select-condition" />
			<div class="funs">					
				<input type="submit" value="导出选中数据" class="_btn"/>
			</div>
		</div>
		<table class="table-style" cellpadding="0" cellspacing="1" width="100%" id="tab">
			<thead>	
				<tr>
					<td ><input type="checkbox" id="_checkAll" /></td>
					<#if fieldList?has_content>
						<#list fieldList as field>
							<td>${field}</td>
						</#list>
					</#if>
				</tr>
			</thead>
			
			<tbody>
				<#if record?has_content>
					<#list record as c>
						<tr>
							<td ><input type="checkbox" name="_check" /></td>
							<#list fieldList as field>
								<td>
								<#if c.get(field)?has_content>
								 ${c.get(field)}
								</#if>
								</td>
							</#list>
						</tr>
					</#list>
				</#if>
			</tbody>
			
		</table>
	
	</form>
</div>

<div class="pager">
	<div style="float: left; margin-left: 20px;"> 
	<select onchange="setPageSize(this)" style="height:30px;">
	<#list [10, 20, 50] as pageSize>
	<option <#if pageSize==parameters.pageSize>selected="selected" </#if> value="${pageSize}">${pageSize}</option>
	</#list>
	</select>
	
	当前页码 ${currPage}  / ${totalPages} , 共 ${totalRow} 条记录 </div>
	<td colspan="3"><button id="button1" type="button" >首页</button></td>
	<td colspan="3"><button id="button4" type="button" >上一页</button></td>
	<td colspan="3"><button id="button2" type="button" >下一页</button></td>
	<td colspan="3"><button id="button3" type="button" >末页</button></td>
</div>


<script type="text/javascript">	   

	   		
	   		/*js必须出现在这些html元素之后*/
		
	   
		//全选、全不选
		$("#_checkAll").click(function(){
			
			$("input[name = '_check']").each(function(){
				if($("#_checkAll").prop("checked")){
					$(this).prop("checked", true);
				}else{
					$(this).prop("checked", false);
				}
			})
		});

		
</script>
</div>

	
