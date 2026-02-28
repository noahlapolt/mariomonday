<script lang="ts">
  import Search from "$lib/components/Search.svelte";
  import PlayerSetRender from "$lib/components/PlayerSetRender.svelte";
  import { PUBLIC_API_URL } from "$env/static/public";
  import { onMount } from "svelte";
  import { GameTypes, globalStates } from "$lib/components/Utils.svelte";

  /* Manage player information. */
  /* I was going to treat the data as sets, but sets are not in JSON really. */
  let players: Player[] = $state([]);
  let playingTeams: PlayerSet[] = $state([]);
  let playerOptions: Player[] = $state([]);
  let selectedTeam: PlayerSet | undefined = $state();
  let playerCount: number = $state(0);

  /* Manage game type and search. */
  let searchTerm: string = $state("");
  let gameType: string = $state("SMASH_ULTIMATE_SINGLES");

  onMount(() => {
    /* Fetch players */
    const Players_INIT: RequestInit = {
      method: "GET",
    };
    fetch(`${PUBLIC_API_URL}/player`, Players_INIT)
      .then((response) => response.json())
      .then((data: Player[]) => {
        players = data;
        return true;
      })
      .catch((e) => {
        console.log("There was an error getting the players.", e);
        return false;
      });

    /* Get game type */
    const urlParams = new URL(window.location.href).searchParams;
    gameType = urlParams.get("mode") || "SMASH_ULTIMATE_SINGLES";

    /* Check for cookies */
    const data = document.cookie.split("; ");
    const info = data.findIndex((text) => {
      return text.substring(0, 4) === "info";
    });
    if (info !== -1) {
      let cookieInfo: {
        playingTeams: PlayerSet[];
        playerCount: number;
        mode: string;
      } = JSON.parse(data[info].split("=")[1]);

      if (cookieInfo.mode === gameType) {
        playingTeams = cookieInfo.playingTeams;
        playerCount = cookieInfo.playerCount;
      }
    }
  });

  /**
   * This function takes the current playingTeams set and turns it into
   * a string. Then saves it as a cookie.
   */
  const saveTeams = () => {
    // This cookie only lasts for a day
    const expires = new Date(Date.now() + 86400000).toUTCString();
    document.cookie = `info={"playingTeams": ${JSON.stringify(playingTeams)}, "playerCount": ${playerCount}, "mode": "${gameType}"}; expires=${expires}; path=/`;
  };

  /**
   * Searches through all of the players. Picks the first four players
   * that match the search term.
   *
   * Time complexity O(n), where n is the length of players.
   */
  const searchPlayers = () => {
    let found = 0;
    let index = 0;
    playerOptions = [];
    while (searchTerm !== "" && index < players.length && found < 4) {
      // Adds the player to the list if they are not already playing, exist, and there are
      // less than 4 results
      if (
        players[index].name.toLowerCase().includes(searchTerm.toLowerCase()) &&
        playingTeams.filter((playingTeam) => {
          return (
            playingTeam.players.filter(({ id }) => {
              return id === players[index].id;
            }).length !== 0
          );
        }).length === 0
      ) {
        playerOptions.push(players[index]);
        found++;
      }
      index++;
    }
  };

  /**
   * Adds a player to the current players on screen.
   * @param player The player being added.
   */
  const addPlayer = (player: Player) => {
    if (
      selectedTeam === undefined ||
      GameTypes[gameType].playersOnATeam === 1
    ) {
      selectedTeam = {
        id: player.id,
        name: `Team ${player.id}`,
        players: [player],
      };

      playingTeams.push(selectedTeam);
    } else {
      selectedTeam.players.push(player);
      selectedTeam = undefined;
    }
    playerCount++;
    saveTeams();
  };

  /**
   * Creates a player from a name.
   * @param name The name of the player.
   */
  const createPlayer = () => {
    const newPlayer = {
      name: searchTerm,
      eloMap: {},
    };
    const player_INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newPlayer),
    };
    fetch(`${PUBLIC_API_URL}/player`, player_INIT).then((response) => {
      if (response.status === 200) {
        searchTerm = "";
        response.json().then((data) => {
          addPlayer(data);
        });
      } else if (response.status === 403) {
        globalStates.login = true;
      }
    });
  };

  /**
   * Creates the bracket and sends the user to the current bracket.
   */
  const createBracket = () => {
    // Builds the teams as expected by the server.
    const teams: Record<string, string[]> = {};
    playingTeams.forEach((playingTeam) => {
      teams[playingTeam.name] = playingTeam.players.map(({ id }) => id);
    });

    // Builds the HTTP and creates the bracket.
    const bracket_INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        gameType: gameType,
        teams: teams,
      }),
    };
    fetch(`${PUBLIC_API_URL}/bracket`, bracket_INIT).then((response) => {
      if (response.status === 403) globalStates.login = true;
      if (response.status === 200)
        response.json().then((data) => {
          window.location.href = `/tournament.html?id=${data.id}`;
        });
    });
  };
</script>

<div id="setup">
  <div id="players">
    <Search id="search" bind:searchTerm search={searchPlayers} {createPlayer}>
      {#each playerOptions as option}
        <button
          onclick={() => {
            addPlayer(option);
            playerOptions = [];
            searchTerm = "";
            document.getElementById("search")?.focus();
          }}
        >
          {option.name}
        </button>
      {/each}
    </Search>
    <div id="playing">
      {#each playingTeams as playerSet, index}
        <PlayerSetRender
          {playerSet}
          {gameType}
          editable={true}
          add={() => {
            document.getElementById("search")?.focus();
            selectedTeam = playerSet;
          }}
          remove={() => {
            playingTeams.splice(index, 1);
          }}
          removePlayer={() => {
            playerCount--;
            saveTeams();
          }}
        />
      {/each}
    </div>
    <button onclick={createBracket}>
      Start Tournament: {playerCount} player{playerCount === 1 ? "" : "s"}
    </button>
  </div>
</div>

<style>
  #setup {
    display: flex;
    flex-wrap: wrap;
    flex-direction: row-reverse;
    width: 100vw;
    justify-content: space-around;
  }

  #players {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    justify-content: space-between;
    max-width: 40rem;
    height: calc(100vh - 3rem);
  }

  #playing {
    display: flex;
    flex-direction: column;
    background-color: var(--prime);
    flex-grow: 1;
    border-radius: 0.5rem;
    margin-top: 0.15rem;
    color: var(--text-prime);
    padding: 0.5rem;
    overflow: scroll;
  }
</style>
