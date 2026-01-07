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
      var nextRound = gameSetsByRound.get(i - 1);
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
    return result
      .values()
      .stream()
      .map(round -> round.stream().sorted().toList())
      .toList();
  }
}
