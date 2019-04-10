<#macro fieldInfoItems field>
  <@fi field 'fieldName'></@fi>
  <@fi field 'PK'></@fi>
  <@fi field 'fieldLabel'></@fi>
  <@fi field 'fieldType'></@fi>
  <@fi field 'showInQuery'></@fi>
  <@fi field 'showInTable'></@fi>
  <@fi field 'showInView'></@fi>
  <@fi field 'showInEdit'></@fi>
  <@fi field 'required'></@fi>
  <@fi field 'isValueField'></@fi>
  <@fi field 'isShowField'></@fi>
  <@fi field 'component'></@fi>
  <@fi field 'isApproveStateField'></@fi>
  <@fi field 'defaultValue'></@fi>
  <@fi field 'orderBy'></@fi>
</#macro>
<#macro fi field fieldName ><#assign fieldValue=field[fieldName]><#if fieldValue!=''&&fieldValue!='N'>${fieldName}: '${fieldValue}', </#if></#macro>
import {Injectable} from '@angular/core';
import {AttachmentService, FieldInfo, FieldInfoService} from '@shared';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class List${entityName}Service extends FieldInfoService {

  get entityName() {
    return '${entityName}';
  }

  get queryEntityName() {
    return '${entityName}View';
  }

  get _fieldInfo(): Array<FieldInfo> {
    return [
  <#list fieldList as field>
    <#if field.isSelectInput>
      {
        ${''}<@fieldInfoItems field></@fieldInfoItems>
      <#if field.component=="select"&&field.dicId!="">
        dicId: '${field.dicId}',
      </#if>
      <#if field.relEntityName!="">
        relEntityName: '${field.relEntityName}',
        <#if field.component=="select">
        relFieldName: this.list${field.relEntityName}Service.valueField, relFieldValue: this.list${field.relEntityName}Service.showField,
        </#if>
      </#if>
      },
    <#else>
      {<@fieldInfoItems field></@fieldInfoItems>},
    </#if>
  </#list>
        ] as Array<FieldInfo>;
  }

  constructor(public router: Router, public http: HttpClient, public modalService: NgbModal, public attachmentService: AttachmentService) {
    super(router, http, modalService, attachmentService);
    this.init();
  }

}


