package mariomonday.backend.managers.tournamentcreators;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreator.GameOrPlayerSet;
import mariomonday.backend.utils.TestDataUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreator.GameOrPlayerSet;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreatorTest.PlayerSetsTestData.Inputs;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreatorTest.PlayerSetsTestData.TestGame;
import mariomonday.backend.managers.utils.BracketPrettyPrinter;
import mariomonday.backend.utils.TestDataUtil;
import mariomonday.backend.utils.jsonfilesource.JsonFileSource;
import mariomonday.backend.utils.jsonfilesource.JsonTestData;

@ExtendWith(MockitoExtension.class)
class MaxSetsStrategyCreatorTest {

  @Spy
  private Clock clock = Clock.systemDefaultZone();

  @InjectMocks
  private MaxSetsStrategyCreator maxSetsStrategyCreator;

  public static Stream<Arguments> createNextRoundData() {
    return Stream.of(
      Arguments.of(
        GameType.SMASH_ULTIMATE_SINGLES,
        2,
        8,
        List.of(List.of(1, 8), List.of(2, 7), List.of(3, 6), List.of(4, 5))
      ),
      Arguments.of(
        GameType.SMASH_ULTIMATE_SINGLES,
        3,
        9,
        List.of(List.of(1), List.of(2), List.of(3), List.of(4), List.of(5), List.of(6), List.of(7), List.of(8, 9))
      ),
      Arguments.of(
        GameType.MARIO_KART_8,
        2,
        13,
        List.of(List.of(1, 8, 9), List.of(2, 7, 10), List.of(3, 6, 11), List.of(4, 5, 12, 13))
      )
    );
  }

  @ParameterizedTest(name = "{index} {0} with {2} players in round {1}")
  @MethodSource("createNextRoundData")
  void createNextRoundPlayers(GameType gameType, int round, int numPlayers, List<List<Integer>> expected) {
    List<GameOrPlayerSet> input = IntStream.range(1, numPlayers + 1)
      .boxed()
      .map(String::valueOf)
      .map(playerSetId ->
        GameOrPlayerSet.builder().playerSet(TestDataUtil.createFakePlayerSet().id(playerSetId).build()).build()
      )
      .toList();

    List<GameOrPlayerSet> actual = maxSetsStrategyCreator.createNextRound(gameType, round, input);

    List<List<String>> expectedIds = expected
      .stream()
      .map(list -> list.stream().map(String::valueOf).toList())
      .toList();

    List<List<String>> actualIds = actual
      .stream()
      .map(gop -> gop.getGameSet().getAddedPlayerSets().stream().map(PlayerSet::getId).toList())
      .toList();

    assertThat(actualIds).isEqualTo(expectedIds);
  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  @SuperBuilder
  @Jacksonized
  static class PlayerSetsTestData extends JsonTestData<Inputs, TestGame> {

    @Value
    @Builder
    @Jacksonized
    static class Inputs {

      GameType gameType;

      int numPlayers;
    }

    @Value
    @Builder
    @Jacksonized
    static class TestGame {

      @Singular
      List<TestGame> innerGames;

      @Singular
      Set<Integer> playerSeeds;

      boolean isSameStructureAsGameSet(GameSet gameSet) {
        if (innerGames.isEmpty()) {
          return playerSeeds
            .stream()
            .map(String::valueOf)
            .collect(Collectors.toSet())
            .equals(gameSet.getAddedPlayerSets().stream().map(PlayerSet::getName).collect(Collectors.toSet()));
        } else {
          return (
            innerGames.size() == gameSet.getPreviousGameSets().size() &&
            innerGames
              .stream()
              .allMatch(innerGame ->
                gameSet.getPreviousGameSets().stream().anyMatch(innerGame::isSameStructureAsGameSet)
              )
          );
        }
      }

      @Override
      public String toString() {
        return playerSeeds.isEmpty()
          ? String.format("(%s)", innerGames.stream().map(Object::toString).collect(Collectors.joining(", ")))
          : playerSeeds.toString();
      }
    }
  }

  @ParameterizedTest(name = "{0}")
  @JsonFileSource(PlayerSetsTestData.class)
  void fromPlayerSets(String descriptor, Inputs inputs, TestGame expected) {
    List<PlayerSet> playerSets = IntStream.range(1, inputs.getNumPlayers() + 1)
      .boxed()
      .map(String::valueOf)
      .map(name ->
        TestDataUtil.createFakePlayerSet()
          .clearPlayers()
          .player(TestDataUtil.createFakePlayer().name(name).build())
          .build()
      )
      .toList();

    Bracket actual = maxSetsStrategyCreator.fromPlayerSets(inputs.getGameType(), playerSets);

    assertThat(actual.getFinalGameSet()).matches(
      expected::isSameStructureAsGameSet,
      String.format(
        "Expected:\n%s\n\nActual:\n%s",
        expected.toString(),
        BracketPrettyPrinter.prettyPrintBottomStructure(actual)
      )
    );
  }
}
