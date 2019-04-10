import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {PageHeaderModule, ConfirmModule, DicSelectModule, UploadFileModule, CommonSelectModule, SharedPipesModule, SelectUserModule, SelectDeptModule, TrueFalseSelectModule} from '@shared';
import {<#list component.complexSelect as module>${module}, </#list>} from '@app';
import {Edit${entityName}Component} from './edit-${minusEntityName}.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NguiDatetimePickerModule,
    ConfirmModule,
    DicSelectModule,
    UploadFileModule,
<#list component.complexSelect as module>
    ${module},
</#list>
<#if component.select>
    CommonSelectModule,
</#if>
<#if component.trueFalseSelect>
    TrueFalseSelectModule,
</#if>
<#if component.selectUser>
    SelectUserModule,
</#if>
<#if component.selectDept>
    SelectDeptModule,
</#if>

  ],
  declarations: [Edit${entityName}Component],
  exports: [Edit${entityName}Component]
})
export class Edit${entityName}Module {
}
