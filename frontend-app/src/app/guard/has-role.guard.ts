import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root',
})
export class HasRoleGuard implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    let isAuthorized = false;

    route.data['roles'].forEach( (role: string) => {
        if (this.authService.user?.scope.includes(role)) {
            isAuthorized = true;
            return;
        }
    });
   
    if (!isAuthorized) {
        // stupid trick, lol > "us'e'rs" > 'e' - not an english letter
        // so we get 404 - not found
        this.router.navigate(['/usеrs']);
    }

    return isAuthorized;
  }
}