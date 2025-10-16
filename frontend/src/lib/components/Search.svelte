<script lang="ts">
  import type { Snippet } from "svelte";

  let {
    id,
    searchTerm = $bindable(),
    search,
    createPlayer,
    children,
  }: {
    id?: string;
    searchTerm: string;
    search: () => void;
    createPlayer?: () => void;
    children?: Snippet;
  } = $props();

  let selected = $state(false);
</script>

<div class="search" style={selected ? "border-color: var(--second)" : ""}>
  <label>
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
      <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
      <path
        d="M480 272C480 317.9 465.1 360.3 440 394.7L566.6 521.4C579.1 533.9 579.1 554.2 566.6 566.7C554.1 579.2 533.8 579.2 521.3 566.7L394.7 440C360.3 465.1 317.9 480 272 480C157.1 480 64 386.9 64 272C64 157.1 157.1 64 272 64C386.9 64 480 157.1 480 272zM272 416C351.5 416 416 351.5 416 272C416 192.5 351.5 128 272 128C192.5 128 128 192.5 128 272C128 351.5 192.5 416 272 416z"
      />
    </svg>
    <input
      {id}
      bind:value={searchTerm}
      onfocus={() => {
        selected = true;
      }}
      onfocusout={() => {
        selected = false;
      }}
      oninput={search}
      type="text"
      placeholder="Search"
    />
  </label>
  <div>
    <button
      aria-label="clear"
      onclick={() => {
        searchTerm = "";
      }}
    >
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"
        />
      </svg>
    </button>
    {#if createPlayer !== undefined}
      <button aria-label="add" onclick={createPlayer}>
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
          <path
            d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
          />
        </svg>
      </button>
    {/if}
    <div class="results">{@render children?.()}</div>
  </div>
</div>

<style>
  /* Search bar related css */
  .search {
    position: relative;
    justify-content: space-between;
    padding: 0.4rem;
    background-color: var(--prime);
    border: 4px solid var(--prime);
    border-radius: 0.5rem;
  }

  .results {
    position: absolute;
    flex-direction: column;
    top: 3rem;
    left: 0;
    background-color: var(--prime);
    width: 100%;
  }

  div {
    display: flex;
  }

  label {
    display: flex;
    flex-grow: 1;
  }

  input {
    background-color: transparent;
    border: none;
    color: var(--text-prime);
    flex-grow: 1;
  }

  input:focus {
    outline: none;
  }
</style>
