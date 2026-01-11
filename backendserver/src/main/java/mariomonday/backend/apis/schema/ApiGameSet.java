package mariomonday.backend.apis.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * A set of games within a bracket, where the winner moves on
 */
@Data
@Builder
public class ApiGameSet implements Comparable<ApiGameSet> {

  @Id
  private String id;

  /**
   * The round this game set is a part of. Round "0" is the final round,
   * round "1" is the semi finals, etc.
   */
  private final Integer roundIndex;

  /**
   * The IDs of the winners of the set
   */
  @Singular
  private List<String> winners;

  /**
   * The IDs of the losers of the set
   */
  @Singular
  private List<String> losers;

  /**
   * The IDs of the teams participating in this set
   */
  @Singular
  private List<String> playerSets;

  /**
   * IDs of the previous game sets that feed into this one.
   */
  @Singular
  private List<String> previousGameSets;

  /**
   * Games within this set. If this is empty, but the set has winners, that is a forfeit.
   */
  @Singular
  private Set<Game> games;

  /**
   * Convert a GameSet object to an API Game Set object.
   * We use sorted lists instead of sets to ensure that order is consistent upon refresh.
   */
  public static ApiGameSet fromGameSet(GameSet gameSet) {
    return ApiGameSet.builder()
      .id(gameSet.getId())
      .winners(gameSet.getWinners().stream().map(PlayerSet::getId).sorted().toList())
      .losers(gameSet.getLosers().stream().map(PlayerSet::getId).sorted().toList())
      .roundIndex(gameSet.getRoundIndex())
      .playerSets(gameSet.getPlayers().stream().map(PlayerSet::getId).sorted().toList())
      .previousGameSets(gameSet.getPreviousGameSets().stream().map(GameSet::getId).sorted().toList())
      .games(gameSet.getGames())
      .build();
  }

  /**
   * Sort by ID. Not an important metric, but makes sorting consistent.
   */
  @Override
  public int compareTo(ApiGameSet other) {
    return this.getId().compareTo(other.getId());
  }
}
