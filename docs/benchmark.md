# Developer Effort Study

A quantitative companion to the [Library Comparison](/guide/comparison): two replication studies that measure **how much code and how many distinct library concepts a developer must write to draw the same chart** in SalusChart, MPAndroidChart, and Vico.

The same chart was implemented three times — once per library — using each library's own idiomatic API, against the same shared dataset. Crucially, each chart is **styled to match the real app's visual design**, not a plain baseline: brand colors, rounded "pill" bars, dashed goal lines, clean cards with hidden axis chrome, and rounded ring caps — applied equally to all three libraries so the comparison stays fair. One implementer wrote all three versions of every chart to reduce skill-asymmetry effects. Every implementation was built and rendered on an Android emulator (API 36).

Two sample apps were measured independently:

- **Apple Health study** — six charts extracted from an Apple-Health-style summary screen.
- **Samsung Health study** — six charts extracted from a Samsung-Health-style app.

> Both studies pin **SalusChart 0.1.8**, **MPAndroidChart v3.1.0** (JitPack), and **Vico 3.2.3**, verified live on Maven Central / JitPack in June 2026. The Vico numbers are specific to the 3.x API; a 2.x re-run would differ. See [Methodology](#methodology) for definitions and reproduction.

## What was measured

| Metric | Definition |
|---|---|
| **User-LOC** | `cloc` Kotlin code lines of the chart function, minus `package`/`import` lines. Blank and comment lines are already excluded. |
| **API symbols** | Distinct charting-library symbols imported by the file — a proxy for concept load. **`0` means the chart was hand-drawn with Compose `Canvas` and the library contributed nothing.** |
| **Native** | ✅ if the library has a first-class chart type for the visual; ❌ if it had to be emulated with another type or hand-drawn. |
| **Rollup scope** | LOC/API totals are reported in two scopes: **native-only** counts only charts that all three libraries implement natively; **overall** counts all six target charts, including emulation and custom `Canvas` fallbacks. |
| **Build/render** | The app compiles and the chart renders on an emulator (API 36). All 18 cells pass in both studies. |

The shared dataset, color palette, and app scaffold are identical across all three libraries and are excluded from measurement. Domain-conversion code (e.g. mapping plain data into a library's model types) **is** measured, because that burden is a deliberate comparison point. A shared helper one library needs but the others do not — MPAndroidChart's rounded-bar renderer — is reported separately so the per-chart totals stay comparable.

## Results at a glance

Across both studies, SalusChart needed the fewest lines of code, the fewest distinct API concepts, and was <mark class="salus-highlight">the only library with a native chart type for all six visuals</mark>.

Each LOC/API cell is **total (average per chart)** within that scope. API-symbol rollups sum the per-chart values from the cell table, so they answer "how many concepts does each implementation file expose?" The native-only lens table below separately reports de-duplicated/call-site diagnostics for the same subset.

**Native-only rollup**

Only charts implemented natively by all three libraries are counted.

| Study | Charts counted | Library | LOC total (avg) | API symbols total (avg) |
|---|---|---|---:|---:|
| Apple Health | C2-C4 | **SalusChart** | <mark class="salus-highlight">87 (29.0)</mark> | <mark class="salus-highlight">10 (3.3)</mark> |
| Apple Health | C2-C4 | MPAndroidChart | 115 (38.3) | 19 (6.3) |
| Apple Health | C2-C4 | Vico | 110 (36.7) | 35 (11.7) |
| Samsung Health | C1-C3 | **SalusChart** | <mark class="salus-highlight">85 (28.3)</mark> | <mark class="salus-highlight">9 (3.0)</mark> |
| Samsung Health | C1-C3 | MPAndroidChart | 101 (33.7) | 19 (6.3) |
| Samsung Health | C1-C3 | Vico | 110 (36.7) | 44 (14.7) |

**Overall rollup**

All six target charts are counted, including emulated and hand-drawn fallback cells.

| Study | Library | LOC total (avg) | API symbols total (avg) | Native coverage |
|---|---|---:|---:|---:|
| Apple Health | **SalusChart** | <mark class="salus-highlight">183 (30.5)</mark> | <mark class="salus-highlight">24 (4.0)</mark> | <mark class="salus-highlight">6 / 6</mark> |
| Apple Health | MPAndroidChart | 248 (41.3) | 30 (5.0) | 3 / 6 |
| Apple Health | Vico | 238 (39.7) | 49 (8.2) | 3 / 6 |
| Samsung Health | **SalusChart** | <mark class="salus-highlight">160 (26.7)</mark> | <mark class="salus-highlight">20 (3.3)</mark> | <mark class="salus-highlight">6 / 6</mark> |
| Samsung Health | MPAndroidChart | 183 (30.5) | 29 (4.8) | 3 / 6 |
| Samsung Health | Vico | 186 (31.0) | 56 (9.3) | 3 / 6 |

MPAndroidChart also needs a shared rounded-bar renderer to match the pill bars: **+55 LOC** in the Apple study and **+26 LOC / +5 API symbols** in the Samsung study. With that helper counted, the Samsung MPAndroidChart total becomes **209 LOC / 34 API symbols**.

Two patterns hold in both studies:

- **Matching a real design, not just "a chart," is itself an effort axis.** The signature Apple/Samsung look uses rounded **pill bars**. SalusChart produces them from a single `barCornerRadiusFraction = 1f` parameter; **MPAndroidChart has no rounded-bar API** and needs a custom `RoundedBarChartRenderer` subclass (26 LOC in the Samsung study, 55 LOC in the Apple study), written once and reused.
- **The gap concentrates on health-shaped charts.** For generic charts (bar, line, reference line) all three are competitive and native; the divergence opens on activity rings, SleepStage charts, and floating range bars — where the general-purpose libraries have no native type and must emulate or hand-draw.

## Native-only lens checks

The full benchmark mixes generic charts with health-shaped charts because that is what real health apps need. To avoid overstating the domain argument, each study also includes a stricter **native-only lens**: only chart cells that all three libraries implement with native primitives are counted. The LOC/API rollups above report this subset separately from the overall six-chart total. The lens table below adds call-site and structural-complexity checks for the same native-only subset.

| Study | Native-common charts | SalusChart | MPAndroidChart | Vico |
|---|---|---:|---:|---:|
| **Apple Health** | C2 daily steps bar, C3 bar + avg line, C4 line + points | 10 API refs / 10 symbols / 0 config statements | 20 API refs / 19 symbols / 29 config statements | 42 API refs / 35 symbols / 0 config statements |
| **Samsung Health** | C1 heart-rate line, C2 steps bar, C3 nutrition stacked bar | 11 API refs / 7 symbols / 0 config statements | 19 API refs / 12 symbols / 49 config statements | 50 API refs / 25 symbols / 0 config statements |

The native-only result supports a narrower claim: even where all three libraries are native, SalusChart touches fewer chart-library concepts and has less chart setup surface. The stronger "health-domain chart" claim belongs to the full six-chart studies, where SalusChart <mark class="salus-highlight">covers all charts natively</mark> and MPAndroidChart/Vico fall back to emulation or custom `Canvas` drawing for three of six cells in each sample.

## Study 1 — Apple Health sample

Six charts from an Apple-Health-style summary screen, styled to the app's clean aesthetic (rounded bars, brand colors, weekday labels, rings on a black disc, no titles/axis chrome on the bar and range charts).

| ID | Chart | Data |
|---|---|---|
| C1 | Activity rings (Move / Exercise / Stand) | daily goals: kcal / min / hr |
| C2 | Daily steps bar (rounded cols, clean card) | one week, steps/day |
| C3 | Steps bar + average reference line | one week + average line |
| C4 | HRV line + point markers + axes | one week, ms/day |
| C5 | SleepStage | one night, AWAKE / REM / CORE / DEEP |
| C6 | Environmental-sound range bar + avg line | one week, min/max dB/day |

Each cell is **user-LOC · API symbols · native**:

| Chart | SalusChart | MPAndroidChart | Vico |
|---|:--:|:--:|:--:|
| C1 Activity rings | 20 · 2 · ✅ | 48 · 4 · ❌ | 37 · 0 · ❌ |
| C2 Daily steps bar | 28 · 2 · ✅ | 33 · 6 · ✅ | 32 · 10 · ✅ |
| C3 Bar + reference line | 39 · 5 · ✅ | 44 · 7 · ✅ | 45 · 13 · ✅ |
| C4 Line + points + axes | 20 · 3 · ✅ | 38 · 6 · ✅ | 33 · 12 · ✅ |
| C5 SleepStage | 40 · 6 · ✅ | 40 · 0 · ❌ | 40 · 0 · ❌ |
| C6 Range / floating bar | 36 · 6 · ✅ | 45 · 7 · ❌ | 51 · 14 · ❌ |
| **Native-only user-LOC (C2-C4)** | 87 | 115 | 110 |
| **Overall user-LOC (C1-C6)** | 183 | 248 | 238 |
| **Native-only API symbols (C2-C4)** | 10 | 19 | 35 |
| **Overall API symbols (C1-C6)** | 24 | 30 | 49 |
| **Native coverage** | 6 / 6 | 3 / 6 | 3 / 6 |

MPAndroidChart additionally needs a **55-LOC shared `RoundedBarChartRenderer`** (counted separately, above) to match Apple's rounded bars. SalusChart still has the lowest user-LOC in every single cell; folding in the shared renderer makes MPAndroidChart effectively +66%.

**Native-only lens summary**

| Lens | SalusChart | MPAndroidChart | Vico | Interpretation |
|---|---:|---:|---:|---|
| API reference sites | 10 | 20 | 42 | SalusChart has the smallest touch surface on the common native charts. |
| Distinct API symbols | 10 | 19 | 35 | Vico's model/layer/provider/component pipeline carries the largest concept load. |
| Chart-level / structural / primitive symbols | 7 / 3 / 0 | 4 / 15 / 0 | 0 / 35 / 0 | SalusChart stays closer to chart-level concepts. |
| Imperative config statements | 0 | 29 | 0 | MPAndroidChart pays View-style setup cost to suppress defaults and force the clean Apple look. |
| Max nesting depth | 3 | 3 | 7 | Vico has the deepest builder/object graph. |
| Low-level touchpoints per chart | 3.3 | 16.3 | 14.0 | Defensive check: SalusChart remains lighter on the shared native subset. |

**Per-chart notes**

- **C1 Activity rings** — SalusChart uses the purpose-built <code>MiniActivityRings</code> (one call). MPAndroidChart has no ring chart, so it overlays three donut `PieChart` views with dimmed tracks; Vico has no radial/pie/gauge type at all and is fully hand-drawn with Compose `Canvas` (zero Vico symbols). All three sit on a black disc, Apple-style.
- **C2 Daily steps bar** — all three native, rendered as a clean Apple card (no y-axis/gridlines). SalusChart uses `MinimalBarChart` + a manual weekday label row; MPAndroidChart strips its axes and routes through the custom rounded renderer; Vico simply omits the start axis but still needs the model producer + column layer + `ColumnProvider` + `CartesianValueFormatter` (10 symbols).
- **C3 / C4 (baselines)** — native everywhere. The LOC gap is modest; the concept-load gap is not (C4 point markers: SalusChart `showPoint = true` / 3 symbols vs Vico's `LineProvider → LineFill → PointProvider → Point → ShapeComponent` / 12 symbols).
- **C5 SleepStage** — the clearest gap. SalusChart has a dedicated <code>SleepStageChart</code>; neither competitor has a state-timeline primitive, so both fall back to the *same* hand-drawn Compose `Canvas` (40 LOC, 0 library symbols), with only partial parity (no stage connectors, no built-in tooltips).
- **C6 Range / floating bar** — SalusChart's first-class <code>RangeBarChart</code> with touch tooltips vs a transparent-base stacked-bar emulation in both competitors (Vico is the heaviest cell at 51 LOC / 14 symbols).

<details>
<summary>Emulator screenshots (SalusChart / MPAndroidChart / Vico, top to bottom)</summary>

![C1 Activity rings](/benchmark/apple-c1-activity-rings.png)
![C2 Daily steps bar](/benchmark/apple-c2-steps-bar.png)
![C3 Bar + reference line](/benchmark/apple-c3-bar-refline.png)
![C4 Line + points + axes](/benchmark/apple-c4-line-points.png)
![C5 SleepStage](/benchmark/apple-c5-sleepstage.png)
![C6 Range / floating bar](/benchmark/apple-c6-range-bar.png)

</details>

## Study 2 — Samsung Health sample

Six charts from a Samsung-Health-style app, styled to the Samsung spec (brand colors, green pill bars, dashed goal lines, hidden y-axis chrome).

| ID | Chart | Data |
|---|---|---|
| C1 | Heart-rate line | one day, hourly bpm |
| C2 | Steps bar (pill bars + goal line) | one week, steps/day |
| C3 | Nutrition stacked bar | one week, carbs/protein/fat g/day |
| C4 | Heart-rate range (floating) bar | one week, min/max bpm/day |
| C5 | SleepStage | one night, AWAKE/REM/LIGHT/DEEP |
| C6 | Activity rings | daily goals (steps / active-time / calories) |

Each cell is **user-LOC · API symbols · native**:

| Chart | SalusChart | MPAndroidChart | Vico |
|---|:--:|:--:|:--:|
| C1 Heart-rate line | 24 · 2 · ✅ | 33 · 6 · ✅ | 27 · 12 · ✅ |
| C2 Steps bar | 34 · 5 · ✅ | 37 · 7 · ✅ | 34 · 13 · ✅ |
| C3 Nutrition stacked bar | 27 · 2 · ✅ | 31 · 6 · ✅ | 49 · 19 · ✅ |
| C4 Heart-rate range bar | 24 · 3 · ✅ | 31 · 6 · ❌ | 37 · 12 · ❌ |
| C5 SleepStage | 38 · 6 · ✅ | 22 · 0 · ❌ | 22 · 0 · ❌ |
| C6 Activity rings | 13 · 2 · ✅ | 29 · 4 · ❌ | 17 · 0 · ❌ |
| **Native-only user-LOC (C1-C3)** | 85 | 101 | 110 |
| **Overall user-LOC (C1-C6)** | 160 | 183 | 186 |
| **Native-only API symbols (C1-C3)** | 9 | 19 | 44 |
| **Overall API symbols (C1-C6)** | 20 | 29 | 56 |
| **Native coverage** | 6 / 6 | 3 / 6 | 3 / 6 |

MPAndroidChart additionally needs a **26-LOC / 5-symbol shared `RoundedBarChartRenderer`** (reused across C2 and C4) for Samsung's pill bars — folding it in, its real total is **209 LOC / 34 symbols**.

**Native-only lens summary**

| Lens | SalusChart | MPAndroidChart | Vico | Interpretation |
|---|---:|---:|---:|---|
| API reference sites | 11 | 19 | 50 | SalusChart uses fewer chart-library calls even on shared native charts. |
| Distinct API symbols | 7 | 12 | 25 | SalusChart exposes the smallest native-common concept set. |
| Chart-level / structural / primitive symbols | 6 / 1 / 0 | 3 / 9 / 0 | 3 / 19 / 3 | Vico spends more surface area on model, layer, axis, provider, and legend plumbing. |
| Imperative config statements | 0 | 49 | 0 | MPAndroidChart requires the most mutable setup to achieve the Samsung Health look. |
| Max nesting depth | 3 | 3 | 7 | Vico has the deepest structural composition. |
| Low-level touchpoints per chart | 3.7 | 22.7 | 16.7 | Defensive check: SalusChart remains lighter before counting non-native domain charts. |

**Per-chart notes**

- **C1 Heart-rate line** — Samsung look: orange line, hidden y-axis, minimal ticks. SalusChart is one `LineChart(showYAxis = false)`; Vico needs the host + model producer + layer stack (12 symbols).
- **C2 Steps bar** — Samsung's signature **green pill bars + dashed "6,000" goal line**. SalusChart: <code>barCornerRadiusFraction = 1f</code> + one <code>ReferenceLineSpec</code>. Vico: `shape = RoundedCornerShape(50)` + a `HorizontalLine` decoration. MPAndroidChart: pill bars require the shared custom renderer; the goal line is a `LimitLine`. This is the first place the *styling* — not just the chart type — drives the gap.
- **C3 Nutrition stacked bar** — all three stack natively, but at legend parity **Vico balloons to 49 LOC / 19 symbols** (model producer + `ExtraStore` legend key + `rememberHorizontalLegend` + `LegendItem` + `ShapeComponent`) vs SalusChart's <code>showLegend = true</code> (27 LOC / 2 symbols).
- **C4 Heart-rate range bar** — first domain split: SalusChart's native pill <code>RangeBarChart</code> in one call vs a transparent-base stacked-bar emulation in both competitors (MPAndroidChart again routes the pill shape through the custom renderer).
- **C5 SleepStage** — both competitors import **zero** library symbols and hand-draw a *partial* SleepStage chart (no time axis, no interaction); their smaller LOC measures "drawing it by hand," not "using the library."
- **C6 Activity rings** — SalusChart's <code>MiniActivityRings</code> is the cheapest cell in the study (13 LOC / 2 symbols); MPAndroidChart overlays donut pies, Vico hand-draws `Canvas` arcs.

<details>
<summary>Emulator screenshots</summary>

![C1 Heart-rate line](/benchmark/samsung-c1-line.png)
![C2 Steps bar (green pill bars + goal line)](/benchmark/samsung-c2-bar.png)
![C4 Heart-rate range bar (+ C5)](/benchmark/samsung-c4-rangebar.png)
![C6 Activity rings](/benchmark/samsung-c7-rings.png)

</details>

## Why the gap appears

1. **Visual fidelity is an effort axis, not just chart availability.** Matching the real Apple/Samsung look — rounded pill bars, dashed goal lines, stripped axis chrome — is a single parameter in SalusChart and Vico, but MPAndroidChart has no rounded-bar API and needs a 26–55 LOC custom `BarChartRenderer` subclass. The clean aesthetic actually *narrowed* the raw-LOC gap (Vico shrinks when it just omits an axis; SalusChart's `MinimalBarChart` grew a manual weekday row), but the native-coverage and concept-load gaps are unchanged.
2. **Baselines are close on LOC but not on concept load.** Where all three are native, user-LOC differs modestly, but API-symbol count diverges sharply (SalusChart ~2, MPAndroidChart ~6, Vico 10–19 per chart). Vico's reactive `CartesianChartModelProducer` + layer/provider/axis/formatter architecture is powerful but front-loads many distinct concepts even for a basic chart.
3. **Domain charts diverge categorically.** SalusChart renders <mark class="salus-highlight">activity rings, SleepStage charts, and range bars with a single native composable each</mark>. MPAndroidChart and Vico have no native type and must emulate (transparent-base stacks, overlaid donuts) or hand-draw with `Canvas` (`native = false` in all six competitor domain cells across the two studies).
4. **For SleepStage (and Vico's rings), the general-purpose libraries contribute literally nothing** — those cells import zero library symbols, so the developer writes the chart from scratch and the reported LOC measures hand-drawing, not library use.
5. **MPAndroidChart's Compose story is dated** — everything goes through `AndroidView` + imperative `apply { }` setup, and bars cannot be rounded without the custom renderer.

## Honesty and limitations

Nothing was hidden to flatter SalusChart. Where a competitor cannot draw a chart, it is recorded as `native = false` with the emulation technique named, and hand-drawn cells are reported with their real LOC and a literal `0` API-symbol count. The one shared helper a single library needs (MPAndroidChart's rounded-bar renderer) is reported separately rather than folded silently into another library's column.

- **LOC is style-sensitive**, which is why the harder-to-game API-symbol count and the boolean `native` are reported alongside it — interpret the three together, not raw LOC alone. Styling to a real design narrowed some LOC gaps while leaving the structural gaps (native coverage, concept load) intact.
- **Partial parity is stated, not hidden.** The competitor SleepStage charts omit time axes, interaction, and accessibility; reaching full parity would cost more code than reported.
- **Chart representativeness.** Six charts each, drawn from two sample apps; results may not generalize to chart types outside this scope (e.g. candlestick, geospatial).

## Methodology

The full methodology — measurement definitions, the exact `cloc` procedure, pinned library coordinates, the official examples each implementation follows, the emulation techniques used where `native = false`, and threats to validity — is documented with each study. Apple Health keeps its study artifacts under `study/` (`RESULTS.md`, `results.csv`, `lenses.csv`, `methodology.md`, `screenshots/`). Samsung Health keeps the same audit trail in its docs/root benchmark files (`RESULTS.md`, `results.csv`, `lenses.csv`, `api_complexity.md`, `methodology.md`, `docs/screenshots/`). The measurement is a deterministic shell loop over one-file-per-chart implementations, so re-running against the pinned versions reproduces the numbers.

```bash
# Per-file measurement (run per library/chart)
code=$(cloc --quiet --csv "$f" | awk -F, '$2=="Kotlin"{print $5}')
pkgimp=$(grep -cE '^[[:space:]]*(package|import)[[:space:]]' "$f")
echo "user_loc=$((code - pkgimp))"
```

Versions were resolved live and pinned in each study's `app/build.gradle.kts`; build environment was AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2024.09.00, JDK 17, compileSdk 36.
