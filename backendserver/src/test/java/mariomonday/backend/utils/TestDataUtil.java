package mariomonday.backend.utils;

import java.util.List;
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
    return createFakePlayer(UUID.randomUUID().toString());
  }

  public static Player.PlayerBuilder createFakePlayer(String name) {
    return Player.builder().name(name).eloMap(Player.generateStartingEloMap()).id(name);
  }

  public static GameSet.GameSetBuilder createFakeGameSet() {
    return GameSet.builder().id(UUID.randomUUID().toString());
  }

  public static PlayerSet.PlayerSetBuilder createFakePlayerSet() {
    return createFakePlayerSet("Yo, you seen this guy?");
  }

  public static PlayerSet.PlayerSetBuilder createFakePlayerSet(String name) {
    return PlayerSet.builder().players(Set.of(createFakePlayer(name).build())).name(name).id(name);
  }

  public static Game.GameBuilder createFakeGame() {
    return Game.builder().id(UUID.randomUUID().toString());
  }

  public static List<PlayerSet> createNFakePlayers(int numberOfPlayers) {
    return IntStream.range(0, numberOfPlayers)
      .boxed()
      .map(num -> createFakePlayerSet("Player " + num).id(String.valueOf(num)).build())
      .toList();
  }
}
