package mariomonday.backend.database.tables;

import mariomonday.backend.database.schema.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Table for players
 */
public interface PlayerRepository extends MongoRepository<Player, String> {}
