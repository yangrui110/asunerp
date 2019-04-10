<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#assign nowTimestamp = Static["com.hanlin.fadp.base.util.UtilDateTime"].nowTimestamp()>

<div id="footer">
  <ul>
    <li>
      ${uiLabelMap.CommonCopyright} (c) 2001-${nowTimestamp?string("yyyy")} The Apache Software Foundation - <a href="http://www.apache.org" target="_blank">www.apache.org</a><br/>
      ${uiLabelMap.CommonPoweredBy} <a href="http://ofbiz.apache.org" target="_blank">Apache OFBiz</a> <#include "fadpHome://runtime/svninfo.ftl" /> <#include "fadpHome://runtime/gitinfo.ftl" />
    </li>
    <li class="opposed">${nowTimestamp?datetime?string.short} -
  <a href="<@fadpUrl>ListTimezones</@fadpUrl>">${timeZone.getDisplayName(timeZone.useDaylightTime(), Static["java.util.TimeZone"].LONG, locale)}</a>
    </li>
  </ul>
</div>

<#if layoutSettings.VT_FTR_JAVASCRIPT?has_content>
  <#list layoutSettings.VT_FTR_JAVASCRIPT as javaScript>
    <script src="<@fadpContentUrl>${StringUtil.wrapString(javaScript)}</@fadpContentUrl>" type="text/javascript"></script>
  </#list>
</#if>

</div>
</body>
</html>
