<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";
  import GameSetRender from "./GameSetRender.svelte";
  import NewPlayerSet from "./NewPlayerSet.svelte";
  import RandomSelect from "./RandomSelect.svelte";
  import { GameTypes, globalStates } from "./Utils.svelte";
  import { SvelteMap } from "svelte/reactivity";

  let {
    bracket,
  }: {
    bracket: Bracket;
  } = $props();

  let reviveSet: GameSet | undefined = $state();
  let newPlayerSet: PlayerSet | undefined = $state();

  // Get some important round info.
  let gameSetCount = 0;
  let resolvedCount = 0;
  let playerSetMap: SvelteMap<string, PlayerSet> = $derived.by(() => {
    const map = new SvelteMap<string, PlayerSet>();

    // Makes a map of the teams for quicker refrence.
    // TODO: Ask for the backend to send it like this.
    bracket.teams.forEach((team) => {
      map.set(team.id, team);
    });

    return map;
  });

  const gameInfo = GameTypes[bracket.gameType];
  const maxPrevGames = gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn;
  const playerSetHeight =
    gameInfo.playersOnATeam * 40 + (gameInfo.playersOnATeam > 1 ? 30 : 0);
  const gameSetsHeight =
    (playerSetHeight + 10) *
      bracket.gameSets[0].length *
      gameInfo.maxPlayerSets +
    4;

  const resolveGameSet = (
    roundIndex: number,
    gameSet: GameSet,
    gameSetIndex: number,
  ) => {
    // Get all of the players.
    let totalWinsPerSet = new Map<
      string,
      { wins: number; playerSetId: string }
    >();
    gameSet.playerSets.forEach((playerSetId) => {
      let playerSet = playerSetMap.get(playerSetId);
      if (playerSet !== undefined) {
        totalWinsPerSet.set(playerSet.id, {
          wins: 0,
          playerSetId: playerSetId,
        });
      }
    });

    // Count up the wins.
    const games: string[][] = [];
    gameSet.games.forEach((game) => {
      game.playerSets.forEach((playerSet, index) => {
        if (index < gameInfo.playerSetsToMoveOn) {
          let winCount = totalWinsPerSet.get(playerSet.id);
          if (winCount !== undefined) {
            totalWinsPerSet.set(playerSet.id, {
              wins: winCount.wins + 1,
              playerSetId: winCount.playerSetId,
            });
          } else {
            // This will should never run, but its here just in case.
            totalWinsPerSet.set(playerSet.id, {
              wins: 1,
              playerSetId: playerSet.id,
            });
          }
        }
      });
      games.push(game.playerSets.map((playerSet) => playerSet.id));
    });

    // Sort by most wins.
    const sortedWins = Array.from(totalWinsPerSet).sort(
      (a, b) => b[1].wins - a[1].wins,
    );

    // Adds the winners.
    const winners: string[] = [];
    for (
      let i = 0;
      i < sortedWins.length && i < gameInfo.playerSetsToMoveOn;
      i++
    ) {
      winners.push(sortedWins[i][1].playerSetId);
    }

    // Let the server know about the win.
    const gameSet_INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        forfeit: false,
        games: games,
        winners: winners,
      }),
    };
    console.log({
      forfeit: false,
      games: games,
      winners: winners,
    });
    fetch(
      `${PUBLIC_API_URL}/bracket/${bracket.id}/completeGameSet/${gameSet.id}`,
      gameSet_INIT,
    ).then((response) => {
      if (response.status === 403) globalStates.login = true;
      else if (response.status === 200) {
        // Update current game sets.
        gameSet.winners = winners;

        // Update any future options
        if (roundIndex + 1 < bracket.gameSets.length) {
          const nextRound = bracket.gameSets[roundIndex + 1];
          const nextSetIndex = Math.floor(
            gameSetIndex /
              (gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn),
          );
          if (nextSetIndex < nextRound.length) {
            winners.forEach((winner) => {
              nextRound[nextSetIndex].playerSets.push(winner);
            });
          }
        } else {
          fetch(`${PUBLIC_API_URL}/bracket/${bracket.id}/complete`, {
            method: "POST",
          }).then((response) => {
            console.log(response);
          });
        }
      } else {
        response.text().then((error) => console.log(error));
      }
    });
  };

  const getLosersPlayerSets = (): PlayerSet[] => {
    const losersPlayerSets: PlayerSet[] = [];

    bracket.losers.forEach((loser) => {
      const playerSet = playerSetMap.get(loser);
      if (playerSet !== undefined) losersPlayerSets.push(playerSet);
    });

    return losersPlayerSets;
  };
</script>

{#if reviveSet !== undefined}
  <RandomSelect
    gameType={bracket.gameType}
    pool={getLosersPlayerSets()}
    onRevived={(revived) => {
      if (reviveSet !== undefined) {
        reviveSet.playerSets.push(revived);
        reviveSet = undefined;
      }
    }}
    onCancel={() => {
      reviveSet = undefined;
    }}
  />
{/if}

{#if newPlayerSet !== undefined}
  <NewPlayerSet
    {newPlayerSet}
    onAddPlayerSet={() => {}}
    onCancel={() => {
      newPlayerSet = undefined;
    }}
  />
{/if}

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
  <b>{new Date(bracket.date).toDateString()}</b>
  <div class="rounds">
    {#each bracket.gameSets as gameSets, roundIndex}
      <div class="round">
        <h2>Round {roundIndex + 1}</h2>
        <div class="gameSets" style={`height: ${gameSetsHeight}px`}>
          {#each gameSets as gameSet, gameSetIndex}
            <div class="gameSetWrapper">
              {#if roundIndex !== 0}
                <div class="vertLine"></div>
              {/if}
              <!-- Display a GameSet or a Revive/Add -->
              <GameSetRender
                bracketId={bracket.id}
                gameType={bracket.gameType}
                {gameSet}
                disabled={gameSet.winners.length > 0}
                {playerSetMap}
                onRevive={(gameSet) => {
                  reviveSet = gameSet;
                }}
                onAddPlayerSet={(playerSet) => {
                  newPlayerSet = playerSet;
                }}
              />
              <!-- Display resolve option -->
              <button
                aria-label="Resolve set."
                onclick={() => {
                  resolveGameSet(roundIndex, gameSet, gameSetIndex);
                }}
                disabled={gameSet.winners.length > 0}
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
                  <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                  <path
                    d="M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z"
                  />
                </svg>
              </button>
              <!-- Display the correct line. -->
              {#if roundIndex !== bracket.gameSets.length - 1}
                <div class="vertLine"></div>
                {#if gameSetIndex % maxPrevGames === 0}
                  <div
                    class="horLine"
                    style="height: 50%; transform: translateY(50%)"
                  ></div>
                {:else if gameSetIndex % maxPrevGames === maxPrevGames - 1}
                  <div
                    class="horLine"
                    style="height: 50%; transform: translateY(-50%)"
                  ></div>
                {:else}
                  <div class="horLine" style="height: 100%;"></div>
                {/if}
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
    align-items: center;
    background-color: var(--prime);
    color: var(--text-prime);
  }

  .rounds {
    display: flex;
    flex-wrap: nowrap;
    max-width: 100vw;
    overflow: auto;
  }

  .round {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .gameSets {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
  }

  .gameSetWrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-grow: 1;
  }

  .horLine {
    background-color: aliceblue;
    width: 2px;
  }

  .vertLine {
    background-color: aliceblue;
    height: 2px;
    width: 2rem;
  }
</style>
