<script lang="ts">
	import favicon from "$lib/assets/favicon.svg";
	import Login from "$lib/components/Login.svelte";
	import "../app.css";

	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<Login />
{@render children?.()}
