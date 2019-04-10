import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {List${entityName}Component} from './list-${minusEntityName}.component';

const routes: Routes = [
    {path: '', component: List${entityName}Component}
    ];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class List${entityName}RoutingModule {
}
