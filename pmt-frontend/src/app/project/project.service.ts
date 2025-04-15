import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Project {
  id?: number;
  name: string;
  description: string;
  startDate: string;
}

export interface ProjectMember {
  id: {
    userId: number;
    projectId: number;
  };
  user: {
    id: number;
    username: string;
    email: string;
  };
  role: {
    id: number;
    name: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Récupère tous les projets
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/api/projects`);
  }

  // Créer un nouveau projet en associant l'utilisateur créateur en tant qu'ADMIN
  createProject(project: Project, creatorId: number): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/api/projects?creatorId=${creatorId}`, project);
  }

  // Récupère les projets associés à un utilisateur
  getUserProjects(userId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/api/projects/users/${userId}/projects`);
  }

  // Récupère la liste des membres d'un projet
  getProjectMembers(projectId: number): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(`${this.apiUrl}/api/projects/${projectId}/members`);
  }

  // Récupère les détails d'un projet par son identifiant
  getProjectById(projectId: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/api/projects/${projectId}`);
  }

  updateMemberRole(projectId: number, userId: number, role: string): Observable<ProjectMember> {
    return this.http.put<ProjectMember>(`${this.apiUrl}/api/projects/${projectId}/members/${userId}?role=${role}`, {});
  }
}
