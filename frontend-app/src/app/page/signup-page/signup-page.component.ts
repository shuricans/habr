import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { SignupRequest } from 'src/app/model/signup-request';
import { SignupResult } from 'src/app/model/signupResult';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-signup-page',
  templateUrl: './signup-page.component.html',
  styleUrls: ['./signup-page.component.scss']
})
export class SignupPageComponent {

  isUsernameAlreadyExists: boolean = false;
  isUnknownError: boolean = false;
  isZeroError: boolean = false;
  isServerError: boolean = false;

  username!: string;
  success: boolean = false;

  form = new FormGroup({
    username: new FormControl(null, [ 
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(30)
    ]),
    firstName: new FormControl(null, Validators.required),
    password: new FormControl(null, [ 
      Validators.required,
      Validators.minLength(6)
    ])
  })

  constructor(private authService: AuthService, private router: Router) {
  }

  submitForm() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    let username = this.form.get('username')?.value;
    let password = this.form.get('password')?.value;
    let firstName = this.form.get('firstName')?.value;

    this.username = username!;

    let request = new SignupRequest(username!, password!, firstName!);

    this.authService.signup(request)
      .subscribe({
        next: (signupResult: SignupResult) => {
          if (signupResult.success) {
            this.success = true;
          } else {
            let status = signupResult.exceptionDetails?.status;
            switch (status) {
              case 0:
                this.isZeroError = true;
                break;
              case 400:
                this.isUsernameAlreadyExists = true;
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
