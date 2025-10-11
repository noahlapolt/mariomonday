package mariomonday.backend.managers.tournamentcreators;

import java.util.List;

import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.Player;

public abstract class AbstractBracketCreator {

  public Bracket fromPlayers(List<Player> people) {
    return fromPlayerIds(people.stream().map(Player::getId).toList());
  }

  public abstract Bracket fromPlayerIds(List<String> playerIds);

}
