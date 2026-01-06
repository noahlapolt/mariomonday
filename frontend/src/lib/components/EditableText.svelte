<script lang="ts">
  let {
    label,
    placeholder,
    editable,
    onSave,
  }: {
    label: string;
    placeholder: string;
    editable: boolean;
    onSave: (text: string) => void;
  } = $props();

  let editing = $state(false);
  let textValue = $state(placeholder);
</script>

<div class="editableText">
  <div>
    {#if !editing}
      {placeholder}
    {:else}
      <label>
        {label}
        <input type="text" {placeholder} bind:value={textValue} />
      </label>
    {/if}
  </div>

  {#if editable}
    <div>
      {#if !editing}
        <button
          aria-label="edit"
          onclick={() => {
            editing = true;
          }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
            <path
              d="M416.9 85.2L372 130.1L509.9 268L554.8 223.1C568.4 209.6 576 191.2 576 172C576 152.8 568.4 134.4 554.8 120.9L519.1 85.2C505.6 71.6 487.2 64 468 64C448.8 64 430.4 71.6 416.9 85.2zM338.1 164L122.9 379.1C112.2 389.8 104.4 403.2 100.3 417.8L64.9 545.6C62.6 553.9 64.9 562.9 71.1 569C77.3 575.1 86.2 577.5 94.5 575.2L222.3 539.7C236.9 535.6 250.2 527.9 261 517.1L476 301.9L338.1 164z"
            />
          </svg>
        </button>
      {:else}
        <button
          aria-label="save"
          onclick={() => {
            onSave(textValue);
            editing = false;
          }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
            <path
              d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"
            />
          </svg>
        </button>
      {/if}
    </div>
  {/if}
</div>

<style>
  .editableText {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 0.5rem;
    margin: 0 0.5rem;
  }
</style>
