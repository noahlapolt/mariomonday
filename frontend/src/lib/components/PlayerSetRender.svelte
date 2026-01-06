<script lang="ts">
  import type { Snippet } from "svelte";
  import PlayerRender from "./PlayerRender.svelte";
  import { GameTypes } from "./Utils.svelte";
  import EditableText from "./EditableText.svelte";

  let {
    playerSet,
    gameType,
    editable = false,
    add,
    remove,
    removePlayer,
    children,
  }: {
    playerSet: PlayerSet;
    gameType: string;
    editable?: boolean;
    add?: () => void;
    remove?: (team: PlayerSet) => void;
    removePlayer?: (player: Player) => void;
    children?: Snippet;
  } = $props();

  const calcHeight = () => {
    return GameTypes[gameType].playersOnATeam > 1
      ? GameTypes[gameType].playersOnATeam * 40 + 30
      : Math.max(playerSet.players.length * 40, 40);
  };
</script>

<div class="team" style={`height: ${calcHeight()}px`}>
  {#if GameTypes[gameType].playersOnATeam > 1}
    <div class="teamName">
      <EditableText
        label="Team Name: "
        placeholder={playerSet.name}
        {editable}
        onSave={(text) => {
          playerSet.name = text;
        }}
      />
    </div>
  {/if}
  {#each playerSet.players as player, index}
    <PlayerRender
      {player}
      {gameType}
      {editable}
      remove={remove !== undefined
        ? (player) => {
            playerSet.players.splice(index, 1);
            if (removePlayer) removePlayer(player);
            if (playerSet.players.length === 0) remove(playerSet);
          }
        : undefined}
    >
      {@render children?.()}
    </PlayerRender>
  {/each}
  {#each { length: GameTypes[gameType].playersOnATeam - playerSet.players.length }}
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
  {/each}
</div>

<style>
  .team {
    display: flex;
    flex-direction: column;
    text-align: center;
    border-radius: 0.5rem;
  }

  .teamName {
    display: flex;
    align-items: center;
    height: 30px;
  }

  .teamAdd {
    display: flex;
    align-items: center;
    margin: 0;
    flex-grow: 1;
  }
</style>
