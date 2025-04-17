import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService, Task, TaskHistory } from './task.service';
import { environment } from '../../environments/environment';

describe('TaskService', () => {
  let service: TaskService;
  let http: HttpTestingController;
  const api = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('should create a task via POST', () => {
    const mock: Task = { id: 1, project: { id: 2 }, name: 'T1', description: 'D', dueDate: '2025-04-01', priority: 1, status: 'todo' };
    service.createTask(mock).subscribe(res => expect(res).toEqual(mock));

    const req = http.expectOne(`${api}/api/tasks`);
    expect(req.request.method).toBe('POST');
    req.flush(mock);
  });

  it('should update a task via PUT', () => {
    const mock: Task = { id: 1, project: { id: 2 }, name: 'T2', description: 'D2', dueDate: '2025-05-01', priority: 2, status: 'done' };
    service.updateTask(mock, 1).subscribe(res => expect(res).toEqual(mock));

    const req = http.expectOne(`${api}/api/tasks/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(mock);
  });

  it('should get a task by id via GET', () => {
    const mock: Task = { id: 3, project: { id: 2 }, name: 'T3', description: 'D3', dueDate: '2025-06-01', priority: 3, status: 'in-progress' };
    service.getTaskById(3).subscribe(res => expect(res).toEqual(mock));

    const req = http.expectOne(`${api}/api/tasks/3`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('should get tasks by project id via GET', () => {
    const list: Task[] = [ { id:4, project:{id:99}, name:'T4', description:'D4', dueDate:'2025-07-01', priority:4, status:'open' } ];
    service.getTasksByProjectId(99).subscribe(res => expect(res).toEqual(list));

    const req = http.expectOne(`${api}/api/tasks/project/99`);
    expect(req.request.method).toBe('GET');
    req.flush(list);
  });

  it('should assign a task via PUT', () => {
    const updated: Task = { id:5, project:{id:2}, name:'T5', description:'D5', dueDate:'2025-08-01', priority:5, status:'todo', assignedTo:{ id:10, username:'u', email:'e' } };
    service.assignTask(5,10).subscribe(res => expect(res).toEqual(updated));

    const req = http.expectOne(`${api}/api/tasks/5/assign?userId=10`);
    expect(req.request.method).toBe('PUT');
    req.flush(updated);
  });

  it('should get task history via GET', () => {
    const hist: TaskHistory[] = [ { id:1, taskId:5, user:{id:10,username:'u'}, changeDate:'2025-04-01T00:00:00', changeDescription:'created' } ];
    service.getTaskHistory(5).subscribe(res => expect(res).toEqual(hist));

    const req = http.expectOne(`${api}/api/tasks/5/history`);
    expect(req.request.method).toBe('GET');
    req.flush(hist);
  });
});