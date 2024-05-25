import { Component, OnInit } from '@angular/core';


export interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
   

}

export const ROUTES: RouteInfo[] = [
    { path: '/dashboard',     title: 'Dashboard',      icon:'nc-bank',       class: ''  },
    { path: '/maps',          title: 'Maps',              icon:'nc-pin-3',      class: '' },
    { path: '/authors', title: 'Authors', icon: 'nc-single-02', class: ''},
    { path: '/articles', title: 'Articles', icon: 'nc-paper', class: '' },
];

@Component({
    moduleId: module.id,
    selector: 'sidebar-cmp',
    templateUrl: 'sidebar.component.html',
})

export class SidebarComponent implements OnInit {
    public menuItems: any[];
    authorsRoutes: any[];
    articlesRoutes: any[];
    ngOnInit() {
        this.menuItems = ROUTES.filter(menuItem => menuItem);
    }
}
