
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
			//因为post的参数限制，故作此处理
			var record = [];
			var field = "";
			for(var j=1; j<oTab[0].cells.length; j++){
				field += oTab[0].cells[j].innerHTML + "#";
			}
			
			for(var k=0; k<row.length; k++){
				var temp = "";
				for(var i=1; i<oTab[0].cells.length; i++){
					temp += oTab[row[k]].cells[i].innerHTML+ "#";
				}
			
				
				record.push(temp);
			}
			jsonData = {};
			for(var i=0; i<record.length; i++){
				jsonData[i] = record[i];
			}
			jsonData = JSON.stringify(jsonData);
		//	alert(field);
		//	alert(jsonData);
			$.post('CSVExportList', {'_json':jsonData, 'field':field}, function(){
				window.location.href = 'CSVExportList?isTrue=true&entityName=${entityName}';
			});
	
		

		
			return false;	
		}else{
			alert("请输入要导出的数据!");
		}
		return false;
	}
	
	
	
	//--------------
	
</script>





<div id="Tip">
	<div  class="dialog-container" style="display:none;">
    	<div class="dialog-head-title" id="title">
	  		<div class="headerL" id="msg1headerL" style="width: 356px;">
	  			<span  class="msg1closeBtn">
	  				
                </span>
	  		</div>
	  		<div class="headerR">
	  		</div>
	  </div>
	  <div class="main-box" >
		  <div class="BodyL" style="width:356px;">
		  		<div style="height:300px; overflow:auto;">
				<table class="input-table" id="_tab" cellpadding="0" cellspacing="1"  >
					<tbody>
						<#if fieldList?has_content>
								
							<#list fieldList as field>
								<tr>
									<td>${field}</td>
									<td>
										<#if fieldsType.get(field) == "String">
											<select class="select-operate" style="width:auto;" >
												<option value="=" selected="selected"> = </option>
												<option value="like"> like </option>
											</select>
										<#elseif fieldsType.get(field) == "Double"> 
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#elseif fieldsType.get(field) == "Long">
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#elseif fieldsType.get(field) == "java.math.BigDecimal" >
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#elseif fieldsType.get(field) == "java.sql.Date" >
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#elseif fieldsType.get(field) == "java.sql.Time" >
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#elseif fieldsType.get(field) == "java.sql.Timestamp" >
											<select class="select-operate" class="select-style" style="width:auto;">
												<option value="=" selected="selected"> = </option>
												<option value=">"> > </option>
												<option value="<"> < </option>
												<option value=">="> >= </option>
												<option value="<="> <= </option>
											</select>
										<#else>
											...
										</#if>
										 	
									</td>
									<td><input type="text" name="${field}" style="width: auto;" /></td>
								</tr>
							</#list>
						</#if>
						<tr>
							<td style="border:0px;"><button class="dialog-ok">确定</button></td>
							<td style="border:0px;"></td>
							<td style="border:0px;"><button class="dialog-cancle">取消</button></td>
						</tr>
					</tbody>
				</table>
				</div>
			</div>
			<div class="BodyR"  style="height: 320px;"></div>
		</div>
   </div> 
	<div style="overflow:scroll;">
	<form method="post" onsubmit="return check()" id="download" action="CSVExportList" >
		<div style="height:23px;">
			<input type="button" class="on-checked" value="筛选条件" id="select-condition" style="float:left;"/>
			<div class="funs" style="float:right;">					
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
								<td><#if c.get(field)?has_content>${c.get(field)}</#if></td>
							</#list>
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
	<div> 
	<select onchange="setPageSize(this)" style="width:70px; height:30px; line-height:26px;float:left;">
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
	</div>
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
			$.post('CSVExport_switch?entityName=${entityName}&groupName=${groupName}', {'pageSize':$(select).val(),'requestPage':1},function(returnedData){console.log(returnedData);$('#Tip').html(returnedData);});			
		
		}
	
		function getList(requestPage){ 
			if(requestPage>${totalPages}||requestPage<1)return false;
			$.post('CSVExport_switch?entityName=${entityName}&groupName=${groupName}', {'requestPage':requestPage},function(data){
				//alert(data);
				$("#Tip").html(data);
				});
				//位什么会弹出一个页面			
		}
	   
		
		$("#select-condition").click(function(){
			       	var $this=this;
                     $('.dialog-container').show();
					  var oTitle=$('.dialog-head-title')[0];
					  var oB=$('.dialog-container')[0];
						startDrag(oTitle, oB);	
						setheight();	
		});
		$(".dialog-ok").click(function(){
				var row = document.getElementById('_tab').rows;
					var jsonData = {};
					for(var i=0; i<row.length-1; i++){
						var form_data = "";
						var form_field = "";
						var form_second = "";
						var form_third = "";
						form_field += $("#_tab tbody tr").eq(i).children().eq(0).html() + "#";
						form_second += $("#_tab tbody tr").eq(i).children().eq(1).find("option:selected").text().trim() + "#";
						form_third +=$("#_tab tbody tr").eq(i).children().eq(2).find('input').val() + "#"; 
						form_data += form_field + form_second + form_third;
						jsonData[i] = form_data;
					}
					jsonData = JSON.stringify(jsonData);
				
					var entityName='${entityName}';
					var groupName='${groupName}';
					$.post('CSVExport_switch?entityName='+entityName+'&groupName='+groupName,
						 {'_json':jsonData}, 
						 function(data){
						 $("#Tip").html(data);
						 });
					
		});
		$(".dialog-cancle").click(function(){
			$('.dialog-container').hide();
		});
		$(".msg1closeBtn").click(function(){
			$('.dialog-container').hide();
		});
		$("#_checkAll").click(function(){
			$("input[name = '_check']").each(function(){
				if($('#_checkAll').prop("checked")){
					$("input[name='_check']").each(function(){
						$(this).prop("checked", true);
					})
				}else{
					$("input[name='_check']").each(function(){
						$(this).prop("checked", false);
					})
				}
			})
		});
		
		function setheight()
		{
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			
			
			var height=$('.input-table').eq(index-1).height();
			if(height<=300)
			{
				$('.BodyR').eq(index-1).height(height+18);
				$('.BodyL').eq(index-1).height(height);
				$('.BodyL').eq(index-1).children().eq(0).height(height);
			}else
			{
				height=$('.BodyL').eq(index-1).height();
			    $('.BodyR').eq(index-1).height(height+18);
			}
			$('.input-table').eq(index-1).find('tr').each(function(){
				$(this).find('td').eq(0).width(60);
				$(this).find('td').eq(1).width(60);
				$(this).find('td').eq(2).find('input').width(100);
			})
		}
</script>
</div>

	
