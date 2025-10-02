package mariomonday.backend.schema;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * A single player, consistent across games
 */
@Data
public class Player {

    /** Player id */
    @Id
    private String id;

    private String name;
}