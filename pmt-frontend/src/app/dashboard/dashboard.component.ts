import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProjectService, Project } from '../project/project.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  projects: Project[] = [];
  userRole: string | null = null;
  currentUserId!: number;

  constructor(
    private projectService: ProjectService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = localStorage.getItem('userId');
  
    if (id) {
      this.currentUserId = Number(id);
      
      this.projectService.getUserProjects(this.currentUserId).subscribe({
        next: (data) => {
          this.projects = data;
        },
        error: (err) => {
          console.error("Erreur lors de la récupération des projets de l'utilisateur", err);
        }
      });
    } else {
      console.error("Aucun userId trouvé dans le localStorage");
    }
  }
}
