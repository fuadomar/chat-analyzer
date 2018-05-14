package tone.analyzer.domain.repository;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.UserAccount;
import tone.analyzer.domain.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

  PasswordResetToken findByToken(String token);

  PasswordResetToken findByUser(UserAccount user);

  Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

  void deleteByExpiryDateLessThan(Date now);
}
