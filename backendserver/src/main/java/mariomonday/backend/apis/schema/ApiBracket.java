package mariomonday.backend.apis.schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import mariomonday.backend.database.schema.Bracket;
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
   * Within each round, game sets are ordered by their next game set. See below:
   * 1---|
   *     |-- 5 ---|
   * 2---|        |
   *              |-- 7
   * 3---|        |
   *     |-- 6 ---|
   * 4---|
   * In round two game 5 comes first,
   * so in round one games 1 and 2 will come first because they feed into game 5.
   * The ordering of games that share their next game set
   * is decided by the order of the "previousGameSets" field for the next game set.
   * In this example, 5 has 1 at index 0 in the "previousGameSet" list, so game 1 comes before game 2.
   * For this reason, the "previousGameSet" field is a List in the ApiGameSet object sorted by ID,
   * even though it is a set in the backend.
   */
  private List<List<ApiGameSet>> gameSets;

  public static ApiBracket fromBracket(Bracket bracket) {
    var loadedBracket = Bracket.loadLazyBracket(bracket);
    return ApiBracket.builder()
      .id(loadedBracket.getId())
      .date(loadedBracket.getDate())
      .rounds(loadedBracket.getRounds())
      .winners(loadedBracket.getWinners())
      .gameType(loadedBracket.getGameType())
      .teams(loadedBracket.getTeams())
      .gameSets(
        orderGameSets(loadedBracket.getGameSets().stream().map(ApiGameSet::fromGameSet).collect(Collectors.toSet()))
      )
      .build();
  }

  /**
   * Order the game sets in a consistent manner as described in
   * {@link mariomonday.backend.apis.schema.ApiBracket#gameSets gameSets}
   */
  private static List<List<ApiGameSet>> orderGameSets(Set<ApiGameSet> gameSets) {
    var gameSetsByRound = groupGameSetsByRound(gameSets);
    var result = new ArrayList<List<ApiGameSet>>();
    // Round index 0 will always only have one game, so we add it first
    result.add(List.of(gameSetsByRound.get(0).get(0)));
    for (int i = 1; i < gameSetsByRound.size(); i++) {
      var currRoundResult = new ArrayList<ApiGameSet>();
      result.add(currRoundResult);
      var currRound = gameSetsByRound.get(i);
      // Make sure to take the sorted version of the next round from the result list
      var nextRound = result.get(i - 1);
      for (int j = 0; j < nextRound.size(); j++) {
        var nextRoundGame = nextRound.get(j);
        for (int k = 0; k < nextRoundGame.getPreviousGameSets().size(); k++) {
          var gsToFind = nextRoundGame.getPreviousGameSets().get(k);
          currRoundResult.add(
            currRound
              .stream()
              .filter(gs -> gs.getId().equals(gsToFind))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("This should never happen"))
          );
        }
      }
    }
    Collections.reverse(result);
    return result;
  }

  /**
   * Group game sets by round index, with each round being unordered
   */
  private static List<List<ApiGameSet>> groupGameSetsByRound(Set<ApiGameSet> gameSets) {
    var result = new HashMap<Integer, List<ApiGameSet>>();
    for (ApiGameSet gameSet : gameSets) {
      int round = gameSet.getRoundIndex();
      if (!result.containsKey(round)) {
        result.put(round, new ArrayList<>());
      }
      result.get(round).add(gameSet);
    }
    return result.values().stream().toList();
  }
}
