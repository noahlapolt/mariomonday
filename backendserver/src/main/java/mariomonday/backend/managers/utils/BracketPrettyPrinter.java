package mariomonday.backend.managers.utils;

import java.util.Optional;
import java.util.stream.Collectors;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;

public class BracketPrettyPrinter {

  public static String prettyPrintBottomStructure(Bracket bracket) {
    return prettyPrintGameStructure(bracket.getFinalGameSet());
  }

  private static String prettyPrintGameStructure(GameSet gameSet) {
    Optional<String> maybeWinnerString = Optional.of(
      gameSet
        .getWinners()
        .stream()
        .map(ps -> ps.getPlayers().stream().map(Player::getName).collect(Collectors.joining("+")))
        .collect(Collectors.joining(","))
    ).filter(winnerString -> !winnerString.isEmpty());

    Optional<String> maybePlayerString = Optional.of(
      gameSet.getAddedPlayerSets().stream().map(PlayerSet::getName).collect(Collectors.joining(","))
    ).filter(playerString -> !playerString.isEmpty());

    Optional<String> maybePreviousGameString = Optional.of(
      gameSet
        .getPreviousGameSets()
        .stream()
        .map(BracketPrettyPrinter::prettyPrintGameStructure)
        .collect(Collectors.joining(","))
    ).filter(previousGameString -> !previousGameString.isEmpty());
    return String.format(
      "(%s%s%s)",
      maybeWinnerString.map(winnerString -> "w:" + winnerString).orElse(""),
      maybePlayerString.map(playerString -> "p:" + playerString).orElse(""),
      maybePreviousGameString.orElse("")
    );
  }
}
