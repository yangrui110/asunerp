import {Component} from '@angular/core';
import {routerTransition} from '@app';
import {CommonDataTable} from '@shared';
import {ActivatedRoute} from '@angular/router';
import {List${entityName}Service} from './list-${minusEntityName}-service';

@Component({
  selector: 'app-list-${minusEntityName}',
  templateUrl: './list-${minusEntityName}.component.html',
  styleUrls: ['./list-${minusEntityName}.component.scss'],
  animations: [routerTransition()]
})
export class List${entityName}Component extends CommonDataTable {
<#if component.approveState>
  get route(): ActivatedRoute {
    return this._route;
  }
</#if>
  constructor(public list${entityName}Service: List${entityName}Service<#if component.approveState>, public _route: ActivatedRoute</#if>) {
    super(list${entityName}Service);
  }
}
