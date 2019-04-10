import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {PageHeaderModule, ConfirmModule, DicSelectModule, UploadFileModule, CommonSelectModule, SharedPipesModule, SelectUserModule, SelectDeptModule} from '@shared';
import {Select${entityName}Component} from './select-${minusEntityName}.component';
import {<#list component.complexSelect as module>${module}, </#list>} from '@app';
import {List${entityName}Service} from '../list-${minusEntityName}-service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NguiDatetimePickerModule,
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

  ],
  declarations: [Select${entityName}Component],
  exports: [Select${entityName}Component],
  providers: [List${entityName}Service]
})
export class Select${entityName}Module {
}
