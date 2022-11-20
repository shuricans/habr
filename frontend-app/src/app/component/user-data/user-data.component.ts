import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {UserDto} from "../../model/user-dto";

@Component({
  selector: 'app-user-data',
  templateUrl: './user-data.component.html',
  styleUrls: ['./user-data.component.scss']
})
export class UserDataComponent implements OnInit {

  userDto?: UserDto;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.userService.getUserData().subscribe({
      next: userDto => {
        console.log(userDto)
        this.userDto = userDto;
      }
    });
  }

  submitForm() {

  }
}
