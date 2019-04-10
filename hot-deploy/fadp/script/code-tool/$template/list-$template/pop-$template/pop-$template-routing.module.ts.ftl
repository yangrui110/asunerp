import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {Pop${entityName}Component} from './pop-${minusEntityName}.component';

const routes: Routes = [
    {path: '', component: Pop${entityName}Component}
    ];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class Pop${entityName}RoutingModule {
}
