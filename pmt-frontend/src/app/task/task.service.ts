import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Task {
  id: number;
  project: { id: number };
  name: string;
  description: string;
  dueDate: string;
  priority: number;
  status: string;
  assignedTo?: {
    id: number;
    username: string;
    email: string;
  } | null;
}

export interface TaskHistory {
  id?: number;
  taskId: number;
  user: { id: number, username: string }; 
  changeDate: string;
  changeDescription: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Créer une nouvelle tâche pour un projet donné.
   */
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/api/tasks`, task);
  }

  /**
   * Mettre à jour une tâche existante.
   */
  updateTask(task: Task, taskId: number): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/api/tasks/${taskId}`, task);
  }

  /**
   * Récupérer une tâche par son id.
   */
  getTaskById(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/api/tasks/${taskId}`);
  }

  /**
   * Récupérer la liste des tâches d'un projet.
   */
  getTasksByProjectId(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/api/tasks/project/${projectId}`);
  }

  assignTask(taskId: number, userId: number): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/api/tasks/${taskId}/assign?userId=${userId}`, {});
  }

  getTaskHistory(taskId: number): Observable<TaskHistory[]> {
    return this.http.get<TaskHistory[]>(`${this.apiUrl}/api/tasks/${taskId}/history`);
  }
}