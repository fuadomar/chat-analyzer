package tone.analyzer.dao;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
import tone.analyzer.domain.entity.EmailInvitation;

/** Created by Dell on 1/17/2018. */
@Component
public class UserAccountDao {

  @Autowired private UserService userService;

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
}
