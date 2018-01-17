package tone.analyzer.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.event.PresenceEventListener;

/** Created by Dell on 1/17/2018. */
@Component
public class UserAccountDao {

  @Autowired private UserService userService;

  @Autowired
  private PresenceEventListener presenceEventListener;

  @Autowired
  private AccountRepository accountRepository;

  public void processEmailInvitationAndUpdateBuddyListIfAbsent(
      EmailInvitation token, Account account) {

    Account receiverAccount = userService.findByName(token.getReceiver());
    Set<BuddyDetails> emailInvitationReceiverBuddyList =
        emailInvitationReceiverBuddyList = new HashSet<>();

    if (receiverAccount != null) {
      emailInvitationReceiverBuddyList = receiverAccount.getBuddyList();
    }
    if (receiverAccount == null) receiverAccount = userService.save(account);

    Account userEmailInvitationSender = userService.findByName(token.getSender());
    Set<BuddyDetails> emailInvitionSenderBuddyList = userEmailInvitationSender.getBuddyList();

    if (emailInvitionSenderBuddyList == null) {
      emailInvitionSenderBuddyList = new HashSet<>();
    }

    emailInvitationReceiverBuddyList.add(
        new BuddyDetails(userEmailInvitationSender.getId(), token.getSender()));
    emailInvitionSenderBuddyList.add(
        new BuddyDetails(receiverAccount.getId(), token.getReceiver()));
    userEmailInvitationSender.setBuddyList(emailInvitionSenderBuddyList);
    receiverAccount.setBuddyList(emailInvitationReceiverBuddyList);
    userService.addBudyyToUser(userEmailInvitationSender, receiverAccount);
  }

  public List<LoginEvent> retrieveBuddyList(String userName) {

    Account userAccount = accountRepository.findByName(userName);
    List<LoginEvent> buddyListObjects = new ArrayList<>();
    Set<BuddyDetails> buddyList = userAccount.getBuddyList();
    List<LoginEvent> onlineBuddyList = new ArrayList<>();

    if (buddyList == null) return buddyListObjects;

    for (BuddyDetails buddy : buddyList) {
      LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
      loginEvent.setId(buddy.getId());
      buddyListObjects.add(loginEvent);
    }
    List<LoginEvent> activeUser =
        new ArrayList<>(presenceEventListener.getParticipantRepository().getActiveSessions().values());

    for (LoginEvent loginEvent : buddyListObjects)
      if (activeUser.contains(loginEvent)) {
        loginEvent.setOnline(true);
        onlineBuddyList.add(loginEvent);
      }
    return onlineBuddyList;
  }
}
