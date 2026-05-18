<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";
  import GameSetRender from "./GameSetRender.svelte";
  import NewPlayerSet from "./NewPlayerSet.svelte";
  import RandomSelect from "./RandomSelect.svelte";
  import { GameTypes, globalStates } from "./Utils.svelte";
  import { SvelteMap } from "svelte/reactivity";
  import areYouSure from "$lib/assets/are you sure.gif";

  let {
    bracket,
  }: {
    bracket: Bracket;
  } = $props();

  let reviveSet: GameSet | undefined = $state();
  let addSet: GameSet | undefined = $state();
  let swap: boolean = $state(false);
  let confirmation: (() => void) | undefined = $state();
  let swaping: string[] = $state(["", ""]);

  // Get some important round info.
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
    (playerSetHeight + 12) *
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
        let winCount = totalWinsPerSet.get(playerSet.id);
        if (winCount !== undefined) {
          totalWinsPerSet.set(playerSet.id, {
            wins: winCount.wins + gameInfo.maxPlayerSets - index,
            playerSetId: winCount.playerSetId,
          });
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
    let winnerCount =
      roundIndex === bracket.gameSets.length - 1
        ? 1
        : gameInfo.playerSetsToMoveOn;
    for (let i = 0; i < sortedWins.length && i < winnerCount; i++) {
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

    confirmation = () => {
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
            confirmation = undefined;
          } else {
            fetch(`${PUBLIC_API_URL}/bracket/${bracket.id}/complete`, {
              method: "POST",
            }).then((response) => {
              if (response.status === 403) globalStates.login = true;
              else if (response.status === 200) confirmation = undefined;
              else response.text().then((error) => console.log(error));
            });
          }
        } else response.text().then((error) => console.log(error));
      });
    };
  };

  const getLosersPlayerSets = (): PlayerSet[] => {
    const loserPlayerSets: PlayerSet[] = [];
    const loserIds = new Set<string>();

    bracket.gameSets.forEach((round) => {
      round.forEach((gameSet) => {
        if (gameSet.winners.length > 0) {
          let winnerSet = new Set(gameSet.winners);
          gameSet.playerSets.forEach((playerSetId) => {
            let playerSet = playerSetMap.get(playerSetId);
            if (
              !winnerSet.has(playerSetId) &&
              playerSet !== undefined &&
              !loserIds.has(playerSetId)
            ) {
              loserIds.add(playerSetId);
              loserPlayerSets.push(playerSet);
            }
          });
        }
      });
    });

    return loserPlayerSets;
  };

  const swapPlayers = () => {
    if (swaping[0] !== "" && swaping[1] !== "" && swaping[0] !== swaping[1]) {
      // Let the server know about the swap.
      const SWAP_INIT: RequestInit = {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          bracketId: bracket.id,
          firstTeamId: swaping[0],
          secondTeamId: swaping[1],
        }),
      };

      confirmation = () => {
        fetch(`${PUBLIC_API_URL}/swapPlayers`, SWAP_INIT).then((response) => {
          if (response.status === 403) globalStates.login = true;
          else if (response.status === 200) {
            // Get all of the games that need to be swapped.
            let gameOne: GameSet | undefined;
            let gameTwo: GameSet | undefined;
            bracket.gameSets[0].forEach((gameSet) => {
              gameSet.playerSets.forEach((playerSetId) => {
                if (playerSetId === swaping[0]) gameOne = gameSet;
                if (playerSetId === swaping[1]) gameTwo = gameSet;
              });
            });

            if (gameOne !== undefined && gameTwo !== undefined) {
              const temp = Array.from(gameOne.playerSets);
              gameOne.playerSets = Array.from(gameTwo.playerSets);
              gameTwo.playerSets = temp;
            }

            confirmation = undefined;
            swaping = ["", ""];
            swap = false;
          } else response.text().then((error) => console.log(error));
        });
      };
    }
  };
</script>

{#if reviveSet !== undefined}
  <RandomSelect
    gameType={bracket.gameType}
    pool={getLosersPlayerSets()}
    onRevived={(revived) => {
      const playerSetRevived = playerSetMap.get(revived);
      if (playerSetRevived !== undefined && reviveSet !== undefined) {
        const bracket_INIT: RequestInit = {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            gameSetId: reviveSet.id,
            playerIds: playerSetRevived.players.map((player) => player.id),
            teamName: playerSetRevived.name,
          }),
        };
        fetch(
          `${PUBLIC_API_URL}/bracket/${bracket.id}/addPlayer`,
          bracket_INIT,
        ).then((response) => {
          if (response.status === 403) globalStates.login = true;
          if (response.status === 200) {
            response.json().then((data) => {
              window.location.href = `/tournament.html?id=${data.id}`;
            });
          }
        });
      }
    }}
    onCancel={() => {
      reviveSet = undefined;
    }}
  />
{/if}

