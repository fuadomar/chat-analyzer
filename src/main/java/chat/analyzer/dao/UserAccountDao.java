package chat.analyzer.dao;

import chat.analyzer.auth.service.UserService;
import chat.analyzer.domain.entity.BuddyDetails;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.model.LoginEvent;
import chat.analyzer.domain.repository.UserAccountRepository;

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
    Set<BuddyDetails> emailInvitationSenderBuddyList = userEmailInvitationSender.getBuddyList();

    if (emailInvitationSenderBuddyList == null) {
      emailInvitationSenderBuddyList = new HashSet<>();
    }

    emailInvitationReceiverBuddyList.add(
        new BuddyDetails(userEmailInvitationSender.getId(), token.getSender()));
    emailInvitationSenderBuddyList.add(
        new BuddyDetails(receiverUserAccount.getId(), userAccount.getName()));
    userEmailInvitationSender.setBuddyList(emailInvitationSenderBuddyList);
    receiverUserAccount.setBuddyList(emailInvitationReceiverBuddyList);
    userService.addBuddyToUserBuddyList(userEmailInvitationSender, receiverUserAccount);
  }

  public List<LoginEvent> findBuddyList(
      String userName, boolean completeBuddyListWithOnlinePresence) {

    UserAccount userUserAccount = userAccountRepository.findByName(userName);
    List<LoginEvent> buddyListAsLoginEvents = new ArrayList<>();
    Set<BuddyDetails> buddyList = userUserAccount.getBuddyList();
    List<LoginEvent> onlineBuddyListAsLoginEvents = new ArrayList<>();

    if (buddyList == null) {
      return buddyListAsLoginEvents;
    }

    buddyList.forEach(
        buddy -> {
          LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
          UserAccount friendUserAccount = userUserAccountRepository.findByName(buddy.getName());
          if (friendUserAccount == null) {
            return;
          }
          loginEvent.setId(buddy.getId());
          loginEvent.setProfileImage(
              friendUserAccount.getDocumentMetaData() != null
                  ? friendUserAccount.getDocumentMetaData().getThumbNail()
                  : "");
          buddyListAsLoginEvents.add(loginEvent);
        });

    simpUserRegistry
        .getUsers()
        .forEach(
            currentUser -> {
              LOG.info("currently online: {}", currentUser.getName());
              if (buddyList.contains(
                  new BuddyDetails(currentUser.getName(), currentUser.getName()))) {
                int indexOnlineUser =
                    buddyListAsLoginEvents.indexOf(new LoginEvent(currentUser.getName(), true));
                if (indexOnlineUser == -1) {
                  return;
                }
                if (completeBuddyListWithOnlinePresence) {
                  LoginEvent loginEvent = buddyListAsLoginEvents.get(indexOnlineUser);
                  loginEvent.setOnline(true);
                } else {
                  LoginEvent loginEvent = buddyListAsLoginEvents.get(indexOnlineUser);
                  loginEvent.setOnline(true);
                  onlineBuddyListAsLoginEvents.add(loginEvent);
                }
              }
            });

    if (completeBuddyListWithOnlinePresence) {
      return buddyListAsLoginEvents;
    }
    return onlineBuddyListAsLoginEvents;
  }

  public UserAccount findByName(String userId) {

    return userAccountRepository.findByName(userId);
  }
}
