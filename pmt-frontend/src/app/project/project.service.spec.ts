import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService, Project, ProjectMember } from './project.service';
import { environment } from '../../environments/environment';

describe('ProjectService', () => {
  let svc: ProjectService;
  let http: HttpTestingController;
  const api = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ ProjectService ]
    });
    svc = TestBed.inject(ProjectService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());

  it('getProjects()', () => {
    const mock: Project[] = [{ id:1, name:'P', description:'D', startDate:'2025-01-01' }];
    svc.getProjects().subscribe(res => expect(res).toEqual(mock));
    const req = http.expectOne(`${api}/api/projects`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('updateMemberRole()', () => {
    const pm: ProjectMember = { id:{userId:2,projectId:1}, user:{id:2,username:'u',email:'e'}, role:{id:3,name:'MEM'} };
    svc.updateMemberRole(1,2,'MEM').subscribe(res => expect(res).toEqual(pm));
    const req = http.expectOne(`${api}/api/projects/1/members/2?role=MEM`);
    expect(req.request.method).toBe('PUT');
    req.flush(pm);
  });

  // tests d’erreur
  it('getProjectById 404', () => {
    svc.getProjectById(5).subscribe({ error: err => expect(err.status).toBe(404) });
    http.expectOne(`${api}/api/projects/5`).flush('err',{ status:404,statusText:'Not Found' });
  });
});
