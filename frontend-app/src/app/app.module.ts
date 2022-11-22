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
import { MarkdownModule } from 'ngx-markdown';
import {FormsModule} from "@angular/forms";
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TokenInterceptorProvider } from './interceptor/token.interceptor';
import { LkPageComponent } from './page/lk-page/lk-page.component';
import { SignupPageComponent } from './page/signup-page/signup-page.component';
import { UserDataComponent } from './component/user-data/user-data.component';
import { PostEditComponentComponent } from './component/post-edit-component/post-edit-component.component';

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
    PostPageComponent,
    LkPageComponent,
    SignupPageComponent,
    UserDataComponent,
    PostEditComponentComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        MarkdownModule.forRoot(),
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
    ],
  providers: [TokenInterceptorProvider],
  bootstrap: [AppComponent]
})
export class AppModule { }
