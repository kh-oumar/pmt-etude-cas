import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <a routerLink="/dashboard" class="logo">PMT</a>
      <ul class="nav-links">
        <li><a routerLink="/dashboard">Accueil</a></li>
        <li *ngIf="!(authService.isLoggedIn$ | async)">
          <a routerLink="/login">Login</a>
        </li>
        <li *ngIf="!(authService.isLoggedIn$ | async)">
          <a routerLink="/register">Register</a>
        </li>
        <li *ngIf="authService.isLoggedIn$ | async">
          <a (click)="onLogout()">Logout</a>
        </li>
      </ul>
    </nav>
  `,
  styles: [`
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      background-color: #007bff;
      color: white;
    }
    .nav-links {
      list-style: none;
      display: flex;
      gap: 20px;
      margin: 0;
      padding: 0;
    }
    .nav-links a {
      color: white;
      text-decoration: none;
      cursor: pointer;
    }
    .logo {
      font-size: 1.5em;
      font-weight: bold;
    }
  `]
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
