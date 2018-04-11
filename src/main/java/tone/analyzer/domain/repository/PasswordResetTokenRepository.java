package tone.analyzer.domain.repository;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends
        MongoRepository<PasswordResetToken, String> {


    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(Account user);

    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

/*  @Modifying
  @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
  void deleteAllExpiredSince(Date now);*/
}
