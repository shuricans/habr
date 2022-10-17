import {Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Credentials} from "../../model/credentials";
import { JwtResponse } from 'src/app/model/jwt-response';
import { AuthResult } from 'src/app/model/authResult';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {

  badCredentials: boolean = false;
  isServerError: boolean = false;
  isZeroError: boolean = false;
  isUnknownError: boolean = false;

  form = new FormGroup({
    username: new FormControl(null, Validators.required),
    password: new FormControl(null, Validators.required)
  })

  constructor(private authService: AuthService, private router: Router) {
  }

  submitForm() {
    if (this.form.invalid) {
      return;
    }

    let username = this.form.get('username')?.value;
    let password = this.form.get('password')?.value;

    let credentials = new Credentials(username!, password!);

    this.authService.signin(credentials)
      .subscribe({
        next: (authresult: AuthResult) => {
          if (authresult.success) {
            this.router.navigate([authresult.redirectUrl]);
          } else {
            let status = authresult.exceptionDetails?.status;
            switch (status) {
              case 0:
                this.isZeroError = true;
                break;
              case 400:
                this.badCredentials = true;
                break;
              case 500:
                this.isServerError = true;
                break;
              default:
                this.isUnknownError = true;
            }
          }
        }
      });
  }
}
