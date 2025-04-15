// src/app/project/project-detail/project-detail.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectDetailComponent } from './project-detail.component';
import { of } from 'rxjs';
import { ProjectService } from '../project.service';
import { TaskService } from '../../task/task.service';
import { RouterTestingModule } from '@angular/router/testing';

const projectServiceMock = {
  getProjectById: jest.fn().mockReturnValue(of({ id: 1, name: 'Project Test', description: 'Test projet', startDate: '2025-04-10' })),
  getProjectMembers: jest.fn().mockReturnValue(of([
    { user: { id: 1, username: 'alice', email: 'alice@example.com' }, role: { name: 'ADMIN' } }
  ]))
};

const taskServiceMock = {
  getTasksByProjectId: jest.fn().mockReturnValue(of([
    { id: 1, name: 'Task 1', description: 'Description 1', dueDate: '2025-04-20', priority: 3, status: 'to-do' }
  ]))
};

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;

  beforeEach(async () => {
    // Pour simuler l'utilisateur connecté
    localStorage.setItem('userId', '1');
    localStorage.setItem('userRole', 'ADMIN');

    await TestBed.configureTestingModule({
        imports: [ProjectDetailComponent, RouterTestingModule],
        providers: [
          { provide: ProjectService, useValue: projectServiceMock },
          { provide: TaskService, useValue: taskServiceMock }
        ]
      }).compileComponents();

    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load project details, tasks, and members', () => {
    expect(component).toBeTruthy();
    expect(component.project.name).toBe('Project Test');
    expect(component.tasks.length).toBeGreaterThan(0);
    expect(component.members.length).toBeGreaterThan(0);
  });

  it('should filter tasks based on selected status', () => {
    // Simulez une modification du filtre
    component.selectedStatus = 'to-do';
    component.onStatusChange();
    expect(component.filteredTasks.length).toBeGreaterThan(0);
    // Vous pouvez ajouter d'autres vérifications en fonction de votre logique de filtrage.
  });
});
