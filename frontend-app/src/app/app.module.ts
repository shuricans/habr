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
import { FormsModule } from "@angular/forms";
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TokenInterceptorProvider } from './interceptor/token.interceptor';
import { LkPageComponent } from './page/lk-page/lk-page.component';
import { SignupPageComponent } from './page/signup-page/signup-page.component';
import { UserDataComponent } from './component/user-data/user-data.component';
import { PostCardComponent } from './component/post-card/post-card.component';
import { PostGalleryComponent } from './component/post-gallery/post-gallery.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { UserPostsTableComponent } from './component/user-posts-table/user-posts-table.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NotFoundPageComponent } from './page/not-found-page/not-found-page.component';
import { PostPaginationComponent } from './component/post-pagination/post-pagination.component';
import { UserPageComponent } from './page/user-page/user-page.component';
import { ConfirmModalComponent } from './component/confirm-modal/confirm-modal.component';
import { InfoModalComponent } from './component/info-modal/info-modal.component';
import { ImageCardComponent } from './component/image-card/image-card.component';
import { AdminNavBarComponent } from './component/admin-nav-bar/admin-nav-bar.component';
import { AllUsersPageComponent } from './page/all-users-page/all-users-page.component';
import { AllPostsPageComponent } from './page/all-posts-page/all-posts-page.component';


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
    PostCardComponent,
    PostGalleryComponent,
    UserPostsTableComponent,
    NotFoundPageComponent,
    PostPaginationComponent,
    UserPageComponent,
    ConfirmModalComponent,
    InfoModalComponent,
    ImageCardComponent,
    AdminNavBarComponent,
    AllUsersPageComponent,
    AllPostsPageComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        MarkdownModule.forRoot(),
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        NgxPaginationModule,
        NgbModule,
    ],
  providers: [TokenInterceptorProvider],
  bootstrap: [AppComponent]
})
export class AppModule { }
