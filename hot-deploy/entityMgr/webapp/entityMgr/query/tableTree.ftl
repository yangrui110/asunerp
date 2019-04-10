<script src="<@fadpContentUrl>${StringUtil.wrapString('/images/entityMgr/js/jquery.treeview.js')}</@fadpContentUrl>" type="text/javascript"></script>
<div class="left">
	 <div>
	      
	          <b>实体名称:</b>
	        <input type= "text" id="filterByEntityName" name= "filterByEntityName" value="${parameters.filterByEntityName?if_exists}" onkeydown='enterIn(event);'/>
	    </div>
<ul id="tree" class="treeview">
	<#list dbList as item>
	   <#if item.entityNames?has_content>
			<li>
				<img src="/images/entityMgr/images/icon.png">
				<span onClick="count(undefined,this)">${item.dataSourceName}
				</span>
					<ul id="tree" class="treeview">
					<#list item.entityNames as entity>
						<li><img src="/images/entityMgr/images/icon.png"/>
					   <span onClick="count('${entity}',this)">${entity}</span>
					</li>
					</#list>
					</ul>				
			</li>
		</#if>
	</#list>		
</ul>
</div>
<script type="text/javascript">
	var $tree=$('#tree');
	var cur;
	var curLevel;
	var $content = $('#tree-content');
	var $right_center=$('.right_center');
	$tree.treeview({
		collapsed : true,
		unique: true,
		animated : "medium"
	});
	function count(entityName,$this){
		//产生选择效果
		if (cur != undefined) {
			cur.removeClass('current_item');
		}
		$($this).addClass('current_item');
		cur=$($this);
		
		if(entityName!=undefined)
		{
				var id=[];
				$('.panel').each(function(i)
				{
					if(i!=0)
					{
						id.push($(this).children().eq(0).attr('id'));
					}
				})
				var dataSourceName=$($this).parent().parent().parent().children().eq(2).html().trim();
				var curid=dataSourceName+'#'+entityName;
				var index=$.inArray(curid, id);
				if(index>=0)
				{
					$("#tt").tabs("select",index+1);
				}else
				{	
					$.post('table_data',
				      {'entityName':entityName,'dataSourceName':dataSourceName},
						function(returnedData) {
							 $('#tt').tabs('add',{ 
							 		id:curid,
								    title:entityName,    
								    content:returnedData,  
								    closable:true
						              });
						});		
				}
		         window.scrollBy(0,-10000);  
		   }
	}
	

</script>
<script>
	function enterIn(evt){
			var evt=evt?evt:(window.event?window.event:null);
			if (evt.keyCode==13){
				var filterByEntityName=$('#filterByEntityName').val();
				$.post('tree', 
						{'filterByEntityName':filterByEntityName},
						function(returnedData){
							$('#cen_tree').html(returnedData);
					 });
			}
		}
</script>



