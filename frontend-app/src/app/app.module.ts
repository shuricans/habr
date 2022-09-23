import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavBarComponent } from './component/nav-bar/nav-bar.component';
import { HabrPageComponent } from './page/habr-page/habr-page.component';
import { WebPageComponent } from './page/web-page/web-page.component';
import { MobilePageComponent } from './page/mobile-page/mobile-page.component';
import { HelpPageComponent } from './page/help-page/help-page.component';
import { MarketingPageComponent } from './page/marketing-page/marketing-page.component';
import { DesignPageComponent } from './page/design-page/design-page.component';
import { SearchPageComponent } from './page/search-page/search-page.component';
import { LoginPageComponent } from './page/login-page/login-page.component';
import { PostPageComponent } from './page/post-page/post-page.component';

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    HabrPageComponent,
    WebPageComponent,
    MobilePageComponent,
    HelpPageComponent,
    MarketingPageComponent,
    DesignPageComponent,
    SearchPageComponent,
    LoginPageComponent,
    PostPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
