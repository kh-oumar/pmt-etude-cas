import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectCreateComponent } from './project/project-create/project-create.component';
import { ProjectDetailComponent } from './project/project-detail/project-detail.component';
import { TaskDetailComponent } from './task/task-detail/task-detail.component';
import { AppShellComponent } from './app-shell.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: AppShellComponent,  // Conteneur général (Navbar + router-outlet)
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      // Les routes ci-dessous sont protégées par le guard d'authentification
      { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
      { path: 'projects/create', component: ProjectCreateComponent, canActivate: [authGuard] },
      { path: 'projects/:id', component: ProjectDetailComponent, canActivate: [authGuard] },
      { path: 'tasks/:id', component: TaskDetailComponent, canActivate: [authGuard] }
    ]
  }
];
