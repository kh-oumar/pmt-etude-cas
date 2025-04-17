import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InvitationService, Invitation } from './invitation.service';
import { environment } from '../../../environments/environment';
import { switchMap } from 'rxjs/operators';

describe('InvitationService', () => {
  let service: InvitationService;
  let http: HttpTestingController;
  const api = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [InvitationService]
    });
    service = TestBed.inject(InvitationService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should send an invitation via POST', () => {
    const inv: Invitation = { email:'a@b.com', project:{id:7}, invitedBy:{id:1}, status:'en attente' } as any;
    service.sendInvitation(7,1,inv).subscribe(res => expect(res).toEqual(inv));

    const req = http.expectOne(`${api}/api/invitations?projectId=7&inviterId=1`);
    expect(req.request.method).toBe('POST');
    req.flush(inv);
  });

  it('should accept invitation via POST', () => {
    const token = 'tok';
    const inv: Invitation = { email:'x', token:'tok', status:'acceptée' } as any;
    service.acceptInvitation(token).subscribe(res => expect(res).toEqual(inv));

    const req = http.expectOne(`${api}/api/invitations/accept?token=tok`);
    expect(req.request.method).toBe('POST');
    req.flush(inv);
  });

  it('sendAndAcceptInvitation should chain send and accept', () => {
    const fakeInv: Invitation = { token:'abc', email:'e@f' } as any;
    service.sendAndAcceptInvitation(1,2,fakeInv).subscribe(res => expect(res).toEqual(fakeInv));

    const sendReq = http.expectOne(`${api}/api/invitations?projectId=1&inviterId=2`);
    sendReq.flush(fakeInv);
    const acceptReq = http.expectOne(`${api}/api/invitations/accept?token=abc`);
    acceptReq.flush(fakeInv);
  });
});
