"""Generate a static HTML site for the HelloJava learning repo.

Output: ../docs/
  index.html              Phase grid (landing)
  phase/<key>.html        File list per phase, grouped by section
  code/<slug>.html        One page per .java file, syntax highlighted
  assets/site.css         Custom styles on top of MDB + Prism
  assets/site.js          Sidebar toggle, theme toggle, search
  manifest.json           Flat file index for client-side search

Run from the repo root:
  python scripts/generate_site.py
"""

from __future__ import annotations

import html
import json
import re
import shutil
from dataclasses import dataclass, field
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "src"
OUT = ROOT / "docs"


# --------------------------------------------------------------------------- #
# Data model                                                                  #
# --------------------------------------------------------------------------- #

@dataclass
class JavaFile:
    abs_path: Path
    rel_path: Path           # relative to SRC, posix style after `.as_posix()`
    phase_key: str           # e.g. "Phase0_SetupAndFirstPrograms"
    section: str             # first dir under phase, or "_root" for files directly in phase
    name: str                # file stem, e.g. "HelloWorld"
    code: str
    title: str               # human-readable, e.g. "Hello World"
    summary: str             # extracted from top Javadoc, may be empty
    slug: str                # URL-safe path key
    line_count: int


@dataclass
class Phase:
    key: str                          # e.g. "Phase0_SetupAndFirstPrograms"
    number: int                       # 0..9
    title: str                        # "Setup & First Programs"
    sections: dict[str, list[JavaFile]] = field(default_factory=dict)

    @property
    def file_count(self) -> int:
        return sum(len(v) for v in self.sections.values())

    @property
    def label(self) -> str:
        return f"Phase {self.number} — {self.title}"


# --------------------------------------------------------------------------- #
# Hard-coded pretty names (the source dirs are CamelCase; this maps them)     #
# --------------------------------------------------------------------------- #

PHASE_TITLES = {
    "Phase0_SetupAndFirstPrograms": "Setup & First Programs",
    "Phase1_CoreLanguage": "Core Language",
    "Phase2_MethodsArraysStrings": "Methods, Arrays, Strings",
    "Phase3_ObjectOrientation": "Object Orientation",
    "Phase4_ErrorsAndTypeSafety": "Errors & Type Safety",
    "Phase5_CollectionsLambdasStreams": "Collections, Lambdas & Streams",
    "Phase6_RuntimeMemoryRegexReflection": "Runtime, Memory, Regex, Reflection",
    "Phase7_Concurrency": "Concurrency",
    "Phase8_PracticalAPIs": "Practical APIs",
    "Phase9_ModernJavaAndModules": "Modern Java & Modules",
}

PHASE_ICONS = {
    "Phase0_SetupAndFirstPrograms": "play_circle",
    "Phase1_CoreLanguage": "code",
    "Phase2_MethodsArraysStrings": "data_array",
    "Phase3_ObjectOrientation": "category",
    "Phase4_ErrorsAndTypeSafety": "shield",
    "Phase5_CollectionsLambdasStreams": "stream",
    "Phase6_RuntimeMemoryRegexReflection": "memory",
    "Phase7_Concurrency": "settings_input_component",
    "Phase8_PracticalAPIs": "api",
    "Phase9_ModernJavaAndModules": "auto_awesome",
}


def split_camel(name: str) -> str:
    """`HelloWorld` -> `Hello World`, `JDKvsJREvsJVM` keeps acronyms readable."""
    # insert spaces between lower→upper, and between upper→Upper+lower
    s = re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", name)
    s = re.sub(r"(?<=[A-Z])(?=[A-Z][a-z])", " ", s)
    return s


