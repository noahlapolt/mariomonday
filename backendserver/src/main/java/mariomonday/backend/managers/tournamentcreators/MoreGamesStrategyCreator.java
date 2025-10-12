package mariomonday.backend.managers.tournamentcreators;

import java.util.List;
import mariomonday.backend.database.schema.Bracket;

public class MoreGamesStrategyCreator extends AbstractBracketCreator {

  @Override
  public Bracket fromPlayerIds(List<String> playerIds) {
    int numPlayers = playerIds.size();

    return null;
  }
}
