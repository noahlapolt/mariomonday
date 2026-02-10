<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";
  import { onMount } from "svelte";
  import Search from "./Search.svelte";
  import { globalStates } from "./Utils.svelte";
  import PlayerRender from "./PlayerRender.svelte";

  let {
    teams,
    playerSetSize,
    onAddPlayerSet,
    onCancel,
  }: {
    teams: PlayerSet[];
    playerSetSize: number;
    onAddPlayerSet: (addPlayerSet: {
      teamName: string;
      playerIds: string[];
    }) => void;
    onCancel: () => void;
  } = $props();

  let teamName = $state("");
  let players: Player[] = $state([]);
  let playerOptions: Player[] = $state([]);
  let searchTerm: string = $state("");
  let selectedTeam: PlayerSet = $state({
    id: "",
    name: "",
    players: [],
  });

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
  });

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
        teams.filter((teams) => {
          return (
            teams.players.filter(({ id }) => {
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
   * Adds a player to the current players on screen.
   * @param player The player being added.
   */
  const addPlayer = (player: Player) => {
    selectedTeam.players.push(player);
  };
</script>

<div class="newSetBackground">
  <div class="newSet">
    {#if selectedTeam.players.length < playerSetSize}
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
    {/if}
    {#if playerSetSize > 1}
      <label>
        Team Name: <input type="text" bind:value={teamName} />
      </label>
    {/if}
    {#each selectedTeam.players as player}
      <div>{player.name}</div>
    {/each}
  </div>
  <div class="buttons">
    <button aria-label="Cancel" onclick={onCancel}>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M183.1 137.4C170.6 124.9 150.3 124.9 137.8 137.4C125.3 149.9 125.3 170.2 137.8 182.7L275.2 320L137.9 457.4C125.4 469.9 125.4 490.2 137.9 502.7C150.4 515.2 170.7 515.2 183.2 502.7L320.5 365.3L457.9 502.6C470.4 515.1 490.7 515.1 503.2 502.6C515.7 490.1 515.7 469.8 503.2 457.3L365.8 320L503.1 182.6C515.6 170.1 515.6 149.8 503.1 137.3C490.6 124.8 470.3 124.8 457.8 137.3L320.5 274.7L183.1 137.4z"
        />
      </svg>
    </button>
    <button
      aria-label="Add"
      disabled={playerSetSize != selectedTeam.players.length}
      onclick={() => {
        onAddPlayerSet({
          teamName: teamName,
          playerIds: selectedTeam.players.map((player) => player.id),
        });
      }}
    >
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
        />
      </svg>
    </button>
  </div>
</div>

<style>
  .newSetBackground {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: #00000055;
    display: grid;
    place-content: center;
    overflow: hidden;
    z-index: 9;
  }

  .newSet {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    background-color: #ffffff;
    padding: 1rem;
    border-radius: 1rem;
    overflow: auto;
  }

  .buttons {
    display: flex;
    justify-content: space-around;
  }

  .buttons button {
    flex-grow: 1;
  }
</style>
