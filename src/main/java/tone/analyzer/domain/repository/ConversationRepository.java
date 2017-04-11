package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.Conversation;

/**
 * Created by user on 4/11/2017.
 */
public interface ConversationRepository extends MongoRepository<Conversation, String> {



 /*   @Query(
            value =
                    "{ $and: [{'user.ban' : false}, {'user.is_active' :true}, {'user.is_deleted' :false}, {'user.userId': ?0} ,{'profile.is_complete': true} ] }"
    )
    UserDetails findUserByUserIdAndIsActive(Long id);*/
}
