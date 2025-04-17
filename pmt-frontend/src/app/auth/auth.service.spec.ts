import { TestBed } from '@angular/core/testing';
import { AuthService, User } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;
  const api = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ AuthService ]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => http.verify());

  it('signup envoie POST et retourne un User', () => {
    const mock: User = { id: 1, username: 'u', email: 'u@u.com' };
    service.signup(mock).subscribe(res => expect(res).toEqual(mock));

    const req = http.expectOne(`${api}/api/auth/signup`);
    expect(req.request.method).toBe('POST');
    req.flush(mock);
  });

  it('login envoie POST, stocke token et userId, et met à jour isLoggedIn$', (done) => {
    const mock: User = { id: 5, username: 'u', email: 'e@e.com' };
    service.isLoggedIn$.subscribe(val => {
      // initial = false, puis true
      if (val) {
        expect(localStorage.getItem('authToken')).toBe('token-fictif');
        expect(localStorage.getItem('userId')).toBe('5');
        done();
      }
    });
    service.login({ email: 'e@e.com', password: 'p' }).subscribe();

    const req = http.expectOne(`${api}/api/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mock);
  });

  it('logout supprime le token, userId et met isLoggedIn$ à false', (done) => {
    // simule un login préalable
    localStorage.setItem('authToken', 'x');
    localStorage.setItem('userId', '10');
    service.isLoggedIn$.subscribe(val => {
      if (!val) {
        expect(localStorage.getItem('authToken')).toBeNull();
        expect(localStorage.getItem('userId')).toBeNull();
        done();
      }
    });
    service.logout();
  });
});
