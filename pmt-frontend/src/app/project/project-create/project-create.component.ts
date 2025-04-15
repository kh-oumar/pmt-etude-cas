import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProjectService, Project } from '../project.service';

@Component({
  selector: 'app-project-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './project-create.component.html',
  styleUrls: ['./project-create.component.css']
})
export class ProjectCreateComponent {
  projectForm: FormGroup;
  errorMessage: string = '';


  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private router: Router
  ) {
    // Construction du formulaire avec quelques validations de base.
    this.projectForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      startDate: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const userIdString  = localStorage.getItem('userId');

      if (userIdString  !== null) {
        const creatorId = Number(userIdString);
        
        this.projectService.createProject(this.projectForm.value, creatorId)
          .subscribe({
            next: (project) => {
              this.router.navigate(['/dashboard']);
            },
            error: (err: any) => {
              console.error("Erreur lors de la création du projet", err);
            }
          });
      }
    }
  }


}
