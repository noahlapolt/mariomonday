<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";
  import BracketRender from "$lib/components/BracketRender.svelte";
  import { onMount } from "svelte";

  let bracket: Bracket | undefined = $state();

  onMount(() => {
    const urlParams = new URL(window.location.href).searchParams;
    const bracketId = urlParams.get("id") || "";

    // Fetch the bracket.
    if (bracketId !== "") {
      const BRACKET_INIT: RequestInit = {
        method: "GET",
      };
      fetch(`${PUBLIC_API_URL}/bracket/${bracketId}`, BRACKET_INIT).then(
        (response) => {
          if (response.status === 200)
            response.json().then((data) => {
              bracket = data;
            });
        },
      );
    }
  });
</script>

{#if bracket !== undefined}
  <main>
    <BracketRender {bracket} />
  </main>
{:else}
  <main>Bracket is loading...</main>
{/if}

<style>
  main {
    min-height: 100vh;
  }
</style>
