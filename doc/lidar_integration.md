Lidar / Point Cloud Integration Guide
=====================================

This guide is for integrators converting lidar scans, photogrammetry, or other classified point-cloud datasets into SPIDA project JSON. It answers the most common question we get from these integrators:

> "What are the main fields of the JSON to be filled in from the lidar acquisition?"

It covers which fields matter most to SPIDAcalc's loading analysis, how measured geometry maps into the schema, and what information a point cloud likely *cannot* provide (and where you maybe able to get it from instead).

Read this alongside the [Data Requirements](data_requirements.md) overview and the [Calc Integration API](calc.md) guide.

## The Short Answer

In priority order, the data to extract and populate:

| Priority | Data | Schema fields |
| --- | --- | --- |
| 1 | **Pole identity & geometry** | `location.label`, `location.geographicCoordinate`, `pole.clientItem` (species/class/height) or `pole.clientItemAlias`, measured `pole.agl` and `pole.glc` |
| 2 | **Span geometry** (where adjacent poles/buildings are) | `wireEndPoints[]`: `distance`, `direction`, `relativeElevation`, `type` |
| 3 | **Wire attachments** | `wires[]`: `attachmentHeight`, `usageGroup`, `owner`, `clientItem`/`clientItemAlias`, `tensionGroup`; plus each wire's ID listed in the correct `wireEndPoints[].wires` |
| 4 | **Guying** | `anchors[]`: `distance`, `direction`, `height`; `guys[]`: `attachmentHeight`; `spanGuys[]`; anchor↔guy associations |
| 5 | **Wire sag** (lidar's unique value-add) | `wires[].measuredSag` or `wires[].midspanHeight`, `spanPoints` |
| 6 | **Other attachments** | `equipments[]`, `crossArms[]`, `insulators[]` heights and directions |
| 7 | **Pole condition & metadata** | `pole.leanAngle`/`leanDirection`, `images`, `poleTags`, `externalId` everywhere |

Items 1–4 are what the [Data Requirements](data_requirements.md) doc calls the "highest priorities for accurate loading": pole species/height/class, span geometry, wire type and tensions, and guying. Without them, loading results will rarely be useful. Item 5 is where lidar shines compared to traditional field collection, though tension adjustment factor through measured sag feature is not yet released, and set to be released in SPIDAcalc in v26.

## How the JSON Is Organized

A SPIDAcalc project file is a hierarchy:

```
project                     (clientFile, schema, label)
└── leads[]                 (a string of poles - a circuit or logical grouping)
    └── locations[]         (one physical pole site: label, geographicCoordinate, images)
        └── designs[]       (layerType: "Measured" for field/lidar-collected data)
            └── structure   (the actual model)
                ├── pole
                ├── wireEndPoints[]   (where spans go)
                ├── wires[]           (each span's attachment on this pole)
                ├── guys[], spanGuys[], anchors[]
                ├── equipments[], crossArms[], insulators[]
                └── spanPoints[], damages[], ...
```

Schemas: [project](../resources/schema/spidacalc/calc/project.schema) · [location](../resources/schema/spidacalc/calc/location.schema) · [design](../resources/schema/spidacalc/calc/design.schema) · [structure](../resources/schema/spidacalc/calc/structure.schema) · [pole](../resources/schema/spidacalc/calc/pole.schema) · [wire](../resources/schema/spidacalc/calc/wire.schema) · [wire end point](../resources/schema/spidacalc/calc/wire_end_point.schema)

Key modeling concepts:

- **Each structure models ONE pole.** Adjacent poles are represented as **wire end points** (WEPs) — a distance, direction, and elevation from the pole under analysis. A 100-pole lidar corridor becomes 100 locations, each with its own structure.
- **Each span is a separate wire.** A conductor passing through a pole is two wires in that structure: one to the `NEXT_POLE` WEP and one to the `PREVIOUS_POLE` WEP.
- **Every physical component references client data.** Material and engineering properties (wood strength, conductor weight/diameter, anchor capacity) live in the utility's pre-configured **client file**, not in your JSON. Your JSON references those items via `clientItem` or `clientItemAlias`. Getting these references to match the client file is a critical integration task — see [What the Point Cloud Cannot Tell You](#what-the-point-cloud-cannot-tell-you) below.
- **Use a "Measured" design layer.** Set `designs[].layerType` to `"Measured"` for as-collected data.
- **Use `externalId`.** Nearly every object supports an `externalId` that SPIDAcalc preserves but never modifies. Populate it with your own feature/segment IDs so you can match results back to your point-cloud objects. See [Matching Components](input_assemblies.md#matching-components-between-spidacalc-and-the-integrator).

## Why These Fields: What the Analysis Actually Uses

Understanding how SPIDAcalc consumes each field explains the priority order:

- **Wire tension × attachment height ⇒ bending moment.** The dominant loads on most poles are horizontal wire tensions applied at their attachment heights, plus wind on the wire spans. This is why `attachmentHeight`, span geometry, and tension identification are critical.
- **WEP distance/direction ⇒ span loads and load angles.** Span length drives the wind and weight span on the pole; the directions of the spans determine whether tensions balance (tangent) or produce large net loads (deadends, angles). Small errors in direction at line angles produce large errors in results.
- **Measured `glc` and `agl` are preferred over nominal values.** When you supply a measured ground line circumference and above-ground length, the engine uses them directly for pole strength and geometry instead of deriving defaults from the species/class tables (default embedment is 10% of length + 2 ft). Lidar-derived values here directly improve accuracy over "nominal" assumptions.
- **Guys are the restraint system.** Guy attachment heights and anchor lead distance/direction determine whether a deadend or angle pole passes. Wrong or missing guy geometry invalidates results on exactly the poles clients care most about.
- **Pole lean adds second-order effects.** `leanAngle`/`leanDirection` feed P-Δ moment magnification.
- **Sag ⇒ tension.** If you provide `measuredSag`, SPIDAcalc back-calculates a per-wire tension adjustment from your measurement — see [Wire Tension Options](#wire-tension-options-what-to-do-without-tension-data).
- **Equipment/crossarm/insulator details are secondary for pole loading** but matter for clients that analyze those components, and for wind/weight contribution of large equipment. Prioritize getting them *present with correct heights*; exact model selection can follow client guidance.

## Field-by-Field Mapping: Point Cloud ⇒ Schema

What you can measure from a classified point cloud and where it goes:

| You measured | Schema field | Notes |
| --- | --- | --- |
| Pole base position (lat/lon) | `location.geographicCoordinate` | GeoJSON Point, `coordinates` are `[longitude, latitude]` |
| Pole height above ground | `pole.agl` | Measured height from ground line to pole top. If the pole top is broken/cut, also see `pole.cutTop` |
| Pole circumference at ground line | `pole.glc` | Fit from the point cloud at the ground line. Engine prefers this measured value |
| Pole lean | `pole.leanAngle` (integer degrees from vertical, 0–20), `pole.leanDirection` (bearing the top points) |  |
| Adjacent pole/building position | `wireEndPoints[].distance` (horizontal span length), `.direction` (bearing), `.relativeElevation` (base-to-base elevation change), `.type` (`NEXT_POLE`, `PREVIOUS_POLE`, `OTHER_POLE`, `BUILDING`) | One NEXT and one PREVIOUS max; any number of OTHER/BUILDING |
| Wire attachment height on pole | `wires[].attachmentHeight` | Height above the ground line, per span |
| Which wires run in which span | `wireEndPoints[].wires` (array of wire IDs) | **A wire not listed in any WEP has no span and will stop analysis** |
| Wire class (power vs comm vs neutral) | `wires[].usageGroup` | `PRIMARY`, `NEUTRAL`, `SECONDARY`, `COMMUNICATION`, `COMMUNICATION_BUNDLE`, etc. Your classifier's power/comm zones map here |
| Wire sag / lowest point | `wires[].measuredSag` (sag + temperature + `distanceFromPole` or `geographicCoordinate`) | Used to back-calculate tension. Alternatively record `wires[].midspanHeight` |
| Wire height at specific span stations (crossings, clearance points) | `spanPoints[]` (distance from pole, environment, per-wire heights), referenced from `wireEndPoints[].spanPoints` | For clearance workflows |
| Exact far-end attachment point | `wires[].wireEndPointPlacement` | Optional; relative to the WEP base |
| Guy attachment height | `guys[].attachmentHeight` | Plus `clientItem`/`clientItemAlias` for the guy wire type |
| Anchor position | `anchors[].distance` (lead from pole), `.direction`, `.height` (relative to pole base), `.guys` (IDs of guys on this anchor), `.supportedWEPs` |  |
| Span guy (pole-to-pole guy) | `spanGuys[]` with `attachmentHeight` (this pole) and `height` (far end), listed in `wireEndPoints[].spanGuys` |  |
| Equipment (transformers, risers, lights…) | `equipments[]`: `attachmentHeight` (attach/bolt height), `bottomHeight` (bottom of unit), `direction` | Type comes from `clientItem`/`clientItemAlias` |
| Crossarm | `crossArms[]`: `attachmentHeight`, `direction` (bearing the arm end points), `offset`, `supportedWEPs`, `insulators` |  |
| Insulator | `insulators[]`: `offset` (attach height on pole, or distance along arm), `direction`, `wires` (IDs held) | How wires connect to pole/arms |
| Terrain under spans | `wireEndPoints[].terrainPoints`, `design.terrainLayer` | For clearance analysis |
| Ground environment | `pole.environment`, `wireEndPoints[].environment`, `spanPoints[].environment` | STREET, RAILROAD, etc. — names from client data |
| Photos / ortho snapshots | `location.images[]` (`url`, optional `direction`) | Bundle in the [exchange file](calc.md#calc-exchange-file-format) Photos directory |
| Your feature IDs | `externalId` on every component | Round-trips unmodified |
| Anything else (scan date, point counts, QC flags…) | `location.userDefinedValues`, `project.userDefinedValues` | Flat string key-value object |

### Fields you must fill from *other* sources

These are required, but do not come from geometry:

| Field | Required on | Typical source |
| --- | --- | --- |
| `owner` (`id` + `industry`) | pole, every wire, guy, anchor, equipment… | Utility records / attachment agreements; often inferred from usage group and height zone |
| `clientItem` / `clientItemAlias` | every physical component | Client file lookup (see below) |
| `tensionGroup` or a tension alternative | every wire | Client standards / assumption / `measuredSag` |
| `clientFile` | project | The utility's client file name, e.g. `"Demo.client"` |

## What the Point Cloud Cannot Tell You

A classified point cloud gives excellent *geometry* but almost no *identity*. Plan your integration around filling these gaps:

1. **Pole species and class.** A scan gives you height and circumference, but analysis needs the material properties behind `clientItem: {species, classOfPole, height}` (or an alias). Sources: utility GIS/asset records, pole tags (birthmarks) captured in field photos, or client-approved default assumptions. Supplying measured `glc`/`agl` softens — but does not eliminate — the impact of an assumed class, because strength still comes from the client pole's material properties.
2. **Conductor type and size.** Classification can distinguish power vs communication zones (→ `usageGroup`), but "1/0 ACSR" vs "4/0 ACSR" comes from records or client-directed assumptions. This drives wire weight, diameter (wind area), and tension — it matters.
3. **Tension.** Never directly visible. You have three options — see the next section. This is where lidar has an advantage: you can *measure sag* instead of assuming.
4. **Ownership.** `owner` is required on the pole and every attachment. Most integrations assign owners by usage group / height zone per client rules.
5. **Component catalog matching.** Every `clientItem`/`clientItemAlias` string must match the utility's client file. Use the [Client Data API](apis/clientAPI.md) to read available items, and use **aliases** so your system's names map cleanly — see [On Aliases](input_assemblies.md#on-aliases) and [Client References](clientReferences.md). Agreeing on this mapping with the client is usually the first step of a deployment.
6. **Underground/hidden features.** Anchors are often obscured by vegetation; guy wires are thin and may be sparsely sampled. Have a QC plan for guying — it is too important to silently drop.

## Wire Tension Options (What To Do Without Tension Data)

Every wire needs a tension for analysis. In order of typical preference for lidar workflows:

1. **`tensionGroup`** — name a pre-configured tension group on the client wire (e.g. "Full Tension", "Slack"). This is the standard approach; the utility chooses the assumption. Using a **wire alias** selects both the wire type and its tension group in one string.
2. **`measuredSag`** — provide the sag you measured from the point cloud plus the temperature at acquisition and either the distance from the pole or the geographic coordinate of the measurement point. SPIDAcalc computes a tension adjustment from your actual measurement. `{ "sag": {...}, "temperature": {...}, "distanceFromPole": {...} }`
3. **`adjustedTension`** — a known tension value (e.g. from a dynamometer or the client's sag-tension run).
4. **`tensionAdjustment`** — a bare multiplier on the tension group value.

Only one of `measuredSag` / `adjustedTension` / `tensionAdjustment` may be present on a wire. Independently of these, `midspanHeight` can record the measured midspan height, and `spanPoints` can record wire-over-ground heights for clearance checking.

## Units, Datums, and Conventions

- **Measured values** are objects: `{"value": 12.3, "unit": "METRE"}`. Common units: `METRE`, `FOOT`, `INCH`, `CENTIMETRE` for lengths; `POUND_FORCE`, `NEWTON` for forces; `FAHRENHEIT`, `CELSIUS` for temperature ([full list](../resources/schema/general/unit.schema)). Metric and imperial can be mixed freely.
- **Directions** are bearings in degrees from **true north** (0 = north, 90 = east), numbers 0–360. Convert from your projection's grid north and account for convergence; do not send magnetic bearings or radians.
- **Heights** (`attachmentHeight`, `agl`, etc.) are **relative to the pole's ground line**, not absolute elevations. Convert from your point cloud's vertical datum by subtracting the ground elevation at each pole.
- **`wireEndPoints[].distance`** is the horizontal span length; vertical change goes in `relativeElevation` (base of this pole to base of the far pole).
- **`anchors[].height`** is relative to the base of the pole (uphill anchors positive, downhill negative).
- **`geographicCoordinate`** is GeoJSON: `{"type": "Point", "coordinates": [longitude, latitude]}` — longitude first, WGS84.
- **`id`** values are display IDs and must be unique within the structure (e.g. `"Wire#1"`, `"WEP#1"`, `"Anchor#1"`). Cross-references (WEP→wires, anchor→guys, insulator→wires) use these IDs. IDs may contain letters, numbers, underscores, spaces, and `#`, must be at least 2 characters, and must not begin or end with a space. See [Identifiers and Validation](calc.md#identifiers-and-validation) for the rules on every ID type.

## Validation Checklist

Common import/analysis blockers integrators hit, roughly in order of frequency. SPIDAcalc will refuse to analyze ("analysis stoppers") structures with:

- A wire whose ID is not listed in any `wireEndPoints[].wires` array (wire with no span), or WEP/insulator/anchor references to IDs that don't exist.
- `clientItem`/`clientItemAlias` strings that don't resolve against the client file (wrong name, wrong client file, alias not configured).
- A `tensionGroup` that doesn't exist on that client wire, or no tension information at all.
- Missing `owner` or `usageGroup` on wires; missing `owner` on the pole.
- `attachmentHeight` above the pole top (higher than `agl`) or negative. Watch for this when the measured `agl` is shorter than the nominal pole assumption, and remember heights are above *ground line*, not absolute.
- Anchor `distance` missing or under 1 foot; guys not associated with an anchor and pole.
- Missing `relativeElevation`/`inclination` on WEPs (one of the two is required), missing `distance` or `direction`.

Recommended practice:

- Validate every file against [project.schema](../resources/schema/spidacalc/calc/project.schema) in your pipeline (see [Schema Validation](../README.md#tools)); add `"strict": true` in dev/test to catch misspelled fields, and remove it in production.
- After analysis, check that every design you sent has `analysis` results — a design with no results failed a validation stopper (see [calc.md](calc.md) command line tool notes).

## Suggested Development Path

1. **Locations first.** Emit a project with just labeled locations, `geographicCoordinate`, and photos. This already imports usefully into SPIDAcalc ([Basic Location Information](data_requirements.md#basic-location-information)).
2. **Pole + spans.** Add pole clientItem/alias with measured `agl`/`glc`, and `wireEndPoints` from your span detection.
3. **Wires.** Add wires with measured attachment heights, usage groups from your classifier, and client aliases + tension groups agreed with the utility. Add `measuredSag` where quality allows.
4. **Guying, equipment, crossarms, insulators.** Complete the structure.
5. **Package as an exchange file** (`.exchange.spida` zip with `project.json` + Photos — [format](calc.md#calc-exchange-file-format)), validate, open in SPIDAcalc, analyze, and review with the client. Then automate analysis via the [command line tool](calc.md#command-line-tool) or [SPIDAcee](cee.md) if desired, and read back [results](results.md).

Working examples to crib from: [lidar measured project](../resources/examples/spidacalc/projects/lidar_measured_project.json) (start here — a complete "Measured" design built the way this guide describes) · [lidar connected locations project](../resources/examples/spidacalc/projects/lidar_connected_locations_project.json) (three connected poles with connected wires and measured sags on every span) · [minimal project with GPS](../resources/examples/spidacalc/projects/minimal_project_with_gps.json) · [full project](../resources/examples/spidacalc/projects/full_project.json) · [one-of-everything structure](../resources/examples/spidacalc/designs/one_of_everything.json)

## Related Documentation

- [Data Requirements](data_requirements.md) — what information is required for pole loading generally
- [Calc Integration API](calc.md) — data format, exchange files, web services, command line analysis
- [Input Assemblies](input_assemblies.md) — alternative "construction standard" style integration (usually a better fit for staking/GIS data than for lidar, which measures each attachment individually)
- [Client References](clientReferences.md) — how component lookups against client data work
- [Results Guide](results.md) — reading analysis results back out
