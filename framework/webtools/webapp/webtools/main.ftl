<#--
-->
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.WebtoolsMainPage}</li>
      <li class="disabled">${delegator.getDelegatorName()}</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
    <#if !userLogin?has_content>
      <div>${uiLabelMap.WebtoolsForSomethingInteresting}.</div>
      <br />
      <div>${uiLabelMap.WebtoolsNoteAntRunInstall}</div>
      <br />
      <div><a href="<@fadpUrl>checkLogin</@fadpUrl>">${uiLabelMap.CommonLogin}</a></div>
    </#if>
    <#if userLogin?has_content>
      <ul class="webToolList">
        <li><h3>${uiLabelMap.WebtoolsCacheDebugTools}</h3></li>
        <li><a href="<@fadpUrl>FindUtilCache</@fadpUrl>">${uiLabelMap.WebtoolsCacheMaintenance}</a></li>
        <li><a href="<@fadpUrl>LogConfiguration</@fadpUrl>">${uiLabelMap.WebtoolsAdjustDebuggingLevels}</a></li>
        <li><a href="<@fadpUrl>LogView</@fadpUrl>">${uiLabelMap.WebtoolsViewLog}</a></li>
        <li><a href="<@fadpUrl>ViewComponents</@fadpUrl>">${uiLabelMap.WebtoolsViewComponents}</a></li>
        <#if security.hasPermission("ARTIFACT_INFO_VIEW", session)>
          <li><h3>${uiLabelMap.WebtoolsGeneralArtifactInfoTools}</h3></li>
          <li><a href="<@fadpUrl>ViewComponents</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsArtifactInfo}</a></li>
          <li><a href="<@fadpUrl>entityref</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceInteractiveVersion}</a></li>
          <li><a href="<@fadpUrl>ServiceList</@fadpUrl>">${uiLabelMap.WebtoolsServiceReference}</a></li>
        </#if>
        <#if security.hasPermission("LABEL_MANAGER_VIEW", session)>
          <li><h3>${uiLabelMap.WebtoolsLabelManager}</h3></li>
          <li><a href="<@fadpUrl>SearchLabels</@fadpUrl>">${uiLabelMap.WebtoolsLabelManager}</a></li>
        </#if>
        <#if security.hasPermission("ENTITY_MAINT", session)>
          <li><h3>${uiLabelMap.WebtoolsEntityEngineTools}</h3></li>
          <li><a href="<@fadpUrl>entitymaint</@fadpUrl>">${uiLabelMap.WebtoolsEntityDataMaintenance}</a></li>
          <li><a href="<@fadpUrl>entityref</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceInteractiveVersion}</a></li>
          <li><a href="<@fadpUrl>entityref?forstatic=true</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceStaticVersion}</a></li>
          <li><a href="<@fadpUrl>entityrefReport</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReferencePdf}</a></li>
          <li><a href="<@fadpUrl>EntitySQLProcessor</@fadpUrl>">${uiLabelMap.PageTitleEntitySQLProcessor}</a></li>
          <li><a href="<@fadpUrl>EntitySyncStatus</@fadpUrl>">${uiLabelMap.WebtoolsEntitySyncStatus}</a></li>
          <li><a href="<@fadpUrl>view/ModelInduceFromDb</@fadpUrl>" target="_blank">${uiLabelMap.WebtoolsInduceModelXMLFromDatabase}</a></li>
          <li><a href="<@fadpUrl>EntityEoModelBundle</@fadpUrl>">${uiLabelMap.WebtoolsExportEntityEoModelBundle}</a></li>
          <li><a href="<@fadpUrl>view/checkdb</@fadpUrl>">${uiLabelMap.WebtoolsCheckUpdateDatabase}</a></li>
          <li><a href="<@fadpUrl>ConnectionPoolStatus</@fadpUrl>">${uiLabelMap.ConnectionPoolStatus}</a></li>
          <#-- want to leave these out because they are only working so-so, and cause people more problems that they solve, IMHO
            <li><a href="<@fadpUrl>view/EditEntity</@fadpUrl>"  target="_blank">Edit Entity Definitions</a></li>
            <li><a href="<@fadpUrl>ModelWriter</@fadpUrl>" target="_blank">Generate Entity Model XML (all in one)</a></li>
            <li><a href="<@fadpUrl>ModelWriter?savetofile=true</@fadpUrl>" target="_blank">Save Entity Model XML to Files</a></li>
          -->
          <#-- not working right now anyway
            <li><a href="<@fadpUrl>ModelGroupWriter</@fadpUrl>" target="_blank">Generate Entity Group XML</a></li>
            <li><a href="<@fadpUrl>ModelGroupWriter?savetofile=true</@fadpUrl>" target="_blank">Save Entity Group XML to File</a></li>
          -->
          <#--
            <li><a href="<@fadpUrl>view/tablesMySql</@fadpUrl>">MySQL Table Creation SQL</a></li>
            <li><a href="<@fadpUrl>view/dataMySql</@fadpUrl>">MySQL Auto Data SQL</a></li>
          -->
          <li><h3>${uiLabelMap.WebtoolsEntityXMLTools}</h3></li>
          <li><a href="<@fadpUrl>xmldsdump</@fadpUrl>">${uiLabelMap.PageTitleEntityExport}</a></li>
          <li><a href="<@fadpUrl>EntityExportAll</@fadpUrl>">${uiLabelMap.PageTitleEntityExportAll}</a></li>
          <li><a href="<@fadpUrl>EntityImport</@fadpUrl>">${uiLabelMap.PageTitleEntityImport}</a></li>
          <li><a href="<@fadpUrl>EntityImportDir</@fadpUrl>">${uiLabelMap.PageTitleEntityImportDir}</a></li>
          <li><a href="<@fadpUrl>EntityImportReaders</@fadpUrl>">${uiLabelMap.PageTitleEntityImportReaders}</a></li>
        </#if>
        <#if security.hasPermission("SERVICE_MAINT", session)>
          <li><h3>${uiLabelMap.WebtoolsServiceEngineTools}</h3></li>
          <li><a href="<@fadpUrl>ServiceList</@fadpUrl>">${uiLabelMap.WebtoolsServiceReference}</a></li>
          <li><a href="<@fadpUrl>scheduleJob</@fadpUrl>">${uiLabelMap.PageTitleScheduleJob}</a></li>
          <li><a href="<@fadpUrl>runService</@fadpUrl>">${uiLabelMap.PageTitleRunService}</a></li>
          <li><a href="<@fadpUrl>FindJob</@fadpUrl>">${uiLabelMap.PageTitleJobList}</a></li>
          <li><a href="<@fadpUrl>threadList</@fadpUrl>">${uiLabelMap.PageTitleThreadList}</a></li>
          <li><a href="<@fadpUrl>ServiceLog</@fadpUrl>">${uiLabelMap.WebtoolsServiceLog}</a></li>
        </#if>
        <#if security.hasPermission("DATAFILE_MAINT", session)>
          <li><h3>${uiLabelMap.WebtoolsDataFileTools}</h3></li>
          <li><a href="<@fadpUrl>viewdatafile</@fadpUrl>">${uiLabelMap.WebtoolsWorkWithDataFiles}</a></li>
        </#if>
        <li><h3>${uiLabelMap.WebtoolsMiscSetupTools}</h3></li>
        <#if security.hasPermission("PORTALPAGE_ADMIN", session)>
          <li><a href="<@fadpUrl>FindGeo</@fadpUrl>">${uiLabelMap.WebtoolsGeoManagement}</a></li>
          <li><a href="<@fadpUrl>WebtoolsLayoutDemo</@fadpUrl>">${uiLabelMap.WebtoolsLayoutDemo}</a></li>
        </#if>
        <#if security.hasPermission("ENUM_STATUS_MAINT", session)>
          <#--
          <li><a href="<@fadpUrl>EditEnumerationTypes</@fadpUrl>">Edit Enumerations</a></li>
          <li><a href="<@fadpUrl>EditStatusTypes</@fadpUrl>">Edit Status Options</a></li>
          -->
        </#if>
        <li><h3>${uiLabelMap.WebtoolsPerformanceTests}</h3></li>
        <li><a href="<@fadpUrl>EntityPerformanceTest</@fadpUrl>">${uiLabelMap.WebtoolsEntityEngine}</a></li>
        <#if security.hasPermission("SERVER_STATS_VIEW", session)>
          <li><h3>${uiLabelMap.WebtoolsServerHitStatisticsTools}</h3></li>
          <li><a href="<@fadpUrl>StatsSinceStart</@fadpUrl>">${uiLabelMap.WebtoolsStatsSinceServerStart}</a></li>
        </#if>
        <li><h3>${uiLabelMap.WebtoolsCertsX509}</h3></li>
        <li><a href="<@fadpUrl>myCertificates</@fadpUrl>">${uiLabelMap.WebtoolsMyCertificates}</a></li>
      </ul>
    </#if>
  </div>
</div>
