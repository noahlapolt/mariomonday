package mariomonday.backend.apis.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import org.springframework.data.annotation.Id;

/**
 * Variant of a bracket that is sent over API, designed to be more readable for the frontend
 */
@Data
@Builder
public class ApiBracket {

  /**
   * ID for the bracket
   */
  @Id
  private final String id;

  /**
   * The moment the bracket started
   */
  @NonNull
  private final Instant date;

  /**
   * The number of rounds in the bracket
   */
  private int rounds;

  /**
   * The winners
   */
  private Set<Player> winners;

  /**
   * The type of game this bracket was for
   */
  private final GameType gameType;

  /**
   * The teams who participated in this bracket. Teams can be a single player
   */
  private Set<PlayerSet> teams;

  /**
   * The sets in this bracket, as a list of sets.
   * The first list is all game sets in round one, the second all game sets in round two, etc.
   */
  private List<List<GameSet>> gameSets;

  public static ApiBracket fromBracket(Bracket bracket) {
    var loadedBracket = Bracket.loadLazyBracket(bracket);
    return ApiBracket.builder()
      .id(loadedBracket.getId())
      .date(loadedBracket.getDate())
      .rounds(loadedBracket.getRounds())
      .winners(loadedBracket.getWinners())
      .gameType(loadedBracket.getGameType())
      .teams(loadedBracket.getTeams())
      .gameSets(orderGameSets(loadedBracket.getGameSets()))
      .build();
  }

  /**
   * Order the game sets in a consistent manner as described in
   * {@link mariomonday.backend.apis.schema.ApiBracket#gameSets gameSets}
   */
  private static List<List<GameSet>> orderGameSets(Set<GameSet> gameSets) {
    return groupGameSetsByRound(gameSets)
      .entrySet()
      .stream()
      .sorted(Entry.comparingByKey(Comparator.reverseOrder()))
      .map(Entry::getValue)
      .map(sets -> sets.stream().sorted().toList())
      .toList();
  }

  /**
   * Group game sets by round index in a map
   */
  private static Map<Integer, List<GameSet>> groupGameSetsByRound(Set<GameSet> gameSets) {
    var result = new HashMap<Integer, List<GameSet>>();
    for (GameSet gameSet : gameSets) {
      int round = gameSet.getRoundIndex();
      if (!result.containsKey(round)) {
        result.put(round, new ArrayList<>());
      }
      result.get(round).add(gameSet);
    }
    return result;
  }
}
