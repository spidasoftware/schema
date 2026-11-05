---
name: release-diff
description: Summarize consumer-relevant changes between two release tags of the spidasoftware/schema repo, split into high-level functional changes and potentially breaking classpath/schema changes. Use when asked what changed between versions, for release notes, or for upgrade impact.
---

# Release diff: consumer-impact summary

Produce a short, two-section summary of what a consuming application needs to know
when moving from tag A to tag B. Ignore plain dependency version bumps.

## Inputs

- `FROM` tag (e.g. `v12.0.3`). Default: the previous tag by version order.
- `TO` tag (e.g. `v13.0.0`). Default: latest tag, or `HEAD` if asked about unreleased work.

Verify both tags exist first: `git tag -l "<FROM>" "<TO>"`.

## Procedure

1. Orientation (one command, no pager):
   ```
   git --no-pager log --no-merges --format="%s" FROM..TO
   git --no-pager diff --name-status FROM..TO
   ```
   Drop `bruno-collection/`, `resources/examples/**/*.json`, and `doc/` from the file
   list unless a commit subject hints at an API surface change (new endpoint, auth
   change, removed method). Those are summarized from commit subjects only.

2. Read the full diff of the paths that matter:
   ```
   git --no-pager diff FROM..TO -- resources/schema src/main build.gradle
   ```
   If output exceeds ~20 KB it is saved to a temp file; read it in ranges rather than
   re-running the command.

3. If the diff touches `doc/apis/*.md`, grep the doc diff for new endpoints
   (`+\`http://...` lines) and new `##` headings.

4. Classify every change using the heuristics below, then write the output.

## Repo-specific heuristics

**Version signals**
- `build.gradle` `version` and `ConverterUtils.currentVersion` move together on a major
  bump. A new `case <engineVersion>: return N` in `ConverterUtils.getSchemaVersion`
  maps a calc engine version to the schema version — state it.

**Classpath (Java/Groovy under `src/main`)**
- New `conversion/changeset/vN/` package or class → additive; list it.
- Removed/renamed class, changed public method signature, changed constructor → breaking.
- New public method on an existing class → additive, mention briefly.
- Removed dependency declarations in `build.gradle` (not version bumps) → note only if a
  consumer might have relied on it transitively (e.g. `ant-launcher` removal).

**Schema (`resources/schema/**/*.schema`)** — treat as breaking:
- Property removed or renamed.
- Property added to `required`.
- New `oneOf` / `anyOf` / `not` constraints, or mutual-exclusion rules.
- New property on a schema with `additionalProperties: false` is **additive for the new
  version but fails validation on older validators** — call this out, even in patch
  releases (e.g. `station_wrapper.sortOrder` backport).
- New `enum` values are additive (e.g. `method_response` error codes, form field types),
  but consumers switching on the enum should be warned.

**Changeset behavior (`revert*` / `applyTo*` methods)**
- Any change to what a `revert*` method strips or rewrites alters down-converted JSON
  output. Report it under functional changes even in patch releases, since consumers
  snapshotting converted output will see diffs.
- NPE/null-safety fixes: mention collectively, with the affected changeset names.

**Ignore**
- Dependency version-only changes, copyright year updates, README link fixes,
  whitespace, test-only changes (unless they reveal intent not visible in main code).

## Output format

Keep the whole response under ~350 words for a major release, ~200 for a patch.

```
## FROM → TO — Consumer-relevant changes

<one line: major/minor/patch; schema version N→M or "no schema version bump">

### Functional changes (high level)
- bullets grouped by feature area (calc schema, CEE, SPIDAmin, converters, API docs)
- one bullet per feature, name the schema/class, no field-by-field prose

### Potentially breaking changes
- schema: removed/renamed/required/oneOf changes, with file and property names
- classpath: new/removed packages or classes, signature changes; or "none"
- behavioral: down-conversion output differences
- dependency notes only if a non-version-bump removal could affect consumers
```

End with at most one short suggestion if a process improvement is evident (e.g.
automating the schema key diff). Do not pad.
