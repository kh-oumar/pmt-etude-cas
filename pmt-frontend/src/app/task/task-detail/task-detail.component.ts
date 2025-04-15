import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Task, TaskService, TaskHistory  } from '../task.service';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.css']
})
export class TaskDetailComponent implements OnInit {
  task!: Task;
  history: TaskHistory[] = [];
  taskForm!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
  ) {}

  ngOnInit(): void {
    const taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.taskService.getTaskById(taskId).subscribe({
      next: (data) => {
        this.task = data;
        this.loadHistory(taskId);
      },
      error: (err) => console.error('Erreur lors de la récupération de la tâche', err)
    });
  }

  loadHistory(taskId: number): void {
    this.taskService.getTaskHistory(taskId).subscribe({
      next: (data) => {
        this.history = data;
      },
      error: (err) => console.error('Erreur lors de la récupération de l\'historique', err)
    });
  }

  goBack(): void {
    window.history.back();
  }
}
