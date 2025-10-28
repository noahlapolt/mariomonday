<script lang="ts">
  import Search from "$lib/components/Search.svelte";
  import TeamInfo from "$lib/components/TeamInfo.svelte";
  import { PUBLIC_API_URL } from "$env/static/public";
  import { onMount } from "svelte";
  import { GameTypes } from "$lib/components/Utils.svelte";

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
      ({ playingTeams, playerCount } = JSON.parse(data[info].split("=")[1]));
    }
  });

  /**
   * This function takes the current playingTeams set and turns it into
   * a string. Then saves it as a cookie.
   */
  const saveTeams = () => {
    // This cookie only lasts for a day
    const expires = new Date(Date.now() + 86400000).toUTCString();
    document.cookie = `info={"playingTeams": ${JSON.stringify(playingTeams)}, "playerCount": ${playerCount}}; expires=${expires}; path=/`;
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
        playingTeams.find(({ players }) =>
          players.find((player) => player === players[index]),
        ) === undefined
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
        id: "",
        name: "New Team",
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
      id: `id-${Math.random() * 1000}`,
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
    fetch(`${PUBLIC_API_URL}/player`, player_INIT)
      .then(() => {
        addPlayer(newPlayer);
        searchTerm = "";
      })
      .catch((err) => {
        console.log("Failed to create the player" + err);
        return false;
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
          }}
        >
          {option.name}
        </button>
      {/each}
    </Search>
    <div id="playing">
      <!--This is hella slow should be optimized.-->
      {#each Array.from(playingTeams).reverse() as team, index}
        <TeamInfo
          {team}
          {gameType}
          editable={true}
          add={() => {
            document.getElementById("search")?.focus();
            selectedTeam = team;
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
    <button>
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
    height: 100vh;
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
