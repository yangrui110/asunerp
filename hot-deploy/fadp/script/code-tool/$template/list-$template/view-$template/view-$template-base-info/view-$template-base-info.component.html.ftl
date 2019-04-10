<div class="form-inline">
<#list fieldList as field>
<#if field.showInView=="Y">
  <div class="form-group p-0 pt-1 <#if field.component=='text'>col-12<#else>col-6</#if>">
    <small class="<#if field.component=='text'>col-2<#else>col-4</#if> col-form-label text-right">{{uiLabel('${field.fieldName}')}}:</small>
    <div class="<#if field.component=='text'>col-10<#else>col-8</#if> form-control border-0">
    <#if field.viewValue> <#--使用选择组件-->
      {{viewDataTemp.${field.fieldName}Value}}
    <#elseif field.component=='trueFalseSelect'> <#--使用[是/否]组件-->
      {{viewDataTemp.${field.fieldName} | trueFalse}}
    <#elseif field.component=='imageUpload'> <#--使用图片上传组件-->
      <app-image-view [appImageView]="viewDataTemp.${field.fieldName}"></app-image-view>
    <#else>
      {{viewDataTemp.${field.fieldName}}}
    </#if>
    </div>
  </div>
</#if>
</#list>
</div>
