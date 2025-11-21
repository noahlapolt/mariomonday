package mariomonday.backend.managers.tournamentcreators;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.time.Clock;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxSetsStrategyCreator extends AbstractBracketCreator {

  private final Clock clock;

  public MaxSetsStrategyCreator(@Autowired Clock clock) {
    this.clock = clock;
  }

  @Override
  public Bracket fromPlayerSets(GameType gameType, List<PlayerSet> playerSets) {
    int numPlayerSets = playerSets.size();
    int numLeftoverPlayers = numPlayerSets % gameType.getMaxPlayerSets();
    int minGamesNecessary = numPlayerSets / gameType.getMaxPlayerSets() + (numLeftoverPlayers == 0 ? 0 : 1);

    int maxRound = getMaxRound(minGamesNecessary, gameType);

    Bracket.BracketBuilder bracketBuilder = Bracket.builder()
      .teams(playerSets)
      .rounds(maxRound +  1)
      .date(clock.instant())
      .gameType(gameType);

    List<GameOrPlayerSet> currentRound = wrapPlayers(playerSets);

    ImmutableList.Builder<GameSet> allGamesBuilder = ImmutableList.builder();

    for (int i = maxRound; i >= 0; i--) {
      currentRound = createNextRound(gameType, i, currentRound);
      allGamesBuilder.addAll(
        currentRound.stream().filter(GameOrPlayerSet::isGameSet).map(GameOrPlayerSet::getGameSet).toList()
      );
    }

    return bracketBuilder.gameSets(allGamesBuilder.build()).build();
  }

  @VisibleForTesting
  List<GameOrPlayerSet> createNextRound(GameType gameType, int roundNumber, List<GameOrPlayerSet> previousRound) {
    int numGamesThisRound = (int) Math.pow(gameType.getNumPreviousGames(), roundNumber);
    if (previousRound.size() < numGamesThisRound) {
      throw new IllegalArgumentException("Not enough games/player sets in previous round");
    }

    ImmutableList.Builder<GameOrPlayerSet> currentRound = ImmutableList.builder();

    int roundTripLength = numGamesThisRound * 2;

    for (int i = 1; i <= numGamesThisRound; i++) {
      int gameOrPlayerEloRanking = i;
      int numToAdd = roundTripLength + 1 - 2 * gameOrPlayerEloRanking;

      ImmutableList.Builder<GameOrPlayerSet> previousGops = ImmutableList.builder();

      while (gameOrPlayerEloRanking <= previousRound.size()) {
        previousGops.add(previousRound.get(gameOrPlayerEloRanking - 1));
        gameOrPlayerEloRanking += numToAdd;
        numToAdd = roundTripLength - numToAdd;
      }

      currentRound.add(
        GameOrPlayerSet.builder().gameSet(createGameSet(previousGops.build(), gameType, roundNumber)).build()
      );
    }

    return currentRound.build();
  }

  private GameSet createGameSet(List<GameOrPlayerSet> inputs, GameType gameType, int roundIndex) {
    return GameSet.builder()
      .gameType(gameType)
      .roundIndex(roundIndex)
      .previousGameSets(
        inputs
          .stream()
          .filter(GameOrPlayerSet::isGameSet)
          .map(gops -> gops.gameSet)
          .collect(ImmutableList.toImmutableList())
      )
      .addedPlayerSets(
        inputs
          .stream()
          .filter(gops -> !gops.isGameSet())
          .map(gops -> gops.playerSet)
          .collect(ImmutableList.toImmutableList())
      )
      .build();
  }

  @VisibleForTesting
  List<GameOrPlayerSet> wrapPlayers(List<PlayerSet> playerSets) {
    return playerSets
      .stream()
      .map(ps -> GameOrPlayerSet.builder().playerSet(ps).build())
      .toList();
  }

  @Data
  @Builder
  @VisibleForTesting
  static class GameOrPlayerSet {

    GameSet gameSet;
    PlayerSet playerSet;

    private enum Type {
      GAME_SET,
      PLAYER_SET,
    }

    Type getType() {
      if (gameSet != null) {
        return Type.GAME_SET;
      } else if (playerSet != null) {
        return Type.PLAYER_SET;
      } else {
        throw new IllegalStateException("Both gameSet and playerSet are null");
      }
    }

    boolean isGameSet() {
      return getType() == Type.GAME_SET;
    }
  }
}
