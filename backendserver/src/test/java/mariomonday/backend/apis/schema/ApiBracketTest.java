package mariomonday.backend.apis.schema;

import java.time.Clock;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreator;
import mariomonday.backend.utils.BaseSpringTest;
import mariomonday.backend.utils.TestDataUtil;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

public class ApiBracketTest extends BaseSpringTest {

  @Spy
  private Clock clock = Clock.systemDefaultZone();

  private List<PlayerSet> players;

  @BeforeEach
  public void setUp() {
    players = TestDataUtil.createNFakePlayers(16);
    playerRepository.insert(
      players
        .stream()
        .map(ps -> ps.getPlayers().stream().findFirst().get())
        .toList()
    );
  }

  @Test
  public void testFromBracket_shouldOrderGameSetsCorrectly() {
    // Setup
    var bracket = generateBracket();
    var apiBracket = ApiBracket.fromBracket(bracket);
    var apiBracket2 = ApiBracket.fromBracket(bracket);

    // Verify
    Assertions.assertEquals(apiBracket.getDate(), bracket.getDate());
    Assertions.assertEquals(apiBracket.getGameType(), bracket.getGameType());
    Assertions.assertEquals(apiBracket.getId(), bracket.getId());
    Assertions.assertEquals(apiBracket.getTeams(), bracket.getTeams());
    Assertions.assertEquals(apiBracket.getRounds(), bracket.getRounds());
    Assertions.assertEquals(apiBracket.getWinners(), bracket.getWinners());
    Assertions.assertEquals(apiBracket.getRounds(), apiBracket.getGameSets().size());
    var totalSize = 0;
    for (int i = 0; i < apiBracket.getRounds(); i++) {
      var round = apiBracket.getGameSets().get(i);
      for (int j = 0; j < round.size(); j++) {
        var set = round.get(j);
        Assertions.assertEquals(apiBracket.getRounds() - i - 1, set.getRoundIndex());
        Assertions.assertEquals("R" + i + " GS" + j, set.getId());
        totalSize += 1;
      }
    }
    Assertions.assertEquals(15, totalSize);

    // Confirm that there is no randomization when creating API bracket from bracket
    Assertions.assertEquals(apiBracket, apiBracket2);
  }

  /**
   * Generate a bracket guaranteeing a specific Game Set ordering to make testing possible
   */
  private Bracket generateBracket() {
    var roundOne = IntStream.range(0, 8)
      .boxed()
      .map(gameNum ->
        GameSet.builder()
          .id("R0 GS" + gameNum)
          .roundIndex(3)
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameSets(List.of())
          .addedPlayerSets(List.of(players.get(gameNum), players.get(gameNum + 1)))
          .build()
      )
      .toList();
    var roundTwo = IntStream.range(0, 4)
      .boxed()
      .map(gameNum ->
        GameSet.builder()
          .id("R1 GS" + gameNum)
          .roundIndex(2)
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameSets(List.of(roundOne.get(gameNum * 2), roundOne.get(gameNum * 2 + 1)))
          .addedPlayerSets(List.of())
          .build()
      )
      .toList();
    var roundThree = IntStream.range(0, 2)
      .boxed()
      .map(gameNum ->
        GameSet.builder()
          .id("R2 GS" + gameNum)
          .roundIndex(1)
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameSets(List.of(roundTwo.get(gameNum * 2), roundTwo.get(gameNum * 2 + 1)))
          .addedPlayerSets(List.of())
          .build()
      )
      .toList();
    var roundFour = IntStream.range(0, 1)
      .boxed()
      .map(gameNum ->
        GameSet.builder()
          .id("R3 GS" + gameNum)
          .roundIndex(0)
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameSets(List.of(roundThree.get(gameNum * 2), roundThree.get(gameNum * 2 + 1)))
          .addedPlayerSets(List.of())
          .build()
      )
      .toList();

    return Bracket.builder()
      .id("Test Bracket")
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .gameSets(
        Stream.of(roundOne, roundTwo, roundThree, roundFour).flatMap(Collection::stream).collect(Collectors.toSet())
      )
      .teams(players)
      .rounds(4)
      .date(Instant.now())
      .winner(players.get(0).getPlayers().stream().findFirst().get())
      .build();
  }
}
