// HelloJava site interactivity
(function () {
  const root = document.documentElement;

  // ----- Theme toggle (persists to localStorage) -----
  const stored = localStorage.getItem('hj-theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const initialTheme = stored || (prefersDark ? 'dark' : 'light');
  applyTheme(initialTheme);

  document.getElementById('hj-theme-toggle')?.addEventListener('click', function () {
    const next = root.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    localStorage.setItem('hj-theme', next);
    applyTheme(next);
  });

  function applyTheme(t) {
    root.setAttribute('data-bs-theme', t);
    const icon = document.getElementById('hj-theme-icon');
    if (icon) icon.textContent = t === 'dark' ? 'light_mode' : 'dark_mode';
  }

  // ----- Highlight active sidebar link -----
  const here = location.pathname.split('/').pop();
  document.querySelectorAll('.hj-sidebar-link').forEach(function (a) {
    if (a.getAttribute('href').endsWith(here)) {
      a.classList.add('active');
    }
  });

  // ----- Client-side search -----
  const searchInput = document.getElementById('hj-search');
  const resultsBox = document.getElementById('hj-search-results');
  if (searchInput && resultsBox) {
    // figure out path to manifest.json (root is one level up from /phase/ or /code/)
    const depth = (location.pathname.match(/\/(phase|code)\//) ? '../' : '');
    let manifest = null;

    searchInput.addEventListener('focus', loadManifest);
    searchInput.addEventListener('input', runSearch);
    document.addEventListener('click', function (e) {
      if (!searchInput.contains(e.target) && !resultsBox.contains(e.target)) {
        resultsBox.classList.remove('show');
      }
    });

    async function loadManifest() {
      if (manifest) return;
      try {
        const res = await fetch(depth + 'manifest.json');
        manifest = await res.json();
      } catch (e) {
          console.warn('search manifest load failed', e);
      }
    }

    function runSearch() {
      const q = searchInput.value.trim().toLowerCase();
      if (!manifest || q.length < 2) {
        resultsBox.classList.remove('show');
        resultsBox.innerHTML = '';
        return;
      }
      const matches = manifest.filter(function (f) {
        return f.title.toLowerCase().includes(q)
            || f.path.toLowerCase().includes(q)
            || (f.summary || '').toLowerCase().includes(q);
      }).slice(0, 12);
      if (matches.length === 0) {
        resultsBox.innerHTML = '<div class="p-3 text-muted small">No matches</div>';
      } else {
        resultsBox.innerHTML = matches.map(function (m) {
          return '<a href="' + depth + 'code/' + m.slug + '.html">' +
              '<div class="fw-bold">' + escapeHtml(m.title) + '</div>' +
              '<div class="hj-result-phase">' + escapeHtml(m.phaseLabel) + ' · ' + escapeHtml(m.section) + '</div>' +
              '</a>';
        }).join('');
      }
      resultsBox.classList.add('show');
    }

    function escapeHtml(s) {
      return String(s).replace(/[&<>"']/g, function (c) {
          return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
      });
    }
  }
})();
