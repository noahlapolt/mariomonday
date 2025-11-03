<script lang="ts">
  import PlayerSetRender from "./PlayerSetRender.svelte";
  import { GameTypes } from "./Utils.svelte";

  let {
    bracketId,
  }: {
    bracketId: string;
  } = $props();

  // Get the bracket from the server for now I will make it up.
  const evilBracket: Bracket = {
    id: "",
    date: new Date(),
    gameType: "SMASH_ULTIMATE_SINGLES",
    winners: [],
    losers: [],
    gameSets: [
      [
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
        {
          id: "Game A",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
            {
              id: "Noah",
              name: "Noah",
              players: [
                {
                  id: "Noah",
                  name: "Noah",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: [],
          games: [],
        },
      ],
      [
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
        {
          id: "Game C",
          playerSets: [
            {
              id: "Reed",
              name: "Reed",
              players: [
                {
                  id: "Reed",
                  name: "Reed",
                  eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                },
              ],
            },
          ],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
      ],
      [
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
      ],
      [
        {
          id: "Game C",
          playerSets: [],
          winners: [],
          previousGameSets: ["", ""],
          games: [],
        },
      ],
    ],
  };

  let bracket = $state(evilBracket);
  bracket.gameSets.forEach((round, roundIndex) => {
    round.forEach((gameSet) => {});
  });

  /**
   * Counts up all of the wins from the recorded games and sets the
   * gameSet winners to the playerSets with the most wins.
   * @param gameSet The gameSet to resolve.
   * @param roundIndex The index of the current round.
   * @param gameSetIndex The index of the current game set.
   */
  const resolveGame = (
    gameSet: GameSet,
    roundIndex: number,
    gameSetIndex: number,
  ) => {
    // Seed the totalWinsPerSet with all players.
    let totalWinsPerSet = new Map<
      string,
      { wins: number; playerSet: PlayerSet }
    >();
    gameSet.playerSets.forEach((playerSet) => {
      totalWinsPerSet.set(playerSet.id, { wins: 0, playerSet: playerSet });
    });

    // Count up the wins.
    gameSet.games.forEach((game) => {
      game.winners.forEach((winner) => {
        let winCount = totalWinsPerSet.get(winner.id);
        if (winCount !== undefined) {
          totalWinsPerSet.set(winner.id, {
            wins: winCount.wins + 1,
            playerSet: winCount.playerSet,
          });
        } else {
          // This will should never run, but its here just in case.
          totalWinsPerSet.set(winner.id, { wins: 1, playerSet: winner });
        }
      });
    });

    // Sort by most wins.
    const sortedWins = Array.from(totalWinsPerSet).sort(
      (a, b) => b[1].wins - a[1].wins,
    );

    // Adds the winners.
    for (
      let i = 0;
      i < sortedWins.length &&
      i < GameTypes[bracket.gameType].playerSetsToMoveOn;
      i++
    ) {
      gameSet.winners.push(sortedWins[i][1].playerSet);
    }
    // Adds the losers.
    for (
      let i = GameTypes[bracket.gameType].playerSetsToMoveOn;
      i < sortedWins.length;
      i++
    ) {
      bracket.losers.push(sortedWins[i][1].playerSet);
    }

    // Update any future options
    if (roundIndex + 1 < bracket.gameSets.length) {
      const nextRound = bracket.gameSets[roundIndex + 1];
      const nextSetIndex = Math.floor(
        gameSetIndex /
          (GameTypes[bracket.gameType].maxPlayerSets /
            GameTypes[bracket.gameType].playerSetsToMoveOn),
      );
      if (nextSetIndex < nextRound.length) {
        gameSet.winners.forEach((winner) => {
          nextRound[nextSetIndex].playerSets.push(winner);
        });
      }
    }

    // TODO: Tell the server about the win duh.
  };

  /**
   * Adds a win to the game set for this user. Assumes that the number of winners per game
   * is the same as the number of winners for the set for the game type.
   * @param playerSet The player/team that just got a win.
   */
  const addWin = (gameSet: GameSet, playerSet: PlayerSet) => {
    // Adds to the last game if more than one player/team is meant to move forward.
    const lastGameIndex = gameSet.games.length - 1;
    if (
      lastGameIndex > -1 &&
      gameSet.games[lastGameIndex].winners.length <
        GameTypes[bracket.gameType].playerSetsToMoveOn
    ) {
      gameSet.games[lastGameIndex].winners.push(playerSet);
    } else {
      gameSet.games.push({
        id: playerSet.players[0].name, // TODO: What should I do with Ids?
        winners: [playerSet],
        playerSets: gameSet.playerSets,
      });
    }

    // TODO: Send info back to the server about the game result.
    // Patch the last game with the response from the server.
  };

  const removeWin = () => {};

  const revivePlayer = () => {
    // Select a random loser from any previous round.
    // Add them to addedPlayerSets and let the server know.
    // Let Jake exclude people or skip someone.
  };

  /**
   * Gets the number of wins this player/team currently has.
   * @param playerSet The player/team to get wins for.
   * @param gameSet The current gameSet to look in.
   * @returns The number of wins the player/team has.
   */
  const getWins = (gameSet: GameSet, playerSet: PlayerSet): number => {
    let wins = 0;

    gameSet.games.forEach((game) => {
      game.winners.forEach((winner) => {
        if (winner.id === playerSet.id) wins++;
      });
    });

    return wins;
  };

  /**
   * If a button should be disabled.
   * @param gameSet
   */
  const isDisabled = (gameSet: GameSet): boolean => {
    return (
      gameSet.winners.length ===
        GameTypes[bracket.gameType].playerSetsToMoveOn ||
      gameSet.playerSets.length === 0
    );
  };
</script>

<div class="bracket">
  <h1>
    {bracket.gameType
      .toLowerCase()
      .split("_")
      .reduce((prev, curr) => {
        const capPrev = `${prev.charAt(0).toUpperCase()}${prev.substring(1, prev.length)}`;
        const capCurr = `${curr.charAt(0).toUpperCase()}${curr.substring(1, curr.length)}`;
        return `${capPrev} ${capCurr}`;
      })}
  </h1>
  <b>{bracket.date.toDateString()}</b>
  <div class="rounds">
    {#each bracket.gameSets as round, roundIndex}
      <div class="round">
        <h2>Round {roundIndex + 1}</h2>
        <!-- This could be optimized. We loop through all of the game sets even if we only need one. -->
        <div class="gameSets">
          {#each round as gameSet, gameSetIndex}
            <div class="gameSet">
              {#if roundIndex !== 0 && gameSet.previousGameSets.length > 1}
                <div
                  class="verticalLine"
                  style={`height: ${(1 / bracket.gameSets[roundIndex - 1].length) * 100}%;`}
                ></div>
              {/if}
              {#if roundIndex !== 0}
                <div class="horizontalLine"></div>
              {/if}
              <div class="set">
                {#each gameSet.playerSets as playerSet}
                  <button
                    aria-label={`Add a win for ${playerSet.name}.`}
                    onclick={() => {
                      addWin(gameSet, playerSet);
                    }}
                    disabled={isDisabled(gameSet)}
                  >
                    <PlayerSetRender {playerSet} gameType={bracket.gameType} />
                    <div>
                      {#each { length: getWins(gameSet, playerSet) }}
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 640 640"
                        >
                          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                          <path
                            d="M64 320C64 178.6 178.6 64 320 64C461.4 64 576 178.6 576 320C576 461.4 461.4 576 320 576C178.6 576 64 461.4 64 320z"
                          />
                        </svg>
                      {/each}
                      {#if gameSet.winners.find((winner) => winner.id === playerSet.id) !== undefined}
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 640 640"
                        >
                          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                          <path
                            d="M345 151.2C354.2 143.9 360 132.6 360 120C360 97.9 342.1 80 320 80C297.9 80 280 97.9 280 120C280 132.6 285.9 143.9 295 151.2L226.6 258.8C216.6 274.5 195.3 278.4 180.4 267.2L120.9 222.7C125.4 216.3 128 208.4 128 200C128 177.9 110.1 160 88 160C65.9 160 48 177.9 48 200C48 221.8 65.5 239.6 87.2 240L119.8 457.5C124.5 488.8 151.4 512 183.1 512L456.9 512C488.6 512 515.5 488.8 520.2 457.5L552.8 240C574.5 239.6 592 221.8 592 200C592 177.9 574.1 160 552 160C529.9 160 512 177.9 512 200C512 208.4 514.6 216.3 519.1 222.7L459.7 267.3C444.8 278.5 423.5 274.6 413.5 258.9L345 151.2z"
                          />
                        </svg>
                      {/if}
                    </div>
                  </button>
                {/each}
                {#each { length: GameTypes[bracket.gameType].maxPlayerSets - gameSet.playerSets.length }}
                  {#if gameSet.previousGameSets.length === GameTypes[bracket.gameType].maxPlayerSets}
                    <button disabled> -------- </button>
                  {:else}
                    <button
                      aria-label="Revive player."
                      class="revive"
                      disabled={isDisabled(gameSet)}
                      onclick={revivePlayer}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 640 640"
                      >
                        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                        <path
                          d="M311.6 95C297.5 75.5 274.9 64 250.9 64C209.5 64 176 97.5 176 138.9L176 141.3C176 205.7 258 274.7 298.2 304.6C311.2 314.3 328.7 314.3 341.7 304.6C381.9 274.6 463.9 205.7 463.9 141.3L463.9 138.9C463.9 97.5 430.4 64 389 64C365 64 342.4 75.5 328.3 95L320 106.7L311.6 95zM141.3 405.5L98.7 448L64 448C46.3 448 32 462.3 32 480L32 544C32 561.7 46.3 576 64 576L384.5 576C413.5 576 441.8 566.7 465.2 549.5L591.8 456.2C609.6 443.1 613.4 418.1 600.3 400.3C587.2 382.5 562.2 378.7 544.4 391.8L424.6 480L312 480C298.7 480 288 469.3 288 456C288 442.7 298.7 432 312 432L384 432C401.7 432 416 417.7 416 400C416 382.3 401.7 368 384 368L231.8 368C197.9 368 165.3 381.5 141.3 405.5z"
                        />
                      </svg>
                    </button>
                  {/if}
                {/each}
              </div>
              <button
                aria-label="Resolve set."
                onclick={() => {
                  resolveGame(gameSet, roundIndex, gameSetIndex);
                }}
                disabled={isDisabled(gameSet)}
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
                  <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                  <path
                    d="M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z"
                  />
                </svg>
              </button>
              {#if roundIndex !== bracket.gameSets.length - 1}
                <div class="horizontalLine"></div>
              {/if}
            </div>
          {/each}
        </div>
      </div>
    {/each}
  </div>
</div>

<style>
  .bracket {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    background-color: var(--prime);
    color: var(--text-prime);
    overflow: scroll;
  }

  .rounds {
    display: flex;
    flex-wrap: nowrap;
    overflow-x: auto;
    height: 100%;
  }

  .round {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 100%;
  }

  .gameSets {
    position: relative;
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    justify-content: space-around;
    align-items: center;
  }

  .gameSet {
    display: flex;
    width: 20rem;
  }

  .set {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }

  .set button {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .revive {
    margin: 0.15rem;
    padding: 0.75rem 1.5rem;
  }

  .horizontalLine {
    height: 0;
    width: 2rem;
    border: 2px solid white;
    align-self: center;
  }

  .verticalLine {
    position: absolute;
    left: 0;
    width: 0;
    border: 2px solid white;
    align-self: center;
    border-top: none;
    border-bottom: none;
  }
</style>
