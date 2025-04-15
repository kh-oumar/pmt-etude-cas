import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskDetailComponent } from './task-detail.component';
import { of } from 'rxjs';
import { TaskService } from '../task.service';
import { ActivatedRoute } from '@angular/router';

const taskServiceMock = {
  getTaskById: jest.fn().mockReturnValue(of({
    id: 1,
    name: 'Task Detail Test',
    description: 'Detail description',
    dueDate: '2025-04-20',
    priority: 3,
    status: 'to-do',
    assignedTo: { username: 'alice' }
  })),
  getTaskHistory: jest.fn().mockReturnValue(of([]))
};

const activatedRouteMock = {
  snapshot: { paramMap: { get: () => '1' } }
};

describe('TaskDetailComponent', () => {
  let component: TaskDetailComponent;
  let fixture: ComponentFixture<TaskDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskDetailComponent],
      providers: [
        { provide: TaskService, useValue: taskServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load task details and history', () => {
    expect(component).toBeTruthy();
    expect(component.task.name).toBe('Task Detail Test');
    expect(taskServiceMock.getTaskHistory).toHaveBeenCalledWith(1);
  });
});
