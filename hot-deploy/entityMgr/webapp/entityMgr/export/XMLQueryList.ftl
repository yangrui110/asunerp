<script>
	function check(){
		var num = 0;
		var row = [];
		var entityNames = "";
		$("input[name='_check']").each(function(){
			if($(this).prop("checked")){
				row.push(this.parentElement.parentElement.rowIndex);
				num++;
			}	
		});
		if(num > 0){
			var oTab = document.getElementById("tab").rows;
			var list1 = null;
			var jsonData = {};
			for(var k=0; k<row.length; k++){
				jsonData[k] = oTab[row[k]].cells[1].innerHTML;
			}
			jsonData = JSON.stringify(jsonData);
			var form=$('<form></form>');
			for(var k=0; k<row.length; k++){
				form.append($('<input type="text" name="tableNames" value="'+oTab[row[k]].cells[1].innerHTML+'">'));
			}
			form.append('<input type="text" name="dataSourceName" value="${parameters.dataSourceName}">');
			var myRound=new Date().getTime();
			form.append('<input type="text" name="myRound" value="'+myRound+'">');
			var options = {
				target: '#Tip', // 后台将把传递过来的值赋给该元素
				url: 'XMLDownLoad?store=true', // 提交给哪个执行
				type: 'POST',
				success: function (returnData) {
					window.location.href ='download?myRound='+myRound + "&store=false";
				}
			};
			form.ajaxSubmit(options);
//				$.post('XMLDownLoad?store=true', {'tableNames':jsonData,'parameters.dataSourceName':$('#temp').val(),'eachCount':$('#_eachCount').val()}, function(data){
//					window.location.href ='download?tableNames='+entityNames + "&store=false";
//				}, 'json');
			
			
			return false;
		}
		alert("请输入要导出的数据!");
		return false;
	}

	
		
		$("#checkAll").click(function(){
			
			$("input[name = '_check']").each(function(){
				if($("#checkAll").prop("checked")){
					$(this).prop("checked", true);
				}else{
					$(this).prop("checked", false);
				}
			})
		
		
		
		//导出详细表结构
		
				
	})
</script>

<div id="Tip">
	

<div style="overflow:scroll;">
	<form method="post" onsubmit="return check()" id="download">
		<div style="height:30px;">
			<a class="on-checked" style="float:left;margin-top:5px;">表名</a>
			<div class="funs" style="float:right;">					
				<input type="submit" value="导出选中表" class="_btn"/>
			</div>
		</div>
		<table class="table-style" cellpadding="0" cellspacing="1" width="100%" id="tab">
			<thead>	
				<tr>
					<td ><input type="checkbox" id="checkAll" /></td>
					<td>表名</td>
					<td>详&nbsp;&nbsp;&nbsp;细</td>
				</tr>
			</thead>
			<tbody>
				<#if entities?has_content>
					<#list entities as e>
						<tr>
							<td ><input type="checkbox" name="_check" /></td>
							<td>${e}</td>
							<td><input type="button"  onClick="window.open('viewTableData?entityName=${e}&dataSourceName=${parameters.dataSourceName}&myForExport=true')" class="more-info" value="详细" /></td>
						</tr>
					</#list>
				</#if>
			</tbody>
			
		</table>
	
	</form>
</div>

</div>