def extract_summary(code: str) -> str:
    """Pull a short prose summary from the first top-of-file Javadoc block.

    Files in this repo follow a pattern of `Heading / ----- / prose ...`. We
    split the Javadoc into paragraphs (separated by blank or `----` lines)
    and return the first paragraph that looks like real prose (i.e. has a
    sentence — contains a period or is reasonably long).
    """
    m = re.search(r"/\*\*(.*?)\*/", code, re.DOTALL)
    if not m:
        return ""
    body = m.group(1)

    # Clean each line: strip leading ' * ', drop HTML/javadoc artefacts.
    cleaned: list[str] = []
    for raw in body.splitlines():
        line = re.sub(r"^\s*\*\s?", "", raw).rstrip()
        if line.startswith("@"):
            break
        if line.strip().lower() in ("<p>", "</p>", "<pre>", "</pre>"):
            cleaned.append("")
            continue
        cleaned.append(line)

    # Split into paragraphs at blank lines OR separator lines (---, ===, ~~~)
    paragraphs: list[str] = []
    current: list[str] = []
    for ln in cleaned:
        s = ln.strip()
        is_sep = bool(re.match(r"^[-=_~]{3,}$", s))
        if not s or is_sep:
            if current:
                paragraphs.append(" ".join(current).strip())
                current = []
            continue
        current.append(s)
    if current:
        paragraphs.append(" ".join(current).strip())

    def looks_like_prose(p: str) -> bool:
        if len(p.split()) < 4:
            return False
        if not p[:1].isalpha() and p[:1] not in ('"', "'", '`'):
            return False
        # ASCII-art / operator listings tend to have very wide gaps
        if re.search(r" {4,}", p):
            return False
        # require some sentence-ending punctuation OR be reasonably long prose
        return any(c in p for c in ".:?!") or len(p) > 80

    summary = next((p for p in paragraphs if looks_like_prose(p)), "")
    if not summary and paragraphs:
        # fall back: longest paragraph that at least starts with a letter
        alpha = [p for p in paragraphs if p[:1].isalpha()]
        if alpha:
            summary = max(alpha, key=len)
        else:
            summary = paragraphs[0]

    if len(summary) > 220:
        summary = summary[:217].rstrip() + "..."
    return summary


def slugify(rel_path: Path) -> str:
    return rel_path.as_posix().replace("/", "__").removesuffix(".java")


# --------------------------------------------------------------------------- #
# Discovery                                                                   #
# --------------------------------------------------------------------------- #

def discover() -> list[Phase]:
    files_by_phase: dict[str, list[JavaFile]] = {}
    for java in sorted(SRC.rglob("*.java")):
        rel = java.relative_to(SRC)
        parts = rel.parts
        phase_key = parts[0]
        if phase_key not in PHASE_TITLES:
            continue
        section = parts[1] if len(parts) > 2 else "_root"
        # for files several levels deep (e.g. Phase1/DecisionMaking/JumpStatement/Foo.java)
        # treat the first dir under the phase as the section.
        code = java.read_text(encoding="utf-8", errors="replace")
        jf = JavaFile(
            abs_path=java,
            rel_path=rel,
            phase_key=phase_key,
            section=section,
            name=java.stem,
            code=code,
            title=split_camel(java.stem),
            summary=extract_summary(code),
            slug=slugify(rel),
            line_count=code.count("\n") + 1,
        )
        files_by_phase.setdefault(phase_key, []).append(jf)

    phases: list[Phase] = []
    for key, title in PHASE_TITLES.items():
        files = files_by_phase.get(key, [])
        sections: dict[str, list[JavaFile]] = {}
        for f in files:
            sections.setdefault(f.section, []).append(f)
        # stable section ordering: _root first, then alphabetical
        ordered = {}
        if "_root" in sections:
            ordered["_root"] = sections.pop("_root")
        for sec in sorted(sections.keys()):
            ordered[sec] = sections[sec]
        phases.append(Phase(
            key=key,
            number=int(key[5]),
            title=title,
            sections=ordered,
        ))
    return phases


# --------------------------------------------------------------------------- #
# HTML templates                                                              #
# --------------------------------------------------------------------------- #

HEAD = """<!doctype html>
<html lang="en" data-bs-theme="light">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>{title} · HelloJava</title>
  <meta name="description" content="A hands-on, topic-by-topic walkthrough of Java 21 LTS.">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=JetBrains+Mono:wght@400;500;700&family=Material+Icons&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/mdb-ui-kit@7.3.2/css/mdb.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/toolbar/prism-toolbar.min.css" rel="stylesheet">
  <link href="{root}assets/site.css" rel="stylesheet">
</head>
<body>
"""

