import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import LockableComponent from './lockable-component';

@Injectable({
    providedIn: 'root'
})
export class CanDeactivateGuard implements CanDeactivate<any> {

    constructor(
        private readonly location: Location,
        private readonly router: Router
    ) {}

    canDeactivate(
        component: LockableComponent,
        currentRoute: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot
      ): Observable<boolean> | Promise<boolean> | boolean {
        if (
          (component.allowRedirect === false ||
            (component.canDeactivate && !component.canDeactivate()))
        ) {
          // Angular bug! The stack navigation with candeactivate guard
          // messes up all the navigation stack...
          // see here: https://github.com/angular/angular/issues/13586#issuecomment-402250031
          this.location.go(currentState.url);
    
          return window.confirm('Changes you made may not be saved.');
        } else {
          return true;
        }
      }
}
