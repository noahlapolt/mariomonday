package mariomonday.backend.database.schema;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

/**
 * A single player, consistent across games
 */
@Data
@Builder
@Document
public class Player {

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
}