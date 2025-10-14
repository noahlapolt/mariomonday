<script lang="ts">
  import type { Snippet } from "svelte";

  let {
    searchTerm = $bindable(),
    search,
    children,
  }: { searchTerm: string; search: () => void; children?: Snippet } = $props();

  let selected = $state(false);
</script>

<div id="search" style={selected ? "border-color: var(--second)" : ""}>
  <label>
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
      <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
      <path
        d="M480 272C480 317.9 465.1 360.3 440 394.7L566.6 521.4C579.1 533.9 579.1 554.2 566.6 566.7C554.1 579.2 533.8 579.2 521.3 566.7L394.7 440C360.3 465.1 317.9 480 272 480C157.1 480 64 386.9 64 272C64 157.1 157.1 64 272 64C386.9 64 480 157.1 480 272zM272 416C351.5 416 416 351.5 416 272C416 192.5 351.5 128 272 128C192.5 128 128 192.5 128 272C128 351.5 192.5 416 272 416z"
      />
    </svg>
    <input
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
    {@render children?.()}
  </div>
</div>

<style>
  /* Search bar related css */
  #search {
    justify-content: space-between;
    padding: 0.4rem;
    background-color: var(--prime);
    border: 2px solid var(--prime);
    border-radius: 0.5rem;
  }

  div {
    display: flex;
  }

  label {
    display: flex;
  }

  input {
    background-color: transparent;
    border: none;
    color: var(--text-prime);
  }

  input:focus {
    outline: none;
  }
</style>
