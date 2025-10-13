package mariomonday.backend.database.tables;

import java.util.List;
import mariomonday.backend.database.schema.GameSet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameSetRepository extends MongoRepository<GameSet, String> {
  /**
   * Query sets by bracket ID
   *
   * @param bracketId The ID of the bracket
   * @return The sets that are associated with the given bracket
   */
  List<GameSet> findByBracketId(String bracketId);

  /**
   * Query sets by participants
   *
   * @param playerId The player
   * @return The sets that player has played
   */
  List<GameSet> findByPlayersContains(String playerId);

  /**
   * Query sets by winners
   *
   * @param playerId The player
   * @return The sets that player has won
   */
  List<GameSet> findByWinnersContains(String playerId);
}
