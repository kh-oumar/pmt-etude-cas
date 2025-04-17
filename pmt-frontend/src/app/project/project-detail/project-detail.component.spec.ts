// src/app/project/project-detail/project-detail.component.spec.ts
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ProjectDetailComponent } from './project-detail.component';
import { ReactiveFormsModule, FormsModule, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

import { ProjectService, Project, ProjectMember } from '../project.service';
import { TaskService, Task } from '../../task/task.service';
import { InvitationService } from '../invitation/invitation.service';

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;

  const fakeRoute = { snapshot: { paramMap: { get: (_: string) => '42' } } };
  const cdRefStub = { detectChanges: jest.fn() };

  const projectMock: Project = { id: 42, name: 'P', description: 'D', startDate: '2025-01-01' };
  const tasksMock: Task[] = [
    { id:1, project:{id:42}, name:'T1', description:'', dueDate:'2025-01-02', priority:1, status:'open' }
  ];
  const membersMock: ProjectMember[] = [
    { id:{userId:99,projectId:42}, user:{id:99,username:'bob',email:'b@x'}, role:{id:1,name:'ADMIN'} }
  ];

  let projectSvc: jest.Mocked<ProjectService>;
  let taskSvc: jest.Mocked<TaskService>;
  let inviteSvc: jest.Mocked<InvitationService>;

  beforeEach(waitForAsync(() => {
    projectSvc = {
      getProjectById: jest.fn().mockReturnValue(of(projectMock)),
      getProjectMembers: jest.fn().mockReturnValue(of(membersMock)),
      updateMemberRole: jest.fn()
    } as any;
    taskSvc = {
      getTasksByProjectId: jest.fn().mockReturnValue(of(tasksMock)),
      createTask: jest.fn(),
      updateTask: jest.fn(),
      assignTask: jest.fn(),
      getTaskHistory: jest.fn()
    } as any;
    inviteSvc = {
      sendInvitation: jest.fn(),
      acceptInvitation: jest.fn(),
      sendAndAcceptInvitation: jest.fn()
    } as any;

    TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        ProjectDetailComponent
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: fakeRoute },
        { provide: ChangeDetectorRef, useValue: cdRefStub },
        { provide: ProjectService, useValue: projectSvc },
        { provide: TaskService, useValue: taskSvc },
        { provide: InvitationService, useValue: inviteSvc }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
    component.taskForm = TestBed.inject(FormBuilder).group({
      name: [''], description: [''], dueDate: [''], priority: [1], status: ['']
    });
    fixture.detectChanges();
  });

  it('should load project, tasks and members on init', () => {
    expect(projectSvc.getProjectById).toHaveBeenCalledWith(42);
    expect(taskSvc.getTasksByProjectId).toHaveBeenCalledWith(42);
    expect(projectSvc.getProjectMembers).toHaveBeenCalledWith(42);
    expect(component.project).toEqual(projectMock);
    expect(component.tasks).toEqual(tasksMock);
    expect(component.members).toEqual(membersMock);

    component.selectedStatus = 'open';
    component.onStatusChange();
    expect(component.filteredTasks.length).toBe(1);

    component.selectedStatus = 'Tous';
    component.onStatusChange();
    expect(component.filteredTasks.length).toBe(1);
  });

  it('should switch to edit mode and populate form', () => {
    component.editTask(tasksMock[0]);
    expect(component.editMode).toBe(true);
    expect(component.taskForm.value.name).toBe(tasksMock[0].name);
  });

  it('should reset form on newTask()', () => {
    component.newTask();
    expect(component.editMode).toBe(false);
    expect(component.taskForm.value.name).toBe('');
  });

  it('should handle loadTasks error', () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    taskSvc.getTasksByProjectId.mockReturnValue(throwError(() => ({ status: 500 })));
    component.loadTasks();
    expect(console.error).toHaveBeenCalled();
  });

  it('should open and confirm role edit', () => {
    component.openRoleEditModal(membersMock[0]);
    expect(component.editingMemberId).toBe(99);
    projectSvc.updateMemberRole.mockReturnValue(of(membersMock[0]));
    component.confirmRoleUpdate(membersMock[0]);
    expect(projectSvc.updateMemberRole).toHaveBeenCalled();
  });

  it('assignTask does nothing if no assignee', () => {
    jest.spyOn(console, 'warn').mockImplementation(() => {});
    component.assignTask(tasksMock[0]);
    expect(console.warn).toHaveBeenCalled();
  });

  it('assignTask success', () => {
    component.selectedAssignees = { [tasksMock[0].id]: 99 };
    taskSvc.assignTask.mockReturnValue(of(tasksMock[0]));
    component.assignTask(tasksMock[0]);
    expect(taskSvc.assignTask).toHaveBeenCalled();
  });
});
