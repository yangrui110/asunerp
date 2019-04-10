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
		if(i >= tables.length){
			alert('所要导入之表不存在!');
			return false;
		}
		
	    var fileName = $('#file-text').val();
        var index = fileName.lastIndexOf(".");
        if(fileName == ""){
        	alert("请选择要上传的文件!");
        	return false;
        }
        
        if(index >= 0){
        	fileName = fileName.substring(index + 1, fileName.length);
        	if(fileName == "csv"){
        		return true;	
        	}else{
        		alert("请导入.csv格式的数据!");
        		return false;
        	}
        } 
       	return false;
     }
     

	//-------------------------
	$(function(){
		$("#choose-file").change(function(){
			$("#file-text").attr('value', $("#choose-file").val());
		});

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
      				tables.push(data.jsonName[i]);		//验证表是否存在用的
				}
			}, "json");
		});


		//必须执行一次
		$.post('getDataForCsv', {'value':$(this).val(), 'groupName':$('#database-type').val()}, function(data){
				for(var i=0; i<data.jsonName.length; i++){

      				tables.push(data.jsonName[i]);		//验证表是否存在用的
				}
			}, "json");



		$('#sub').click(function()
			{	
				 if(ckeck()){
				 var form = $("form[name=fileForm]");  
		       	 var options  = {    
		            url:'addDataList',    
		            type:'post',   
		            success:function(data)    
		            {    
		            		show($(eval(data).responseText).val());
		             },
		             error:function(data){  
                       		show($(eval(data).responseText).val());
                      }  
		          };    
			      form.ajaxSubmit(options); 
			      }
				 return false;
			})
	})
	
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

<div  style="font-size:40px;text-align:center;margin-bottom:20px;">
	<#if message?has_content>
		${message}
	</#if>
</div>
<div class="center" style="margin-bottom: 40px;">
	<form id="form1" onsubmit="return ckeck()"action="addDataList?isTrue=true" method="post" enctype="multipart/form-data"  name="fileForm">
		<input type="hidden" name="isTrue" value="true" />
		<table cellpadding="0" cellspacing="0" class="input-table"
				width="100%" id="insertMarks">
			<tbody>
				<tr>
					<td class="input-label">文件类型</td>
					<td>
						<select id="file-type">
							<option selected="selected" value="1">CSV</option>	
						</select>
					</td>
					<td class="input-label">文件字符集</td>
					<td>
						<select id="char-set">
							<option selected="selected" value="1">UTF-8</option>
							
						</select>
					</td>
				</tr>
				<tr>
					<td class="input-label">数据库</td>
					<td colspan="3">
						<select id="database-type" style="width: 550px;" name="databaseName" onchange="changedatabase()">
							<#if entityGroups?has_content>
								<#list entityGroups as groupName>
									<option  value="${groupName}" >${groupName}</option>
								</#list>
							</#if>
						</select>
					</td>
				</tr>
				<tr>
					<td class="input-label">表</td>
					<td colspan="3">
						
						<input type="text" list="_select" id="choose-table"   autocomplete="off" disableautocomplete   name="tableName" style="border: 1px solid rgb(229, 229, 225);margin-left:0%;width:600px;height:32px;" />
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
					<td class="input-label">附件</td>
					<td colspan="3">
						
						<input type="text" id="file-text" readonly="readonly" />
						<div id="mask-file">选择文件
							<input type="file" name="file" class="file-style" id="choose-file" style="width:100px;"/>
						</div>
						<span style="line-height:24px; height:24px; margin-left:40px; float:left">(注:文件大小不能超过8M)</span>
					
					</td>
					
				</tr>
				<tr>
					<td colspan="4"><input type="submit" value="上传" id="sub"></td>
				</tr>
			</tbody>
		</table>
	</from>
	
</div>


