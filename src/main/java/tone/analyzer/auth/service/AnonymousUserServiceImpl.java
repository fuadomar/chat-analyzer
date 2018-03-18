package tone.analyzer.auth.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.AccountRepository;

@Service
public class AnonymousUserServiceImpl implements UserService {

    @Autowired
    private AccountRepository userRepository;

    @Override
    public Account save(Account account) {
        account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
        account.setEnabled(true);
        account.setRole(Arrays.asList(new Role("ROLE_ANONYMOUS_CHAT"),
                new Role("ROLE_USER")));
        userRepository.save(account);
        return account;
    }

    @Override
    public Account findByName(String username) {
        return userRepository.findByName(username);
    }

    @Override
    public void addBudyyToUser(Account emailInvitationSender, Account emailInvitationReceiver) {
        userRepository.save(emailInvitationSender);
        userRepository.save(emailInvitationReceiver);
    }
}
