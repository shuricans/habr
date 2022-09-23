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

const routes: Routes = [
  {path: "", pathMatch: "full", redirectTo: "habr"},
  {path: "habr", component: HabrPageComponent},
  {path: "habr/:postId", component: PostPageComponent},
  {path: "design", component: DesignPageComponent},
  {path: "web", component: WebPageComponent},
  {path: "mobile", component: MobilePageComponent},
  {path: "marketing", component: MarketingPageComponent},
  {path: "help", component: HelpPageComponent},
  {path: "search", component: SearchPageComponent},
  {path: "login", component: LoginPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
