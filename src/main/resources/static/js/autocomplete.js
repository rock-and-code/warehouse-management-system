/*
 * Lightweight autocomplete. Attach to any <input> with:
 *
 *   data-autocomplete-source="PURCHASE_ORDER|ITEM|CUSTOMER|VENDOR"
 *
 * Optional attributes:
 *   data-autocomplete-mode="PREFIX|CONTAINS"        (default PREFIX)
 *   data-autocomplete-limit="10"                    (default 10, max 50)
 *   data-autocomplete-href-template="/items/{id}"   (each row links to this; {id} substituted)
 *   data-autocomplete-fill-on-select="true"         (populate the input instead of navigating)
 *
 * Calls GET /api/suggestions?q=&type=&mode=&limit=
 * Renders a Bootstrap-styled dropdown beneath the input.
 *
 * No external dependencies. Auto-binds on DOMContentLoaded for every matching input.
 */
(function () {
    'use strict';

    const DEBOUNCE_MS = 180;
    const MIN_CHARS = 1;

    function attach(input) {
        const sourceType = input.dataset.autocompleteSource;
        if (!sourceType) return;

        const mode = (input.dataset.autocompleteMode || 'PREFIX').toUpperCase();
        const limit = parseInt(input.dataset.autocompleteLimit, 10) || 10;
        const hrefTemplate = input.dataset.autocompleteHrefTemplate || null;
        const fillOnSelect = input.dataset.autocompleteFillOnSelect === 'true';

        // Position wrapper so the dropdown can absolute-pos against it.
        const wrapper = document.createElement('div');
        wrapper.style.position = 'relative';
        wrapper.style.flex = '1 1 auto';
        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);

        const menu = document.createElement('div');
        menu.className = 'list-group shadow-sm';
        menu.style.position = 'absolute';
        menu.style.top = '100%';
        menu.style.left = '0';
        menu.style.right = '0';
        menu.style.zIndex = '1080';
        menu.style.maxHeight = '20rem';
        menu.style.overflowY = 'auto';
        menu.style.display = 'none';
        wrapper.appendChild(menu);

        let debounceHandle = null;
        let currentSeq = 0;
        let activeIdx = -1;
        let items = [];

        function close() {
            menu.style.display = 'none';
            activeIdx = -1;
            items = [];
        }

        function highlight() {
            Array.from(menu.children).forEach((el, i) => {
                el.classList.toggle('active', i === activeIdx);
            });
        }

        function pick(item) {
            if (fillOnSelect || !hrefTemplate) {
                input.value = item.label;
                close();
                input.focus();
                return;
            }
            window.location.href = hrefTemplate.replace('{id}', encodeURIComponent(item.id));
        }

        function render(results) {
            menu.innerHTML = '';
            items = results || [];
            if (items.length === 0) {
                menu.style.display = 'none';
                return;
            }
            items.forEach((item, i) => {
                const row = document.createElement('button');
                row.type = 'button';
                row.className = 'list-group-item list-group-item-action py-1 px-3 small';
                row.textContent = item.label;
                row.addEventListener('mousedown', (e) => {
                    e.preventDefault(); // keep input focus
                    pick(item);
                });
                row.addEventListener('mouseenter', () => { activeIdx = i; highlight(); });
                menu.appendChild(row);
            });
            menu.style.display = 'block';
            activeIdx = -1;
        }

        function fetchSuggestions(q) {
            const seq = ++currentSeq;
            const url = '/api/suggestions?'
                + 'q=' + encodeURIComponent(q)
                + '&type=' + encodeURIComponent(sourceType)
                + '&mode=' + encodeURIComponent(mode)
                + '&limit=' + limit;
            fetch(url, { credentials: 'same-origin', headers: { 'Accept': 'application/json' } })
                .then(r => r.ok ? r.json() : [])
                .then(data => {
                    if (seq !== currentSeq) return; // stale response, drop it
                    render(data);
                })
                .catch(() => { /* swallow — empty dropdown is fine */ });
        }

        input.setAttribute('autocomplete', 'off');
        input.addEventListener('input', () => {
            const q = input.value.trim();
            clearTimeout(debounceHandle);
            if (q.length < MIN_CHARS) {
                close();
                return;
            }
            debounceHandle = setTimeout(() => fetchSuggestions(q), DEBOUNCE_MS);
        });

        input.addEventListener('keydown', (e) => {
            if (menu.style.display === 'none') return;
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                activeIdx = Math.min(items.length - 1, activeIdx + 1);
                highlight();
            } else if (e.key === 'ArrowUp') {
                e.preventDefault();
                activeIdx = Math.max(0, activeIdx - 1);
                highlight();
            } else if (e.key === 'Enter') {
                if (activeIdx >= 0 && items[activeIdx]) {
                    e.preventDefault();
                    pick(items[activeIdx]);
                }
            } else if (e.key === 'Escape') {
                close();
            }
        });

        document.addEventListener('click', (e) => {
            if (!wrapper.contains(e.target)) close();
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('input[data-autocomplete-source]').forEach(attach);
    });
})();
