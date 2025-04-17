import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService, User } from '../auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: Partial<AuthService>;
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = {
      signup: jest.fn()
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [ RegisterComponent, ReactiveFormsModule ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('devrait créer le form avec champs invalides', () => {
    expect(component.registerForm.valid).toBe(false);
    const btn = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(btn.disabled).toBe(true);
  });

  it('devrait appeler authService.signup et router.navigate sur succès', () => {
    const fakeUser: User = { id: 1, email: 'u@v.com', username: 'user' };
    (authService.signup as jest.Mock).mockReturnValue(of(fakeUser));
    component.registerForm.setValue({
      username: 'user',
      email: 'u@v.com',
      password: '123456'
    });

    component.onSubmit();
    expect(authService.signup).toHaveBeenCalledWith({
      username: 'user',
      email: 'u@v.com',
      password: '123456'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.errorMessage).toBe('');
  });

  it('devrait gérer l’erreur et afficher errorMessage', () => {
    (authService.signup as jest.Mock).mockReturnValue(throwError(() => new Error('fail')));
    component.registerForm.setValue({
      username: 'user',
      email: 'u@v.com',
      password: '123456'
    });

    component.onSubmit();
    expect(authService.signup).toHaveBeenCalled();
    expect(component.errorMessage).toBe("Une erreur est survenue lors de l'inscription.");
  });
});
