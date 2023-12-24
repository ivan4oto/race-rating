import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor() { }
  handleLogin(token: string) {
    localStorage.setItem('access_token', token);
  }
}
