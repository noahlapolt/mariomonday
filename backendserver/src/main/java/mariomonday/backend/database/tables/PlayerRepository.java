package mariomonday.backend.database.tables;

import org.springframework.data.mongodb.repository.MongoRepository;

import mariomonday.backend.database.schema.Player;

/**
 * Table for players
 */
public interface PlayerRepository extends MongoRepository<Player, String> {

}