NAVBAR = """
<nav class="navbar navbar-expand-lg fixed-top shadow-0 hj-navbar">
  <div class="container-fluid">
    <button class="btn btn-link text-white d-lg-none me-2 p-2" type="button" data-mdb-toggle="offcanvas" data-mdb-target="#sidebar" aria-controls="sidebar">
      <span class="material-icons">menu</span>
    </button>
    <a class="navbar-brand fw-bold d-flex align-items-center" href="{root}index.html">
      <span class="material-icons me-2">coffee</span>
      <span>HelloJava</span>
      <span class="badge bg-light text-dark ms-2 fw-normal">Java 21 LTS</span>
    </a>
    <div class="d-flex align-items-center ms-auto gap-2">
      <div class="hj-search d-none d-md-flex">
        <span class="material-icons">search</span>
        <input type="search" id="hj-search" placeholder="Search topics..." autocomplete="off">
        <div id="hj-search-results"></div>
      </div>
      <a class="btn btn-link text-white p-2" href="https://github.com" target="_blank" rel="noopener" title="View on GitHub" id="hj-github-link">
        <span class="material-icons">open_in_new</span>
      </a>
      <button class="btn btn-link text-white p-2" id="hj-theme-toggle" title="Toggle theme">
        <span class="material-icons" id="hj-theme-icon">dark_mode</span>
      </button>
    </div>
  </div>
</nav>
"""

SIDEBAR_OPEN = """
<div class="offcanvas-lg offcanvas-start hj-sidebar" tabindex="-1" id="sidebar">
  <div class="offcanvas-header">
    <h6 class="offcanvas-title fw-bold">All Phases</h6>
    <button type="button" class="btn-close" data-mdb-dismiss="offcanvas" data-mdb-target="#sidebar" aria-label="Close"></button>
  </div>
  <div class="offcanvas-body p-0">
    <div class="accordion accordion-flush" id="hj-sidebar-acc">
"""

SIDEBAR_CLOSE = """
    </div>
  </div>
</div>
"""

FOOTER = """
<footer class="hj-footer text-center py-4 mt-5">
  <small class="text-muted">
    HelloJava · A topic-by-topic walkthrough of Java 21 ·
    Built with <a href="https://mdbootstrap.com/" target="_blank" rel="noopener">MDB</a>
    and <a href="https://prismjs.com/" target="_blank" rel="noopener">Prism.js</a>
  </small>
</footer>

<script src="https://cdn.jsdelivr.net/npm/mdb-ui-kit@7.3.2/js/mdb.umd.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/prism-core.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/prism-java.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/toolbar/prism-toolbar.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js"></script>
<script src="{root}assets/site.js"></script>
</body>
</html>
"""


def render_sidebar(phases: list[Phase], current_phase: str | None, root: str) -> str:
    parts = [SIDEBAR_OPEN]
    for p in phases:
        is_open = p.key == current_phase
        parts.append(f"""
      <div class="accordion-item bg-transparent border-0">
        <h2 class="accordion-header">
          <button class="accordion-button {'' if is_open else 'collapsed'} bg-transparent py-2 px-3"
                  type="button" data-mdb-collapse-init data-mdb-toggle="collapse"
                  data-mdb-target="#sb-{p.key}" aria-expanded="{'true' if is_open else 'false'}">
            <span class="material-icons me-2 text-primary">{PHASE_ICONS[p.key]}</span>
            <span class="flex-grow-1">
              <small class="text-muted d-block">Phase {p.number}</small>
              <span class="fw-bold">{html.escape(p.title)}</span>
            </span>
            <span class="badge bg-light text-dark ms-2">{p.file_count}</span>
          </button>
        </h2>
        <div id="sb-{p.key}" class="accordion-collapse collapse {'show' if is_open else ''}">
          <div class="accordion-body p-0">
            <a class="hj-sidebar-link d-block px-3 py-2 fw-bold" href="{root}phase/{p.key}.html">
              <span class="material-icons-outlined small me-1">summarize</span>Phase overview
            </a>
""")
        for sec_name, files in p.sections.items():
            if sec_name != "_root":
                parts.append(f'<div class="hj-sidebar-section px-3 pt-2 pb-1 text-muted small text-uppercase">{html.escape(split_camel(sec_name))}</div>')
            for f in files:
                parts.append(
                    f'<a class="hj-sidebar-link d-block px-3 py-1" '
                    f'href="{root}code/{f.slug}.html">'
                    f'<span class="material-icons-outlined small me-1">description</span>'
                    f'{html.escape(f.title)}</a>'
                )
        parts.append("""
          </div>
        </div>
      </div>
""")
    parts.append(SIDEBAR_CLOSE)
    return "".join(parts)


# --------------------------------------------------------------------------- #
# Page renderers                                                              #
# --------------------------------------------------------------------------- #

