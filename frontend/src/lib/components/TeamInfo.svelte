<script lang="ts">
  import type { Snippet } from "svelte";
  import PlayerInfo from "./PlayerInfo.svelte";
  import { GameTypes } from "./Utils.svelte";
  import EditableText from "./EditableText.svelte";

  let {
    team,
    gameType,
    editable,
    add,
    remove,
    removePlayer,
    children,
  }: {
    team: PlayerSet;
    gameType: string;
    editable?: boolean;
    add?: () => void;
    remove?: (team: PlayerSet) => void;
    removePlayer?: (player: Player) => void;
    children?: Snippet;
  } = $props();
</script>

<div class={GameTypes[gameType].playersOnATeam > 1 ? "team" : ""}>
  {#if GameTypes[gameType].playersOnATeam > 1}
    {#if editable}
      <EditableText
        label="Team Name: "
        placeholder={team.name}
        onSave={(text) => {
          team.name = text;
        }}
      />
    {:else}
      <label
        >Team Name: <input
          class="teamName"
          placeholder={team.name}
          type="text"
        /></label
      >
    {/if}
  {/if}
  {#each team.players as player, index}
    <PlayerInfo
      {player}
      {gameType}
      {editable}
      remove={remove !== undefined
        ? (player) => {
            team.players.splice(index, 1);
            if (removePlayer) removePlayer(player);
            if (team.players.length === 0) remove(team);
          }
        : undefined}
    >
      {@render children?.()}</PlayerInfo
    >
  {/each}
  {#if team.players.length < GameTypes[gameType].playersOnATeam}
    <button
      class="teamAdd"
      aria-label="add player to a team"
      onclick={() => {
        if (add !== undefined) {
          add();
        }
      }}
    >
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
        />
      </svg>
    </button>
  {/if}
</div>

<style>
  .team {
    display: flex;
    flex-direction: column;
    text-align: center;
    border: 1px dashed var(--second);
    border-radius: 0.5rem;
    margin: 0.5rem;
    padding: 0.5rem 0;
  }

  .teamName {
    background-color: var(--prime);
    color: var(--text-prime);
    border: none;
  }

  .teamName:focus {
    outline: 1px solid var(--second);
  }

  .teamAdd {
    margin: 0.45rem 0.5rem;
  }
</style>
