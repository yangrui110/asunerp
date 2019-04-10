<div class="Tip">
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
										
									<td>
										<#if fieldsType.get(field) == "java.sql.Date" || fieldsType.get(field) == "java.sql.Time" || fieldsType.get(field) == "java.sql.Timestamp">
											<input type="text" name="${field}" class="_date" />
										<#else>
											<input type="text" name="${field}" />
										</#if>
										
									</td>
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
	<form method="post" onsubmit="return check()" class="download">
		<div style="height:23px;text-align:center;margin-bottom:8px;">
			<input type="button"  value="筛选条件" class="select-condition"/>
		</div>
		<table class="table-style" cellpadding="0" cellspacing="1" width="100%" id="tab">
			<thead>	
				<tr>
					<td ><input type="checkbox" class="_checkAll" /></td>
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
	<select class="setpage" style="width:70px; height:30px; line-height:26px;float:left;">
	<#list [10, 20, 50] as pageSize>
	<option <#if pageSize==parameters.pageSize>selected="selected" </#if> value="${pageSize}">${pageSize}</option>
	</#list>
	</select>
	
	<span style="float:left; line-height:32px;">
		当前页码
		<strong class="currPage"> ${currPage}</strong>
		  / 
		 <strong class="totalPages"> ${totalPages}</strong>
		 , 共
		 <strong class="totalRow"> ${totalRow}</strong>
		   条记录 </span>
     </div>
	<div style="float:right; margin-right:20px;">
	
	<td colspan="3"><button class="button1" type="button" >首页</button></td>
	<td colspan="3"><button class="button4" type="button" >上一页</button></td>
	<td colspan="3"><button class="button2" type="button" >下一页</button></td>
	<td colspan="3"><button class="button3" type="button" >末页</button></td>
	</div>
</div>


<script type="text/javascript">	   

	   	$(".msg1closeBtn").unbind("click");
	   	$(".msg1closeBtn").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".button4").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					$('.dialog-container').eq(index-1).hide();
					
				}
			});
		});
		
		$(".dialog-cancle").unbind("click");
	   	$(".dialog-cancle").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".dialog-cancle").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					$('.dialog-container').eq(index-1).hide();
					
				}
			});
		});
		
		$(".dialog-ok").unbind("click");
	   	$(".dialog-ok").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".dialog-ok").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					//$('.dialog-container').eq(index-1).hide();
							var row = $('.input-table').eq(index-1).children().eq(0).children().size()-1;
						    var jsonData = {};
							for(var i=0; i<row; i++){
								var form_data = "";
								var form_field = "";
								var form_second = "";
								var form_third = "";
								form_field = $(".input-table").eq(index-1).children().children().eq(i).children().eq(0).html().trim() + "#";
								form_second = $(".input-table").eq(index-1).children().children().eq(i).children().eq(1).find("option:selected").text().trim() + "#";
								form_third =$(".input-table").eq(index-1).children().children().eq(i).children().eq(2).find('input').val().trim() + "#"; 
								form_data = form_field + form_second + form_third;
								jsonData[i] = form_data;
							}
							
							jsonData = JSON.stringify(jsonData);
							console.log(jsonData);
							
								var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
								$.post('table_data?entityName='+str[1]+'&dataSourceName='+str[0], 
										{'_json':jsonData},
										 function(returnedData){
										 $('.Tip').eq(index-1).parent().html(returnedData);
										 });	
				}
			});
		});
	   
		$(".select-condition").unbind("click");
		$(".select-condition").click(function(){
				var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
				$(".select-condition").each(function(i)
				{
					var $this=this;
					if(i==index-1)
					{
						$('.dialog-container').eq(index-1).show();
					    var oTitle=$('.dialog-head-title').eq(index-1)[0];
					    var oB=$('.dialog-container').eq(index-1)[0];
					 //   var top=$('#header').offset().top+$('#header').height();
						startDrag(oTitle, oB );
						setheight();
						$('._date').datetimepicker({lang:'ch'});
					}
				});
		});
	
		
		
		
		
		function getList(requestPage,totalPages,entityName,dataSourceName,index){ 
			
			if(requestPage>totalPages||requestPage<1)return false;
			$.post('table_data?entityName='+entityName+'&dataSourceName='+dataSourceName, {'requestPage':requestPage},function(data){
					$('.Tip').eq(index-1).parent().html(data);
				});
				
		}
		
		$(".button1").unbind("click");
	   	$(".button1").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".button1").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					var requstPage=1;
					var totalPage=$('.totalPages').eq(index-1).html().trim();
					var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
					getList(requstPage,totalPage,str[1],str[0],index);
				}
			});
		});
		$(".button2").unbind("click");
	   	$(".button2").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".button2").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
				
					var requstPage=$('.currPage').eq(index-1).html().trim();
					requstPage=parseInt(requstPage)+1;
					var totalPage=$('.totalPages').eq(index-1).html().trim();
					var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
					getList(requstPage,totalPage,str[1],str[0],index);
				}
			});
		});
		$(".button3").unbind("click");
	   	$(".button3").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".button3").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
				
					var requstPage=$('.totalPages').eq(index-1).html().trim();
					var totalPage=$('.totalPages').eq(index-1).html().trim();
					var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
					getList(requstPage,totalPage,str[1],str[0],index);
				}
			});
		});
		
		$(".button4").unbind("click");
	   	$(".button4").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".button4").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
				
					var requstPage=$('.currPage').eq(index-1).html().trim();
					requstPage=parseInt(requstPage)-1;
					var totalPage=$('.totalPages').eq(index-1).html().trim();
					var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
					getList(requstPage,totalPage,str[1],str[0],index);
				}
			});
		});
	
		$(".setpage").unbind("change");
		$('.setpage').change(function(){ 
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$(".setpage").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					var str=$('.panel-body-noborder').eq(index).attr('id').split('#');
					var pageSize=$('.setpage').eq(index-1).val();
					$.post(
							'table_data?entityName='+str[1]+'&dataSourceName='+str[0],
							{'pageSize':pageSize,'requestPage':1},
							 function(returnedData){
							 $('.Tip').eq(index-1).parent().html(returnedData);
							 });	
				}
			})
		})
		
		$("._checkAll").unbind("click");
		$("._checkAll").click(function(){
			var index=$('.tabs-selected').parent().children().index($('.tabs-selected'));
			$("._checkAll").each(function(i)
			{
				var $this=this;
				if(i==index-1)
				{
					if($($this).prop("checked")){
							$($this).parent().parent().parent().next().children().each(function()
								{
									$(this).children().eq(0).children().prop("checked", true);
								})
						}else
						{
							$($this).parent().parent().parent().next().children().each(function()
								{
									$(this).children().eq(0).children().eq(0).prop("checked", false);
								})
							
						}
					
				}
			})
		})
		
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

	
