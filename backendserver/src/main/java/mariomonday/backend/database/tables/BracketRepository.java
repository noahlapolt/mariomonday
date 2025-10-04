package mariomonday.backend.database.tables;

import java.util.List;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameType;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Table for brackets
 */
public interface BracketRepository extends MongoRepository<Bracket, String> {
    List<Bracket> findByGameType(GameType gameType);
}