def render_index(phases: list[Phase]) -> str:
    cards = []
    for p in phases:
        cards.append(f"""
      <div class="col">
        <a href="phase/{p.key}.html" class="text-decoration-none">
          <div class="card hj-phase-card h-100 shadow-2 hover-shadow-3">
            <div class="card-body">
              <div class="d-flex align-items-center mb-3">
                <div class="hj-phase-icon">
                  <span class="material-icons">{PHASE_ICONS[p.key]}</span>
                </div>
                <div class="ms-3">
                  <small class="text-muted text-uppercase fw-bold">Phase {p.number}</small>
                  <h5 class="mb-0 fw-bold">{html.escape(p.title)}</h5>
                </div>
              </div>
              <div class="d-flex justify-content-between text-muted small">
                <span><span class="material-icons-outlined small">description</span> {p.file_count} files</span>
                <span><span class="material-icons-outlined small">folder</span> {len(p.sections)} section{'s' if len(p.sections) != 1 else ''}</span>
              </div>
            </div>
          </div>
        </a>
      </div>
""")

    total_files = sum(p.file_count for p in phases)
    body = f"""
{NAVBAR.format(root="")}
{render_sidebar(phases, None, "")}

<main class="hj-main">
  <section class="hj-hero py-5">
    <div class="container-fluid px-4">
      <div class="row align-items-center">
        <div class="col-lg-7">
          <h1 class="display-4 fw-bold mb-3 text-white">Learn Java 21,<br>topic by topic.</h1>
          <p class="lead text-white-50 mb-4">
            {total_files} runnable, self-contained Java files across {len(phases)} phases — every topic
            has theory in the Javadoc and worked examples in <code class="text-warning">main()</code>.
          </p>
          <div class="d-flex flex-wrap gap-3">
            <a href="phase/{phases[0].key}.html" class="btn btn-light btn-lg">
              <span class="material-icons me-1">play_arrow</span>Start with Phase 0
            </a>
            <a href="#phases" class="btn btn-outline-light btn-lg">
              <span class="material-icons me-1">grid_view</span>Browse phases
            </a>
          </div>
        </div>
        <div class="col-lg-5 d-none d-lg-block">
          <pre class="hj-hero-code shadow-3 m-0"><code class="language-java">public class HelloWorld {{

    public static void main(String[] args) {{
        System.out.println("Hello World");
    }}
}}</code></pre>
        </div>
      </div>
    </div>
  </section>

  <section id="phases" class="py-5">
    <div class="container-fluid px-4">
      <div class="d-flex justify-content-between align-items-end mb-4">
        <div>
          <h2 class="fw-bold mb-1">Learning phases</h2>
          <p class="text-muted mb-0">Work through them in order — within a phase, files are self-contained.</p>
        </div>
      </div>
      <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
        {"".join(cards)}
      </div>
    </div>
  </section>
</main>
{FOOTER.format(root="")}
"""
    return HEAD.format(title="Home", root="") + body


def render_phase(phase: Phase, phases: list[Phase]) -> str:
    section_blocks = []
    for sec_name, files in phase.sections.items():
        sec_title = "Overview" if sec_name == "_root" else split_camel(sec_name)
        rows = []
        for f in files:
            summary = f.summary or "Worked example with theory in the file's Javadoc."
            rows.append(f"""
        <div class="col">
          <a href="../code/{f.slug}.html" class="text-decoration-none">
            <div class="card hj-file-card h-100 shadow-1">
              <div class="card-body">
                <div class="d-flex align-items-start">
                  <span class="material-icons-outlined hj-file-icon me-2">description</span>
                  <div class="flex-grow-1 min-w-0">
                    <h6 class="card-title mb-1 fw-bold text-truncate" title="{html.escape(f.title)}">{html.escape(f.title)}</h6>
                    <p class="card-text small text-muted hj-file-summary mb-2">{html.escape(summary)}</p>
                    <div class="d-flex gap-3 small text-muted">
                      <span><span class="material-icons-outlined small">code</span> {f.line_count} lines</span>
                      <span class="text-truncate"><span class="material-icons-outlined small">folder_open</span> {html.escape(f.rel_path.as_posix())}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a>
        </div>
""")
        section_blocks.append(f"""
    <section class="mb-5">
      <div class="d-flex align-items-center mb-3">
        <div class="hj-section-marker"></div>
        <h3 class="fw-bold mb-0 ms-3">{html.escape(sec_title)}</h3>
        <span class="badge bg-light text-dark ms-3">{len(files)} file{'s' if len(files) != 1 else ''}</span>
      </div>
      <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-3">
        {"".join(rows)}
      </div>
    </section>
""")

    body = f"""
{NAVBAR.format(root="../")}
{render_sidebar(phases, phase.key, "../")}

<main class="hj-main">
  <section class="hj-page-hero py-4">
    <div class="container-fluid px-4">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-2">
          <li class="breadcrumb-item"><a href="../index.html" class="text-white-50">Home</a></li>
          <li class="breadcrumb-item active text-white" aria-current="page">Phase {phase.number}</li>
        </ol>
      </nav>
      <div class="d-flex align-items-center">
        <div class="hj-page-icon">
          <span class="material-icons">{PHASE_ICONS[phase.key]}</span>
        </div>
        <div class="ms-3">
          <small class="text-white-50 text-uppercase fw-bold">Phase {phase.number}</small>
          <h1 class="text-white fw-bold mb-0">{html.escape(phase.title)}</h1>
        </div>
      </div>
    </div>
  </section>

  <section class="py-4">
    <div class="container-fluid px-4">
      {"".join(section_blocks)}
    </div>
  </section>
</main>
{FOOTER.format(root="../")}
"""
    return HEAD.format(title=phase.label, root="../") + body


