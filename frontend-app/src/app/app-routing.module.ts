import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HabrPageComponent} from "./page/habr-page/habr-page.component";
import {DesignPageComponent} from "./page/design-page/design-page.component";
import {WebPageComponent} from "./page/web-page/web-page.component";
import {MobilePageComponent} from "./page/mobile-page/mobile-page.component";
import {MarketingPageComponent} from "./page/marketing-page/marketing-page.component";
import {HelpPageComponent} from "./page/help-page/help-page.component";
import {SearchPageComponent} from "./page/search-page/search-page.component";
import {LoginPageComponent} from "./page/login-page/login-page.component";
import {PostPageComponent} from "./page/post-page/post-page.component";
import { SigninSignupGuard } from './guard/signin-signup-guard';
import { LkPageComponent } from './page/lk-page/lk-page.component';
import { AuthGuard } from './guard/auth-guard';
import { SignupPageComponent } from './page/signup-page/signup-page.component';
import { NotFoundPageComponent } from './page/not-found-page/not-found-page.component';
import { UserPageComponent } from './page/user-page/user-page.component';
import { CanDeactivateGuard } from './guard/can-deactivate.guard';
import { HasRoleGuard } from './guard/has-role.guard';
import { AllUsersPageComponent } from './page/all-users-page/all-users-page.component';
import { AllPostsPageComponent } from './page/all-posts-page/all-posts-page.component';

const routes: Routes = [
  {
    path: "", 
    pathMatch: "full", 
    redirectTo: "habr"
  },
  {
    path: "habr", 
    component: HabrPageComponent
  },
  {
    path: "habr/:postId", 
    component: PostPageComponent
  },
  {
    path: "design", 
    component: DesignPageComponent
  },
  {
    path: "web", 
    component: WebPageComponent
  },
  {
    path: "user", 
    component: UserPageComponent
  },
  {
    path: "users", 
    component: AllUsersPageComponent, 
    canActivate: [AuthGuard, HasRoleGuard], 
    data: {
      roles: ['ROLE_ADMIN', 'ROLE_MODERATOR']
    }
  },
  {
    path: "posts", 
    component: AllPostsPageComponent, 
    canActivate: [AuthGuard, HasRoleGuard], 
    data: {
      roles: ['ROLE_ADMIN', 'ROLE_MODERATOR']
    }
  },
  {
    path: "mobile", 
    component: MobilePageComponent
  },
  {
    path: "marketing", 
    component: MarketingPageComponent
  },
  {
    path: "help", 
    component: HelpPageComponent
  },
  {
    path: "search", 
    component: SearchPageComponent
  },
  {
    path: "login", 
    component: LoginPageComponent, 
    canActivate: [SigninSignupGuard]
  },
  {
    path: "lk", 
    component: LkPageComponent, 
    canActivate: [AuthGuard], 
    canDeactivate: [CanDeactivateGuard]
  },
  {
    path: "signup", 
    component: SignupPageComponent, 
    canActivate: [SigninSignupGuard]
  },
  {
    path: "user/:username", 
    component: UserPageComponent
  },
  {
    path: '**', // Wildcard route for a 404 page
    component: NotFoundPageComponent
  },  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
