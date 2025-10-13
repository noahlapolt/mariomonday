package mariomonday.backend.database.tables;

import java.util.List;
import mariomonday.backend.database.schema.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Table for games
 */
public interface GameRepository extends MongoRepository<Game, String> {
  /**
   * Query games by set ID
   *
   * @param setId The ID of the set
   * @return The games associated with the given set
   */
  List<Game> findBySetId(String setId);

  /**
   * Query games by participants
   *
   * @param playerId The player
   * @return The games that player has played
   */
  List<Game> findByPlayersContains(String playerId);

  /**
   * Query games by winners
   *
   * @param playerId The player
   * @return The games that player has won
   */
  List<Game> findByWinnersContains(String playerId);
}
