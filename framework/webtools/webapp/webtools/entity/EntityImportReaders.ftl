<#--

-->

<div class="page-title"><span>${uiLabelMap.WebtoolsImportToDataSource}</span></div>
<p>${uiLabelMap.WebtoolsXMLImportInfo}</p>
<hr />
  <form method="post" action="<@fadpUrl>entityImportReaders</@fadpUrl>">
    Enter Readers (comma separated, no spaces; from entityengine.xml and fadp-component.xml files; common ones include seed,ext,demo):<br />
    <input type="text" size="60" name="readers" value="${readers?default("seed")}"/><br />
    <input type="checkbox" name="mostlyInserts" <#if mostlyInserts??>checked="checked"</#if> value="true"/>${uiLabelMap.WebtoolsMostlyInserts}<br />
    <input type="checkbox" name="maintainTimeStamps" <#if keepStamps??>checked="checked"</#if> value="true"/>${uiLabelMap.WebtoolsMaintainTimestamps}<br />
    <input type="checkbox" name="createDummyFks" <#if createDummyFks??>checked="checked"</#if> value="true"/>${uiLabelMap.WebtoolsCreateDummyFks}<br />
    <input type="checkbox" name="checkDataOnly" <#if checkDataOnly??>checked="checked"</#if> value="true"/>${uiLabelMap.WebtoolsCheckDataOnly}<br />
    ${uiLabelMap.WebtoolsTimeoutSeconds}:<input type="text" size="6" value="${txTimeoutStr?default("7200")}" name="txTimeout"/><br />
    <div class="button-bar"><input type="submit" value="${uiLabelMap.WebtoolsImport}"/></div>
  </form>
  <#if messages??>
      <hr />
      <h3>${uiLabelMap.WebtoolsResults}:</h3>
      <#list messages as message>
          <p>${message}</p>
      </#list>
  </#if>
