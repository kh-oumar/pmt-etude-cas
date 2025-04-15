import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { of } from 'rxjs';
import { ProjectService, Project } from '../project/project.service';
import { AuthService } from '../auth/auth.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let projectServiceMock: Partial<ProjectService>;
  let authServiceMock: Partial<AuthService>;

  beforeEach(async () => {
    // Création d'une simulation (mock) pour ProjectService
    projectServiceMock = {
      getUserProjects: jest.fn().mockReturnValue(
        of([{ id: 1, name: 'Test Project', description: 'Une description', startDate: '2025-04-10' }] as Project[])
      )
    };

    // Pour ce test, nous n'avons pas besoin de simuler des méthodes spécifiques d'AuthService,
    // mais vous pouvez en ajouter si nécessaire.
    authServiceMock = {};

    // Stockez des valeurs dans le localStorage pour simuler un utilisateur connecté.
    localStorage.setItem('userId', '1');

    await TestBed.configureTestingModule({
        imports: [DashboardComponent, RouterTestingModule],
        providers: [
          { provide: ProjectService, useValue: projectServiceMock },
          { provide: AuthService, useValue: authServiceMock }  // Provide a mock for AuthService as necessary
        ]
      }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and load projects', () => {
    expect(component).toBeTruthy();
    expect(projectServiceMock.getUserProjects).toHaveBeenCalledWith(1);
    expect(component.projects.length).toBeGreaterThan(0);
  });
});
