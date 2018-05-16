package chat.analyzer.dao;

import chat.analyzer.auth.service.UserService;
import chat.analyzer.domain.entity.BuddyDetails;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.model.LoginEvent;
import chat.analyzer.domain.repository.UserAccountRepository;
import chat.analyzer.event.PresenceEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.entity.EmailInvitation;

/** Created by Dell on 1/17/2018. */
@Component
public class UserAccountDao {

  private static final Logger LOG = LoggerFactory.getLogger(UserAccountDao.class);

  @Autowired private UserAccountRepository userAccountRepository;

  @Autowired SimpUserRegistry simpUserRegistry;

  @Autowired private UserAccountRepository userUserAccountRepository;

  public void save(UserAccount userAccount) {
    userAccountRepository.save(userAccount);
  }

  public UserAccount findOne(String userId) {
    return userAccountRepository.findOne(userId);
  }

  public void processEmailInvitationAndUpdateBuddyListIfAbsent(
      EmailInvitation token, UserAccount userAccount, UserService userService) {

    UserAccount receiverUserAccount = userService.findByName(userAccount.getName());
    Set<BuddyDetails> emailInvitationReceiverBuddyList = new HashSet<>();

    if (receiverUserAccount != null) {
      emailInvitationReceiverBuddyList = receiverUserAccount.getBuddyList();
    }
    if (receiverUserAccount == null) {
      receiverUserAccount = userService.save(userAccount);
    }

    UserAccount userEmailInvitationSender = userService.findByName(token.getSender());
    Set<BuddyDetails> emailInvitionSenderBuddyList = userEmailInvitationSender.getBuddyList();

    if (emailInvitionSenderBuddyList == null) {
      emailInvitionSenderBuddyList = new HashSet<>();
    }

    emailInvitationReceiverBuddyList.add(
        new BuddyDetails(userEmailInvitationSender.getId(), token.getSender()));
    emailInvitionSenderBuddyList.add(
        new BuddyDetails(receiverUserAccount.getId(), userAccount.getName()));
    userEmailInvitationSender.setBuddyList(emailInvitionSenderBuddyList);
    receiverUserAccount.setBuddyList(emailInvitationReceiverBuddyList);
    userService.addBuddyToUserBuddyList(userEmailInvitationSender, receiverUserAccount);
  }

  public List<LoginEvent> findBuddyList(
      String userName, boolean completeBuddyListWithOnlinePresence) {

    UserAccount userUserAccount = userAccountRepository.findByName(userName);
    List<LoginEvent> buddyObjectList = new ArrayList<>();
    Set<BuddyDetails> buddyList = userUserAccount.getBuddyList();
    List<LoginEvent> onlineBuddyList = new ArrayList<>();

    if (buddyList == null) {
      return buddyObjectList;
    }

    for (BuddyDetails buddy : buddyList) {
      LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
      UserAccount friendUserAccount = userUserAccountRepository.findByName(buddy.getName());
      if (friendUserAccount == null) {
        continue;
      }
      loginEvent.setId(buddy.getId());
      loginEvent.setProfileImage(
          friendUserAccount.getDocumentMetaData() != null
              ? friendUserAccount.getDocumentMetaData().getThumbNail()
              : "");
      buddyObjectList.add(loginEvent);
    }

    for (SimpUser currentUser : simpUserRegistry.getUsers()) {
      LOG.info("currently online: {}", currentUser.getName());
      if (buddyList.contains(new BuddyDetails(currentUser.getName(), currentUser.getName()))) {
        int indexOnlineUser = buddyObjectList.indexOf(new LoginEvent(currentUser.getName(), true));
        if (indexOnlineUser == -1) {
          continue;
        }

        if (completeBuddyListWithOnlinePresence) {
          LoginEvent loginEvent = buddyObjectList.get(indexOnlineUser);
          loginEvent.setOnline(true);
        } else {
          LoginEvent loginEvent = buddyObjectList.get(indexOnlineUser);
          loginEvent.setOnline(true);
          onlineBuddyList.add(loginEvent);
        }
      }
    }
    if (completeBuddyListWithOnlinePresence) {
      return buddyObjectList;
    }
    return onlineBuddyList;
  }

  public UserAccount findByName(String userId) {

    return userAccountRepository.findByName(userId);
  }
}
