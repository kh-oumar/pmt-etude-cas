import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService, User } from '../auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: Partial<AuthService>;
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = {
      login: jest.fn()
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [ LoginComponent, ReactiveFormsModule ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('devrait créer le form avec email et password invalides', () => {
    expect(component.loginForm.valid).toBe(false);
    const btn = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(btn.disabled).toBe(true);
  });

  it('devrait appeler authService.login et router.navigate sur succès', () => {
    const fakeUser: User = { id: 1, email: 'a@b.com', username: 'u' };
    (authService.login as jest.Mock).mockReturnValue(of(fakeUser));
    component.loginForm.setValue({ email: 'a@b.com', password: 'pwd' });

    component.onSubmit();
    expect(authService.login).toHaveBeenCalledWith({ email: 'a@b.com', password: 'pwd' });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.errorMessage).toBe('');
  });

  it('devrait gérer l’erreur et afficher errorMessage', () => {
    (authService.login as jest.Mock).mockReturnValue(throwError(() => new Error('fail')));
    component.loginForm.setValue({ email: 'x@x.com', password: 'bad' });

    component.onSubmit();
    expect(authService.login).toHaveBeenCalled();
    expect(component.errorMessage).toBe('Échec de la connexion. Vérifiez vos identifiants.');
  });
});
