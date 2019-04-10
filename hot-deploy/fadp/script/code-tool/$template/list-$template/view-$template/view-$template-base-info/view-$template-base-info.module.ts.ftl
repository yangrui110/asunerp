import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedPipesModule, ImageViewModule} from '@shared';
import {View${entityName}BaseInfoComponent} from './view-${minusEntityName}-base-info.component';

@NgModule({
  imports: [
    CommonModule,
<#if component.trueFalseSelect>
    SharedPipesModule,
</#if>
<#if component.imageUpload>
    ImageViewModule,
</#if>

  ],
  declarations: [View${entityName}BaseInfoComponent],
  exports: [View${entityName}BaseInfoComponent]
})
export class View${entityName}BaseInfoModule {
}
