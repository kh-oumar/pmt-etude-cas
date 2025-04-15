import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private loggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('authToken'));
  isLoggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  signup(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/api/auth/signup`, user);
  }

  login(credentials: { email: string; password: string }): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/api/auth/login`, credentials).pipe(
      tap((user: User) => {
        // On simule ici le stockage d'un token réel
        localStorage.setItem('authToken', 'token-fictif');

        // Stocker l'ID de l'utilisateur
        if (user.id) {
          localStorage.setItem('userId', user.id.toString());
        }

        this.loggedIn.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    this.loggedIn.next(false);
  }
}
