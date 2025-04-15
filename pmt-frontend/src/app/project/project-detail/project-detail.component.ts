// src/app/project/project-detail/project-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms'; // Pour ngModel
import { ProjectService, Project, ProjectMember } from '../project.service';
import { Task, TaskService } from '../../task/task.service';
import { InvitationComponent } from '../invitation/invitation.component';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, InvitationComponent, FormsModule],
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  projectId!: number;
  project: Project | any;
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  members: ProjectMember[] = [];
  
  currentUserId!: number;
  currentUserRole: string | null = null;

  // Filtre par statut
  statuses: string[] = [];
  selectedStatus: string = "Tous";  // Par défaut affiche tous

  selectedTask!: Task | null;
  taskForm!: FormGroup;
  editMode = false;

  selectedAssignees: { [taskId: number]: number | null } = {};

  // Pour le changement de rôle d'un membre
  editingMemberId: number | null = null;
  selectedRole: string = '';
  roles: string[] = ['ADMIN', 'MEMBER', 'OBSERVER'];

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private taskService: TaskService,
    private fb: FormBuilder,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      this.currentUserId = Number(storedUserId);
    } else {
      console.error("Aucun userId trouvé dans le localStorage");
    }

    this.currentUserRole = localStorage.getItem('userRole');

    this.loadProject();
    this.loadTasks();
    this.loadMembers();

    this.taskForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      dueDate: ['', Validators.required],
      priority: [1, Validators.required],
      status: ['', Validators.required]
    });


  }

  loadProject(): void {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: data => this.project = data,
      error: err => console.error('Erreur lors du chargement du projet', err)
    });
  }

  loadTasks(): void {
    this.taskService.getTasksByProjectId(this.projectId).subscribe({
      next: data => {
        this.tasks = [...data];
        this.filteredTasks = [...this.tasks];
        // Générer dynamiquement la liste des statuts présents
        this.statuses = ['Tous', ...Array.from(new Set(this.tasks.map(task => task.status)))];
        this.cdRef.detectChanges();
      },
      error: err => console.error('Erreur lors de la récupération des tâches', err)
    });
  }

  loadMembers(): void {
    this.projectService.getProjectMembers(this.projectId).subscribe({
      next: data => {
        this.members = data;
        const currentMember = this.members.find(member => member.user.id === this.currentUserId);
        this.currentUserRole = currentMember ? currentMember.role.name : "OBSERVER";
        this.cdRef.detectChanges();  
      },
      error: err => console.error('Erreur lors de la récupération des membres', err)
    });
  }

  onStatusChange(): void {
    console.log("Filtre modifié :", this.selectedStatus);
    if (this.selectedStatus === "Tous") {
      this.filteredTasks = [...this.tasks];
    } else {
      this.filteredTasks = this.tasks.filter(task => task.status === this.selectedStatus);
    }
  }

  // Lorsque l'utilisateur clique sur "Modifier" pour une tâche existante, 
  // on passe en mode modification en remplissant le formulaire avec les valeurs de la tâche.
  editTask(task: Task): void {
    this.selectedTask = task;
    this.editMode = true;
    this.taskForm.patchValue({
      name: task.name,
      description: task.description,
      dueDate: task.dueDate,
      priority: task.priority,
      status: task.status
    });
  }

  // Lors du clic sur un bouton "Ajouter une tâche" ou lorsque l'utilisateur décide de créer une nouvelle tâche,
  // on réinitialise le formulaire et on passe en mode création.
  newTask(): void {
    this.selectedTask = null;
    this.editMode = false;  // On peut utiliser false pour indiquer qu'il s'agit d'une création
    this.taskForm.reset({
      name: '',
      description: '',
      dueDate: '',
      priority: 1,
      status: ''
    });
  }

  // Méthode de soumission : selon que selectedTask est défini (modification) ou non (création)
  onSubmit(): void {
    if (this.taskForm.valid) {
      const taskData: Task = { ...this.taskForm.value, project: { id: this.projectId } };
      if (this.selectedTask) {
        // Mise à jour
        const updatedTask: Task = { ...this.selectedTask, ...this.taskForm.value };
        this.taskService.updateTask(updatedTask, this.selectedTask.id!).subscribe({
          next: () => {
            this.loadTasks();
            this.newTask(); // Réinitialiser le formulaire après modification
          },
          error: err => console.error('Erreur lors de la mise à jour de la tâche', err)
        });
      } else {
        // Création
        this.taskService.createTask(taskData).subscribe({
          next: () => {
            this.loadTasks();
            this.newTask(); // On remet le formulaire vide
          },
          error: err => console.error('Erreur lors de la création de la tâche', err)
        });
      }
    }
  }

  cancelEdit(): void {
    this.newTask(); // Réinitialise le formulaire et passe en mode création
  }

  // --- Gestion du changement de rôle des membres ---

  openRoleEditModal(member: ProjectMember): void {
    this.editingMemberId = member.user.id;
    this.selectedRole = member.role.name;
  }

  confirmRoleUpdate(member: ProjectMember): void {
    this.projectService.updateMemberRole(this.projectId, member.user.id, this.selectedRole)
      .subscribe({
         next: updatedMember => {
           // Réinitialiser l'édition et rafraîchir la liste des membres
           this.editingMemberId = null;
           this.loadMembers();
         },
         error: err => console.error("Erreur lors de la mise à jour du rôle", err)
      });
  }

  cancelRoleUpdate(): void {
    this.editingMemberId = null;
  }

  // Méthode pour assigner une tâche à un membre (s'appuie sur le service)
  assignTask(task: Task): void {
    const selectedUserId = this.selectedAssignees[task.id];
    if (!selectedUserId) {
      console.warn("Aucun membre sélectionné pour la tâche", task.id);
      return;
    }
    this.taskService.assignTask(task.id!, selectedUserId).subscribe({
      next: (updatedTask: Task) => {
        this.loadTasks();
        // Réinitialiser la sélection pour cette tâche
        this.selectedAssignees[task.id] = null;
      },
      error: err => console.error("Erreur lors de l'assignation de la tâche", err)
    });
  }

  cancelAssignment(taskId: number): void {
    this.selectedAssignees[taskId] = null;
  }
}
