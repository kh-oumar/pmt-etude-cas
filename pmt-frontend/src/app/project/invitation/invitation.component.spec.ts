import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InvitationComponent } from './invitation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { InvitationService } from './invitation.service';
import { of, throwError } from 'rxjs';

describe('InvitationComponent', () => {
  let component: InvitationComponent;
  let fixture: ComponentFixture<InvitationComponent>;
  let invitationService: Partial<InvitationService>;

  beforeEach(async () => {
    invitationService = {
      sendAndAcceptInvitation: jest.fn()
    };
    jest.spyOn(console, 'error').mockImplementation(() => {});

    await TestBed.configureTestingModule({
      imports: [
        InvitationComponent,
        ReactiveFormsModule
      ],
      providers: [
        { provide: InvitationService, useValue: invitationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InvitationComponent);
    component = fixture.componentInstance;
    component.projectId = 7;
    component.inviterId = 3;
    fixture.detectChanges();
  });

  it('désactive le bouton submit si le formulaire est invalide', () => {
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(btn.disabled).toBeTruthy();
  });

  it('appelle le service et émet memberAdded en cas de succès', () => {
    component.invitationForm.setValue({ email: 'test@example.com' });
    (invitationService.sendAndAcceptInvitation as jest.Mock).mockReturnValue(of({}));
    const emitSpy = jest.spyOn(component.memberAdded, 'emit');

    component.onSubmit();

    expect(invitationService.sendAndAcceptInvitation)
      .toHaveBeenCalledWith(7, 3, { email: 'test@example.com' });
    expect(component.successMessage)
      .toBe('Invitation envoyée et acceptée avec succès !');
    expect(component.errorMessage).toBe('');
    expect(component.invitationForm.value.email).toBeNull();
    expect(emitSpy).toHaveBeenCalled();
  });

  it('gère l’erreur et affiche errorMessage', () => {
    component.invitationForm.setValue({ email: 'err@example.com' });
    (invitationService.sendAndAcceptInvitation as jest.Mock)
      .mockReturnValue(throwError(() => new Error('fail')));
    const emitSpy = jest.spyOn(component.memberAdded, 'emit');

    component.onSubmit();

    expect(invitationService.sendAndAcceptInvitation).toHaveBeenCalled();
    expect(component.successMessage).toBe('');
    expect(component.errorMessage)
      .toBe("Erreur lors de l'envoi de l'invitation");
    expect(emitSpy).not.toHaveBeenCalled();
  });
});