def render_code(jf: JavaFile, phases: list[Phase]) -> str:
    # Build prev/next within the same phase + section
    phase = next(p for p in phases if p.key == jf.phase_key)
    flat: list[JavaFile] = []
    for files in phase.sections.values():
        flat.extend(files)
    idx = next(i for i, f in enumerate(flat) if f.slug == jf.slug)
    prev_f = flat[idx - 1] if idx > 0 else None
    next_f = flat[idx + 1] if idx + 1 < len(flat) else None

    summary_html = ""
    if jf.summary:
        summary_html = f"""
      <div class="alert hj-summary mb-4" role="note">
        <div class="d-flex">
          <span class="material-icons me-2 text-primary">info</span>
          <div>{html.escape(jf.summary)}</div>
        </div>
      </div>
"""

    nav_html = ""
    if prev_f or next_f:
        prev_html = (
            f'<a class="btn btn-outline-primary" href="{prev_f.slug}.html">'
            f'<span class="material-icons me-1">arrow_back</span>{html.escape(prev_f.title)}</a>'
            if prev_f else '<span></span>'
        )
        next_html = (
            f'<a class="btn btn-primary" href="{next_f.slug}.html">'
            f'{html.escape(next_f.title)}<span class="material-icons ms-1">arrow_forward</span></a>'
            if next_f else '<span></span>'
        )
        nav_html = f'''
      <div class="d-flex justify-content-between align-items-center mt-4 pt-4 border-top">
        {prev_html}
        {next_html}
      </div>
'''

    section_pretty = "" if jf.section == "_root" else split_camel(jf.section)
    breadcrumb_section = (
        f'<li class="breadcrumb-item active text-white" aria-current="page">{html.escape(section_pretty)}</li>'
        if section_pretty else ''
    )

    body = f"""
{NAVBAR.format(root="../")}
{render_sidebar(phases, jf.phase_key, "../")}

<main class="hj-main">
  <section class="hj-page-hero hj-code-hero py-4">
    <div class="container-fluid px-4">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-2 small">
          <li class="breadcrumb-item"><a href="../index.html" class="text-white-50">Home</a></li>
          <li class="breadcrumb-item"><a href="../phase/{phase.key}.html" class="text-white-50">Phase {phase.number}</a></li>
          {breadcrumb_section}
        </ol>
      </nav>
      <div class="d-flex align-items-center flex-wrap gap-3">
        <div class="hj-page-icon hj-page-icon-sm">
          <span class="material-icons">description</span>
        </div>
        <div class="flex-grow-1">
          <h1 class="text-white fw-bold mb-1">{html.escape(jf.title)}</h1>
          <code class="text-white-50 small">src/{html.escape(jf.rel_path.as_posix())}</code>
        </div>
        <div class="d-flex gap-2">
          <span class="badge bg-light text-dark"><span class="material-icons-outlined small">code</span> {jf.line_count} lines</span>
        </div>
      </div>
    </div>
  </section>

  <section class="py-4">
    <div class="container-fluid px-4">
      {summary_html}
      <div class="hj-code-card card shadow-2">
        <div class="card-header d-flex justify-content-between align-items-center bg-dark text-white">
          <div class="d-flex align-items-center gap-2">
            <span class="hj-traffic-light bg-danger"></span>
            <span class="hj-traffic-light bg-warning"></span>
            <span class="hj-traffic-light bg-success"></span>
            <span class="ms-2 font-monospace small">{html.escape(jf.name)}.java</span>
          </div>
          <span class="badge bg-warning text-dark">Java 21</span>
        </div>
        <div class="card-body p-0">
          <pre class="line-numbers m-0"><code class="language-java">{html.escape(jf.code)}</code></pre>
        </div>
      </div>
      {nav_html}
    </div>
  </section>
</main>
{FOOTER.format(root="../")}
"""
    return HEAD.format(title=jf.title, root="../") + body


