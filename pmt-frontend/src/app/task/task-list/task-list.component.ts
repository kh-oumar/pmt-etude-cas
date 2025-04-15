import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task, TaskService } from '../task.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <h3>Tâches du projet</h3>
    <ul>
      <li *ngFor="let task of tasks">
        <strong>{{ task.name }}</strong> ({{ task.status }}) - 
        <a [routerLink]="['/tasks', task.id]">Voir les détails</a>
      </li>
    </ul>
  `,
  styles: [`
    ul {
      list-style: none;
      padding: 0;
    }
    li {
      margin-bottom: 10px;
      border-bottom: 1px solid #ccc;
      padding-bottom: 5px;
    }
  `]
})
export class TaskListComponent implements OnInit {
  @Input() projectId!: number;
  tasks: Task[] = [];

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    if (this.projectId) {
      this.taskService.getTasksByProjectId(this.projectId).subscribe({
        next: (data) => this.tasks = data,
        error: (err) => console.error('Erreur lors de la récupération des tâches', err)
      });
    }
  }
}
