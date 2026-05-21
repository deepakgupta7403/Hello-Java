"""
Normalize paragraph breaks inside Javadoc blocks to the convention used by
src/Phase0_SetupAndFirstPrograms/Introduction/Introduction.java:

    * text of previous paragraph.
    * <p>
    *
    * Next paragraph starts here.

Algorithm
---------
For each Javadoc block (between `/**` and `*/`):
  Identify maximal runs of "filler" lines, where a filler line is either
    - a blank Javadoc line (` *` with no content after the asterisk), or
    - a `<p>` line (` * <p>`).
  A run that sits between two non-filler Javadoc text lines (i.e. a real
  paragraph break) is replaced with exactly two lines:
    ` * <p>\n`
    ` *\n`
  Filler runs at the start of the block (right after `/**`) or at the end
  (right before `*/`) are left alone.

The transformation is idempotent: a block that already matches the target
pattern is unchanged.

Run with --dry-run to count proposed changes without writing.
"""

import argparse
import sys
from pathlib import Path


def is_javadoc_text_line(stripped: str) -> bool:
    """A Javadoc body line that isn't blank, isn't a <p> tag, isn't /** or */."""
    if not stripped.startswith("*"):
        return False
    if stripped in ("*/",):
        return False
    if stripped.startswith("/**"):
        return False
    body = stripped[1:].strip()
    if body == "":
        return False
    if body.lower() == "<p>":
        return False
    return True


def is_filler_line(stripped: str) -> bool:
    """Blank Javadoc line or a sole <p> line."""
    if not stripped.startswith("*") or stripped.startswith("/**") or stripped == "*/":
        return False
    body = stripped[1:].strip()
    return body == "" or body.lower() == "<p>"


def transform(text: str):
    # Preserve original line endings by splitting with keepends
    lines = text.splitlines(keepends=True)
    out = []
    in_javadoc = False
    i = 0
    changes_count = 0

    # Build a parallel array of stripped versions for quick checks
    stripped = [ln.strip() for ln in lines]

    while i < len(lines):
        line = lines[i]
        s = stripped[i]

        if not in_javadoc:
            if s.startswith("/**"):
                in_javadoc = True
            out.append(line)
            i += 1
            continue

        # in_javadoc == True
        if s == "*/" or s.startswith("*/"):
            in_javadoc = False
            out.append(line)
            i += 1
            continue

        if is_filler_line(s):
            # Walk forward to find the end of this filler run.
            j = i
            while j < len(lines) and is_filler_line(stripped[j]):
                j += 1

            # Determine context: was the previous emitted line a Javadoc text
            # line, and is the next line a Javadoc text line?
            prev_is_text = len(out) > 0 and is_javadoc_text_line(out[-1].strip())
            next_is_text = j < len(lines) and is_javadoc_text_line(stripped[j])

            run_lines = lines[i:j]
            run_count = j - i

            if prev_is_text and next_is_text:
                # Replace with normalized two-line break: <p> then blank.
                # Use the indentation of the first line in the run.
                first = run_lines[0]
                indent = first[: len(first) - len(first.lstrip())]
                ending = ""
                if first.endswith("\r\n"):
                    ending = "\r\n"
                elif first.endswith("\n"):
                    ending = "\n"

                replacement = [
                    f"{indent}* <p>{ending}",
                    f"{indent}*{ending}",
                ]
                # Count changes only if replacement differs from original run.
                if replacement != run_lines:
                    changes_count += 1
                out.extend(replacement)
            else:
                # Filler at block boundary — leave as-is.
                out.extend(run_lines)

            i = j
            continue

        out.append(line)
        i += 1

    return "".join(out), changes_count


def process_file(path: Path, dry_run: bool):
    raw = path.read_bytes()
    text = raw.decode("utf-8")
    new_text, changes = transform(text)
    if not dry_run and new_text != text:
        path.write_bytes(new_text.encode("utf-8"))
    return changes


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("root", help="Root directory to scan for .java files")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    root = Path(args.root)
    total_files = 0
    changed_files = 0
    total_changes = 0
    per_file = []

    for p in sorted(root.rglob("*.java")):
        total_files += 1
        n = process_file(p, args.dry_run)
        if n:
            changed_files += 1
            total_changes += n
            per_file.append((n, p))

    mode = "DRY-RUN" if args.dry_run else "APPLIED"
    print(f"[{mode}] scanned={total_files} changed={changed_files} normalized_breaks={total_changes}")
    per_file.sort(reverse=True)
    for n, p in per_file[:10]:
        print(f"  {n:4d}  {p}")


if __name__ == "__main__":
    sys.exit(main())
