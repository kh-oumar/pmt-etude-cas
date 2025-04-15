import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface Invitation {
  id?: number;
  project?: any;
  email: string;
  token?: string;
  invitedBy?: any;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class InvitationService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Méthode pour envoyer l'invitation
  sendInvitation(projectId: number, inviterId: number, invitation: Invitation): Observable<Invitation> {
    return this.http.post<Invitation>(
      `${this.apiUrl}/api/invitations?projectId=${projectId}&inviterId=${inviterId}`,
      invitation
    );
  }

  // Méthode pour accepter l'invitation par token
  acceptInvitation(token: string): Observable<Invitation> {
    return this.http.post<Invitation>(`${this.apiUrl}/api/invitations/accept?token=${token}`, {});
  }

  // Méthode combinée : envoyer l'invitation puis l'accepter automatiquement
  sendAndAcceptInvitation(projectId: number, inviterId: number, invitation: Invitation): Observable<Invitation> {
    return this.sendInvitation(projectId, inviterId, invitation).pipe(
      switchMap((invitationResponse: Invitation) => {
        // Si le token est présent dans la réponse, enchaîner avec acceptInvitation
        if (invitationResponse.token) {
          return this.acceptInvitation(invitationResponse.token);
        }
        // Sinon, renvoyer directement l'invitationResponse
        return new Observable<Invitation>(subscriber => {
          subscriber.next(invitationResponse);
          subscriber.complete();
        });
      })
    );
  }
}
