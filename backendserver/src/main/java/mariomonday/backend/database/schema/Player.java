package mariomonday.backend.database.schema;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A single player, consistent across games
 */
@Data
@Builder
@Document
public class Player {

  /**
   * ELO new players start with, for all games
   */
  public static final int STARTING_ELO = 1500;

  /**
   * Player id
   */
  @Id
  private final String id;

  /**
   * Player name, must be unique
   */
  @Indexed(unique = true)
  private String name;

  private Map<GameType, Integer> eloMap;

  public static Map<GameType, Integer> generateStartingEloMap() {
    return Arrays.stream(GameType.values()).collect(
      Collectors.toMap(gameType -> gameType, gameType -> Player.STARTING_ELO)
    );
  }
}
