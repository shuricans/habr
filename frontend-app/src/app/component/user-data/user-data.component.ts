import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {UserDto} from "../../model/user-dto";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UpdateUserInfoRequest} from "../../model/update-user-info-request";

@Component({
  selector: 'app-user-data',
  templateUrl: './user-data.component.html',
  styleUrls: ['./user-data.component.scss']
})
export class UserDataComponent implements OnInit {

  userDto?: UserDto;

  constructor(private userService: UserService) {
  }

  form = new FormGroup({
    firstName: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(50)
    ]),
    lastName: new FormControl('', [
      Validators.maxLength(50)
    ]),
    birthday: new FormControl('01.01.2000'),
    aboutMe: new FormControl('', [
      Validators.maxLength(255)
    ]),
  })

  ngOnInit(): void {
    this.userService.getUserData().subscribe({
      next: userDto => {
        this.userDto = userDto;
        this.form.controls.firstName.setValue(userDto.firstName)
        this.form.controls.lastName.setValue(userDto.lastName)
        this.form.controls.birthday.setValue(userDto.birthday)
        this.form.controls.aboutMe.setValue(userDto.aboutMe)
      }
    });
  }

  submitForm() {
    if (this.form.invalid) {
      return;
    }

    let firstName = this.form.get('firstName')?.value;
    let lastName = this.form.get('lastName')?.value;
    let birthday = this.form.get('birthday')?.value;
    let aboutMe = this.form.get('aboutMe')?.value;

    let request = new UpdateUserInfoRequest(firstName!, lastName!, aboutMe!, birthday!)

    this.userService.updateUserInfo(request).subscribe({
      next: updatedUserDto => {
        console.log(updatedUserDto)
        alert('user data updated successfully')
      },
      error: error => {
        console.log(error)
      }
    })
  }
}
