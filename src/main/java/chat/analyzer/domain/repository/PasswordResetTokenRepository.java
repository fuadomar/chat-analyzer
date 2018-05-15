package chat.analyzer.domain.repository;

import chat.analyzer.domain.entity.PasswordResetToken;
import chat.analyzer.domain.entity.UserAccount;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

  PasswordResetToken findByToken(String token);

  PasswordResetToken findByUser(UserAccount user);

  Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

  void deleteByExpiryDateLessThan(Date now);
}
