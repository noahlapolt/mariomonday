<script lang="ts">
  import type { Snippet } from "svelte";
  import EditableText from "./EditableText.svelte";
  import { PUBLIC_API_URL } from "$env/static/public";
  import { globalStates } from "./Utils.svelte";

  let {
    player,
    gameType,
    editable,
    remove,
    children,
  }: {
    player: Player;
    gameType?: string;
    editable?: boolean;
    remove?: (player: Player) => void;
    children?: Snippet;
  } = $props();
</script>

<div class="player">
  <div class="info">
    {#if editable}
      <EditableText
        label="Name"
        placeholder={player.name}
        onSave={(text) => {
          const player_INIT: RequestInit = {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              id: player.id,
              name: text,
              elo: player.eloMap,
            }),
          };
          fetch(`${PUBLIC_API_URL}/player/${player.id}`, player_INIT).then(
            (response) => {
              if (response.status === 403) globalStates.login = true;
            },
          );
        }}
      />
    {:else}
      {player.name}
    {/if}
    {#if gameType !== undefined}
      Elo: {player.eloMap[gameType]}
    {/if}
  </div>

  <div>
    {#if remove !== undefined}
      <button
        aria-label="delete"
        onclick={() => {
          remove(player);
        }}
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
          <path
            d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"
          />
        </svg>
      </button>
    {/if}

    {@render children?.()}
  </div>
</div>

<style>
  .player {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: 1px solid var(--second);
    margin: 0.25rem 0.5rem;
    border-radius: 0.5rem;
    padding: 0 0.5rem;
  }

  .info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
</style>
