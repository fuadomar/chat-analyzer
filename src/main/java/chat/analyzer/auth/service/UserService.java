package chat.analyzer.auth.service;

import chat.analyzer.domain.entity.UserAccount;

/** Created by mozammal on 4/18/17. */
public interface UserService {

  UserAccount save(UserAccount userAccount);

  UserAccount findByName(String username);

  void addBuddyToUserBuddyList(
      UserAccount emailInvitationSenser, UserAccount emailInvitationEeceiver);
}