# --------------------------------------------------------------------------- #
# Assets                                                                      #
# --------------------------------------------------------------------------- #

SITE_CSS = """/* HelloJava custom theme (layered on MDB + Prism Tomorrow) */

:root {
  --hj-primary:    #1976d2;
  --hj-primary-d:  #0d47a1;
  --hj-accent:     #ff9800;
  --hj-bg:         #f5f7fa;
  --hj-surface:    #ffffff;
  --hj-text:       #212529;
  --hj-muted:      #6c757d;
  --hj-border:     #e0e4e8;
  --hj-navbar-h:   64px;
  --hj-sidebar-w:  300px;
}

[data-bs-theme="dark"] {
  --hj-bg:         #121826;
  --hj-surface:    #1c2333;
  --hj-text:       #e6e8eb;
  --hj-muted:      #9aa0a6;
  --hj-border:     #2a3142;
}

html, body { height: 100%; }
body {
  font-family: 'Roboto', system-ui, -apple-system, 'Segoe UI', sans-serif;
  background: var(--hj-bg);
  color: var(--hj-text);
  padding-top: var(--hj-navbar-h);
}

code, pre, .font-monospace {
  font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', Menlo, Consolas, monospace !important;
}

.material-icons,
.material-icons-outlined {
  vertical-align: middle;
  font-size: 1.1em;
}
.material-icons.small,
.material-icons-outlined.small { font-size: 1em; margin-right: 2px; }

/* --- Navbar --- */
.hj-navbar {
  background: linear-gradient(135deg, var(--hj-primary) 0%, var(--hj-primary-d) 100%);
  height: var(--hj-navbar-h);
  z-index: 1040;
}
.hj-navbar .navbar-brand { color: #fff !important; font-size: 1.25rem; }
.hj-navbar .badge { font-size: 0.7rem; }

.hj-search {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.15);
  border-radius: 24px;
  padding: 6px 14px;
  min-width: 280px;
  transition: background 0.2s;
}
.hj-search:focus-within { background: rgba(255,255,255,0.25); }
.hj-search .material-icons { color: rgba(255,255,255,0.8); }
.hj-search input {
  background: transparent;
  border: 0;
  outline: 0;
  color: #fff;
  padding: 0 8px;
  width: 100%;
  font-size: 0.9rem;
}
.hj-search input::placeholder { color: rgba(255,255,255,0.7); }

#hj-search-results {
  position: absolute;
  top: 48px;
  left: 0;
  right: 0;
  background: var(--hj-surface);
  color: var(--hj-text);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.2);
  max-height: 60vh;
  overflow-y: auto;
  display: none;
}
#hj-search-results.show { display: block; }
#hj-search-results a {
  display: block;
  padding: 10px 14px;
  text-decoration: none;
  color: var(--hj-text);
  border-bottom: 1px solid var(--hj-border);
}
#hj-search-results a:hover { background: var(--hj-bg); }
#hj-search-results .hj-result-phase {
  font-size: 0.75rem;
  color: var(--hj-muted);
}

/* --- Layout (sidebar + main) --- */
.hj-main {
  margin-left: 0;
  min-height: calc(100vh - var(--hj-navbar-h));
}
@media (min-width: 992px) {
  .hj-main { margin-left: var(--hj-sidebar-w); }
}

.hj-sidebar {
  width: var(--hj-sidebar-w);
  background: var(--hj-surface);
  border-right: 1px solid var(--hj-border);
}
@media (min-width: 992px) {
  .hj-sidebar {
    position: fixed;
    top: var(--hj-navbar-h);
    left: 0;
    bottom: 0;
    transform: none !important;
    visibility: visible !important;
    overflow-y: auto;
    display: block;
  }
}
.hj-sidebar .accordion-button {
  font-size: 0.95rem;
  color: var(--hj-text);
  box-shadow: none !important;
}
.hj-sidebar .accordion-button:not(.collapsed) {
  background: rgba(25, 118, 210, 0.08);
  color: var(--hj-primary);
}
.hj-sidebar .accordion-item {
  border-bottom: 1px solid var(--hj-border) !important;
}
.hj-sidebar-link {
  color: var(--hj-text);
  text-decoration: none;
  font-size: 0.875rem;
  border-left: 3px solid transparent;
  transition: all 0.15s;
}
.hj-sidebar-link:hover {
  background: var(--hj-bg);
  border-left-color: var(--hj-primary);
  color: var(--hj-primary);
}
.hj-sidebar-link.active {
  background: rgba(25, 118, 210, 0.1);
  border-left-color: var(--hj-primary);
  color: var(--hj-primary);
  font-weight: 500;
}
.hj-sidebar-section {
  font-size: 0.7rem;
  letter-spacing: 0.06em;
  font-weight: 700;
}

/* --- Hero (home) --- */
.hj-hero {
  background: linear-gradient(135deg, var(--hj-primary) 0%, var(--hj-primary-d) 100%);
  margin-bottom: 0;
}
.hj-hero h1 { line-height: 1.15; }
.hj-hero-code {
  background: rgba(0,0,0,0.4) !important;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 12px;
  padding: 24px;
  font-size: 0.95rem;
  color: #fff;
}

/* --- Hero (phase/code page) --- */
.hj-page-hero {
  background: linear-gradient(135deg, var(--hj-primary) 0%, var(--hj-primary-d) 100%);
  color: #fff;
}
.hj-page-hero .breadcrumb a { text-decoration: none; }
.hj-page-hero .breadcrumb a:hover { color: #fff !important; }
.hj-page-hero .breadcrumb-item + .breadcrumb-item::before { color: rgba(255,255,255,0.5); }
.hj-page-icon {
  width: 56px; height: 56px;
  background: rgba(255,255,255,0.18);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
}
.hj-page-icon .material-icons { font-size: 32px; color: #fff; }
.hj-page-icon-sm { width: 44px; height: 44px; border-radius: 10px; }
.hj-page-icon-sm .material-icons { font-size: 24px; }

/* --- Phase cards (home) --- */
.hj-phase-card {
  background: var(--hj-surface);
  color: var(--hj-text);
  border: 0;
  border-radius: 14px;
  transition: transform 0.2s, box-shadow 0.2s;
  border-top: 4px solid var(--hj-primary);
}
.hj-phase-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.12) !important;
}
.hj-phase-icon {
  width: 56px; height: 56px;
  background: linear-gradient(135deg, var(--hj-primary) 0%, var(--hj-primary-d) 100%);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
}
.hj-phase-icon .material-icons { font-size: 28px; color: #fff; }

/* --- File cards (phase page) --- */
.hj-section-marker {
  width: 6px;
  height: 28px;
  background: var(--hj-primary);
  border-radius: 3px;
}
.hj-file-card {
  background: var(--hj-surface);
  color: var(--hj-text);
  border: 1px solid var(--hj-border);
  border-radius: 10px;
  transition: transform 0.15s, box-shadow 0.15s, border-color 0.15s;
}
.hj-file-card:hover {
  transform: translateY(-2px);
  border-color: var(--hj-primary);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08) !important;
}
.hj-file-card .hj-file-icon { color: var(--hj-primary); font-size: 22px; }
.hj-file-card .hj-file-summary {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.45;
}
.min-w-0 { min-width: 0; }

/* --- Code page --- */
.hj-summary {
  background: rgba(25, 118, 210, 0.08);
  border: 0;
  border-left: 4px solid var(--hj-primary);
  color: var(--hj-text);
  border-radius: 6px;
}

.hj-code-card { border: 0; border-radius: 10px; overflow: hidden; }
.hj-code-card .card-header { border-bottom: 1px solid #000; padding: 10px 16px; }
.hj-traffic-light {
  display: inline-block;
  width: 12px; height: 12px;
  border-radius: 50%;
}
.hj-code-card pre[class*="language-"] {
  margin: 0;
  border-radius: 0;
  padding: 18px 12px 18px 50px;
  font-size: 14px;
  line-height: 1.6;
  max-height: 78vh;
}

/* Prism line-numbers tweaks */
pre.line-numbers > code { padding-left: 0; }
.line-numbers .line-numbers-rows {
  border-right-color: #444 !important;
  padding-top: 18px;
}
.line-numbers .line-numbers-rows > span:before {
  color: #777 !important;
}

/* Prism toolbar (copy button) */
div.code-toolbar > .toolbar { top: 12px; right: 16px; }
div.code-toolbar > .toolbar button {
  background: rgba(255,255,255,0.1);
  color: #fff;
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 0.75rem;
  font-family: 'Roboto', sans-serif !important;
  border: 1px solid rgba(255,255,255,0.2);
}
div.code-toolbar > .toolbar button:hover {
  background: var(--hj-primary);
  border-color: var(--hj-primary);
}

/* --- Footer --- */
.hj-footer { border-top: 1px solid var(--hj-border); }

/* --- Utility / dark mode harmonization --- */
[data-bs-theme="dark"] .card { background-color: var(--hj-surface); color: var(--hj-text); }
[data-bs-theme="dark"] .hj-sidebar { background: var(--hj-surface); }
[data-bs-theme="dark"] .hj-sidebar-link { color: var(--hj-text); }
[data-bs-theme="dark"] .hj-sidebar-link:hover { background: #232a3b; }
[data-bs-theme="dark"] .badge.bg-light { background: #2a3142 !important; color: var(--hj-text) !important; }
[data-bs-theme="dark"] .text-muted { color: var(--hj-muted) !important; }
[data-bs-theme="dark"] #hj-search-results a { color: var(--hj-text); }
[data-bs-theme="dark"] #hj-search-results a:hover { background: #232a3b; }
[data-bs-theme="dark"] .hj-summary {
  background: rgba(25, 118, 210, 0.18);
  color: var(--hj-text);
}
[data-bs-theme="dark"] .breadcrumb { --bs-breadcrumb-divider-color: var(--hj-muted); }

/* Responsive */
@media (max-width: 768px) {
  .hj-search { display: none; }
  .hj-hero h1 { font-size: 2.2rem; }
  .hj-code-card pre[class*="language-"] { font-size: 12.5px; padding-left: 42px; }
}
"""