{#if addSet !== undefined}
  <NewPlayerSet
    teams={bracket.teams}
    playerSetSize={gameInfo.playersOnATeam}
    onAddPlayerSet={({ teamName, playerIds }) => {
      if (addSet !== undefined) {
        const bracket_INIT: RequestInit = {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            gameSetId: addSet.id,
            playerIds: playerIds,
            teamName: teamName,
          }),
        };
        fetch(
          `${PUBLIC_API_URL}/bracket/${bracket.id}/addPlayer`,
          bracket_INIT,
        ).then((response) => {
          if (response.status === 403) globalStates.login = true;
          if (response.status === 200) {
            response.json().then((data) => {
              window.location.href = `/tournament.html?id=${data.id}`;
            });
          }
        });
      }
    }}
    onCancel={() => {
      addSet = undefined;
    }}
  />
{/if}

{#if swap}
  <div class="pop" style="z-index: 8;">
    <div class="popScreen">
      {#each { length: 2 }, index}
        <select bind:value={swaping[index]}>
          {#each bracket.teams as team}
            <option value={team.id}>
              {#if team.players.length > 1}
                {team.name}
              {:else}
                {team.players[0].name}
              {/if}
            </option>
          {/each}
        </select>
      {/each}
      <div class="popScreenButtons">
        <button
          onclick={() => {
            swap = false;
          }}
          aria-label="Cancel"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
            <path
              d="M183.1 137.4C170.6 124.9 150.3 124.9 137.8 137.4C125.3 149.9 125.3 170.2 137.8 182.7L275.2 320L137.9 457.4C125.4 469.9 125.4 490.2 137.9 502.7C150.4 515.2 170.7 515.2 183.2 502.7L320.5 365.3L457.9 502.6C470.4 515.1 490.7 515.1 503.2 502.6C515.7 490.1 515.7 469.8 503.2 457.3L365.8 320L503.1 182.6C515.6 170.1 515.6 149.8 503.1 137.3C490.6 124.8 470.3 124.8 457.8 137.3L320.5 274.7L183.1 137.4z"
            />
          </svg>
        </button>
        <button onclick={swapPlayers} aria-label="Confirm">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
            <path
              d="M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
{/if}

{#if confirmation !== undefined}
  <div class="pop">
    <div class="popScreen">
      <img src={areYouSure} alt="Are you sure?" />
      <div class="popScreenButtons">
        <button
          onclick={() => {
            confirmation = undefined;
          }}
          aria-label="Cancel"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
            <path
              d="M183.1 137.4C170.6 124.9 150.3 124.9 137.8 137.4C125.3 149.9 125.3 170.2 137.8 182.7L275.2 320L137.9 457.4C125.4 469.9 125.4 490.2 137.9 502.7C150.4 515.2 170.7 515.2 183.2 502.7L320.5 365.3L457.9 502.6C470.4 515.1 490.7 515.1 503.2 502.6C515.7 490.1 515.7 469.8 503.2 457.3L365.8 320L503.1 182.6C515.6 170.1 515.6 149.8 503.1 137.3C490.6 124.8 470.3 124.8 457.8 137.3L320.5 274.7L183.1 137.4z"
            />
          </svg>
        </button>
        <button onclick={confirmation} aria-label="Confirm">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
            <path
              d="M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
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
  <div class="adminOptions">
    <button
      aria-label="Swap players"
      onclick={() => {
        swap = true;
      }}
    >
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
        <path
          d="M566.6 214.6L470.6 310.6C458.1 323.1 437.8 323.1 425.3 310.6C412.8 298.1 412.8 277.8 425.3 265.3L466.7 224L96 224C78.3 224 64 209.7 64 192C64 174.3 78.3 160 96 160L466.7 160L425.3 118.6C412.8 106.1 412.8 85.8 425.3 73.3C437.8 60.8 458.1 60.8 470.6 73.3L566.6 169.3C579.1 181.8 579.1 202.1 566.6 214.6zM169.3 566.6L73.3 470.6C60.8 458.1 60.8 437.8 73.3 425.3L169.3 329.3C181.8 316.8 202.1 316.8 214.6 329.3C227.1 341.8 227.1 362.1 214.6 374.6L173.3 416L544 416C561.7 416 576 430.3 576 448C576 465.7 561.7 480 544 480L173.3 480L214.7 521.4C227.2 533.9 227.2 554.2 214.7 566.7C202.2 579.2 181.9 579.2 169.4 566.7z"
        />
      </svg>
    </button>
  </div>
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
                onAddPlayerSet={(gameSet) => {
                  addSet = gameSet;
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
  .pop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: #00000055;
    z-index: 9;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
  }

  .popScreen {
    background-color: #ffffff;
    padding: 1rem;
    border-radius: 1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
  }

  .popScreenButtons {
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-around;
  }

  .popScreen img {
    width: 20rem;
  }

  .adminOptions {
    display: flex;
  }

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
