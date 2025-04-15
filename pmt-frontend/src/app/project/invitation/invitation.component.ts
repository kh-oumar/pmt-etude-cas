import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { InvitationService, Invitation } from './invitation.service';

@Component({
  selector: 'app-invitation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './invitation.component.html',
  styleUrls: ['./invitation.component.css']
})
export class InvitationComponent {
  @Input() projectId!: number;
  @Input() inviterId!: number;
  @Output() memberAdded = new EventEmitter<void>();

  invitationForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private invitationService: InvitationService
  ) {
    this.invitationForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.invitationForm.valid) {
      const invitation: Invitation = this.invitationForm.value;
      this.invitationService.sendAndAcceptInvitation(this.projectId, this.inviterId, invitation)
        .subscribe({
          next: (data) => {
            this.successMessage = 'Invitation envoyée et acceptée avec succès !';
            this.errorMessage = '';
            this.invitationForm.reset();
            this.memberAdded.emit();
          },
          error: (err) => {
            console.error('Erreur lors de l\'envoi de l\'invitation', err);
            this.errorMessage = 'Erreur lors de l\'envoi de l\'invitation';
            this.successMessage = '';
          }
        });
    }
  }
}
