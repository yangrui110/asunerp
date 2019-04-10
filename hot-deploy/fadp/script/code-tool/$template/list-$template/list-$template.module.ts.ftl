import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {PageHeaderModule, ConfirmModule, DicSelectModule, UploadFileModule, CommonSelectModule, SharedPipesModule, SelectUserModule, SelectDeptModule} from '@shared';
import {List${entityName}RoutingModule} from './list-${minusEntityName}-routing.module';
import {Edit${entityName}Module} from './edit-${minusEntityName}/edit-${minusEntityName}.module';
import {List${entityName}Service} from './list-${minusEntityName}-service';
import {View${entityName}Module} from './view-${minusEntityName}/view-${minusEntityName}.module';
import {List${entityName}Component} from './list-${minusEntityName}.component';
import {<#list component.complexSelect as module>${module}, </#list>} from '@app';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NguiDatetimePickerModule,
    PageHeaderModule,
    ConfirmModule,
    DicSelectModule,
<#list component.complexSelect as module>
    ${module},
</#list>
    <#if component.select>
    CommonSelectModule,
    </#if>
    <#if component.trueFalseSelect>
    SharedPipesModule,
    </#if>
    <#if component.selectUser>
    SelectUserModule,
    </#if>
    <#if component.selectDept>
    SelectDeptModule,
    </#if>
    List${entityName}RoutingModule,
    Edit${entityName}Module,
    View${entityName}Module,
  ],
  declarations: [List${entityName}Component],
  exports: [List${entityName}Component],
  providers: [List${entityName}Service]
})
export class List${entityName}Module {
}
