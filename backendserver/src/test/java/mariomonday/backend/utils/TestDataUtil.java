package mariomonday.backend.utils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;

public class TestDataUtil {

  public static Player.PlayerBuilder createFakePlayer() {
    var uuid = UUID.randomUUID().toString();
    return Player.builder().name(uuid).eloMap(Player.generateStartingEloMap()).id(uuid);
  }

  public static GameSet.GameSetBuilder createFakeGameSet() {
    return GameSet.builder().id(UUID.randomUUID().toString());
  }

  public static PlayerSet.PlayerSetBuilder createFakePlayerSet() {
    return PlayerSet.builder()
      .players(Set.of(createFakePlayer().build()))
      .name("Yo, you seen this guy?")
      .id(UUID.randomUUID().toString());
  }

  public static Game.GameBuilder createFakeGame() {
    return Game.builder().id(UUID.randomUUID().toString());
  }

  public static Set<PlayerSet> createNFakePlayers(int numberOfPlayers) {
    return IntStream.range(0, numberOfPlayers)
      .boxed()
      .map(num -> createFakePlayerSet().id(String.valueOf(num)).build())
      .collect(Collectors.toSet());
  }
}
