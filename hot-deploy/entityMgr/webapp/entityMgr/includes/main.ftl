<script>
	function linkTo(uri){
		window.location.href="/entityMgr/control/" + uri;
	}
</script>

<div class="clearfix">
	<div class="clients">
		<div class="grid_12 mobile-nomargin" id="client_div">
			<div class="list-carousel carousel__projects">
				<ul id="projects-scroller">
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img7.png" height="208" width="200" alt="" onclick="linkTo('dataSourceList')">

							</figure>
							<h5 class="title">数据源</h5>
							<span class="cats"></span>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img5.png" height="208" width="200" alt="" onclick="linkTo('dataModelList')">

							</figure>
							<h5 class="title">数据抽取模型</h5>
						</div>
					</li>
					<!--<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img3.png" height="208" width="200" alt="" onclick="linkTo('ModelExport')">

							</figure>
							<h5 class="title"><a href="ModelExport">表结构导出</a></h5>
						</div>
					</li>-->
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img3.png" height="208" width="200" alt="" onclick="linkTo('entityModelList')">

							</figure>
							<h5 class="title"><a href="entityModelList">sql元数据</a></h5>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img8.png" height="208" width="200" alt="" onclick="linkTo('mongoCollectionList')">

							</figure>
							<h5 class="title"><a href="mongoCollectionList">存储数据模型</a></h5>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img1.png" height="208" width="200" alt="" onclick="linkTo('dataSourceListForQuery')">
							</figure>
							<h5 class="title"><a href="dataSourceListForQuery">业务核</a></h5>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img2.png" height="208" width="200" alt="" onclick="linkTo('XMLExport')">

							</figure>
							<h5 class="title"><a href="XMLExport">数据抽取</a></h5>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img4.png" height="208" width="200" alt="" onclick="linkTo('CSVImport')">

							</figure>
							<h5 class="title"><a href="CSVImport">数据入库</a></h5>
						</div>
					</li>
					<li>
						<div class="item-inner">
							<figure class="img-holder">
								<img src="../../entityMgr/images/dms-img7.png" height="208" width="200" alt="" onclick="linkTo('dictionaryList')">

							</figure>
							<h5 class="title"><a href="dictionaryList">数据字典</a></h5>
						</div>
					</li>

				</ul>

			</div>
		</div>
	</div>
</div>