SITE_JS = """// HelloJava site interactivity
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
    const depth = (location.pathname.match(/\\/(phase|code)\\//) ? '../' : '');
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
      } catch (e) { console.warn('search manifest load failed', e); }
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
      return String(s).replace(/[&<>\"']/g, function (c) {
        return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '\"': '&quot;', \"'\": '&#39;' }[c];
      });
    }
  }
})();
"""


# --------------------------------------------------------------------------- #
# Driver                                                                      #
# --------------------------------------------------------------------------- #

def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def main() -> None:
    # Clean previous build, but tolerate Windows file locks (IDE indexers, open browsers).
    if OUT.exists():
        for sub in ("code", "phase", "assets"):
            d = OUT / sub
            if d.exists():
                for p in d.glob("*"):
                    try:
                        p.unlink()
                    except OSError:
                        pass
        for f in OUT.glob("*.html"):
            try:
                f.unlink()
            except OSError:
                pass
        for f in OUT.glob("*.json"):
            try:
                f.unlink()
            except OSError:
                pass
    OUT.mkdir(parents=True, exist_ok=True)

    phases = discover()

    # index
    write(OUT / "index.html", render_index(phases))

    # phase pages
    for p in phases:
        write(OUT / "phase" / f"{p.key}.html", render_phase(p, phases))

    # code pages
    total = 0
    for p in phases:
        for files in p.sections.values():
            for jf in files:
                write(OUT / "code" / f"{jf.slug}.html", render_code(jf, phases))
                total += 1

    # assets
    write(OUT / "assets" / "site.css", SITE_CSS)
    write(OUT / "assets" / "site.js", SITE_JS)

    # search manifest (flat list of all files)
    manifest = []
    for p in phases:
        for sec_name, files in p.sections.items():
            for f in files:
                manifest.append({
                    "title": f.title,
                    "slug": f.slug,
                    "path": f.rel_path.as_posix(),
                    "phaseLabel": p.label,
                    "section": "Overview" if sec_name == "_root" else split_camel(sec_name),
                    "summary": f.summary,
                })
    write(OUT / "manifest.json", json.dumps(manifest, indent=2))

    print(f"Generated {total} code pages, {len(phases)} phase pages -> {OUT}")


if __name__ == "__main__":
    main()