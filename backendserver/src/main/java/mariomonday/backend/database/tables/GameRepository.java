package mariomonday.backend.database.tables;

import java.util.List;
import mariomonday.backend.database.schema.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Table for games
 */
public interface GameRepository extends MongoRepository<Game, String> {
}
