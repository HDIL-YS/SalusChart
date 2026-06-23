# Library Comparison

How SalusChart compares to two widely used Android chart libraries: **MPAndroidChart** and **Vico**.

SalusChart is a health-first Jetpack Compose library. MPAndroidChart and Vico are general-purpose charting libraries — they plot numbers well, but leave health-domain modeling, aggregation, and mHealth chart patterns to the app. This page lays out the differences factually so you can pick the right tool for the job.

For a quantitative measurement of how much code and how many distinct concepts each library takes to draw the same charts, see the [Developer Effort Study](./benchmark).

> Third-party facts (versions, maintenance status, star counts) were verified in **June 2026** and will drift over time. Verify current details against each project before adopting it.

## At a glance

| | **SalusChart** | **MPAndroidChart** | **Vico** |
|---|---|---|---|
| Focus | Mobile & wearable **health** visualization | General-purpose charting | General-purpose Cartesian charting |
| UI toolkit | Jetpack Compose only | Android Views | Compose **and** Android Views |
| Language | Kotlin | Java | Kotlin |
| Rendering | Compose `Canvas` | `View` + Canvas | Canvas (Compose / Views) |
| Latest version | `0.1.8` (pre-1.0) | `3.1.0` (Mar 2019) | `3.2.3` Android · `2.5.2` multiplatform (Jun 2026) |
| Maintenance | Active, early-stage | Stale (no release since 2019) | Active |
| Min SDK | 30 | 14 | 23 |
| Health data pipeline | **Built in** | None | None |
| Built-in time aggregation | **Yes** (minute → year) | No | No |
| Wear OS module | **Yes** (`ui:wear-compose`) | No | No (Glance widgets only) |
| Multiplatform (KMP) | No (Android only) | No | Yes |
| Distribution | Maven Central | JitPack | Maven Central |
| License | Apache 2.0 | Apache 2.0 | Apache 2.0 |
| GitHub stars | New project | ~38k | ~3k |

## Positioning and scope

**SalusChart** ships ready-made charts for recurring mHealth patterns — activity rings, sleep stages, heart-rate ranges, calendar heatmaps, goal progress, gauges, and compact dashboard summaries — plus the data models and aggregation that feed them. See [Design Principles](./design-principles).

**MPAndroidChart** and **Vico** are general plotting engines. They render any numeric series you give them, which makes them flexible across domains, but a health app must build its own data models, time grouping, units, reference ranges, and health-specific chart types on top.

If you need a generic line or bar chart in a non-health screen, a general-purpose library is the natural fit. If you are building a health dashboard, detail view, or wearable glance screen, SalusChart removes most of the domain plumbing.

## Platform and rendering

| | SalusChart | MPAndroidChart | Vico |
|---|---|---|---|
| Jetpack Compose | Native | Via `AndroidView` wrapper (community, not official) | Native |
| Android Views | No | Native | Native (`views` module) |
| Compose Multiplatform | No | No | Yes (`multiplatform` modules: Android, JVM, iOS/Native, Wasm, JS) |
| Min SDK | 30 | 14 | 23 |

SalusChart is Compose-only and targets a relatively recent **minSdk 30**, which is the main adoption trade-off versus the other two. MPAndroidChart's minSdk 14 reflects its age; its Java/View design predates Compose and requires an `AndroidView` wrapper to embed in modern Compose UIs. Vico is the most portable of the three: Compose, Views, and Compose Multiplatform from one codebase.

## Chart types

| Family | SalusChart | MPAndroidChart | Vico |
|---|---|---|---|
| Line | Yes | Yes | Yes |
| Bar / column | Yes | Yes | Yes |
| Horizontal bar | Yes | Yes | No |
| Stacked bar | Yes | Yes (grouped/stacked) | Via layered columns |
| Combined / dual-axis | Yes (`ComboChart`) | Yes (`CombinedChart`) | Yes (layered) |
| Scatter | Yes | Yes | No |
| Bubble | No | Yes | No |
| Pie / donut | Yes | Yes | Yes (since 3.1.0) |
| Radar / candlestick | No | Yes (both) | Candlestick only |
| Range bar | **Yes** | No | No |
| Progress rings / gauges | **Yes** | No | No |
| Sleep stage | **Yes** | No | No |
| Calendar heatmap | **Yes** | No | No |
| Compact "minimal" variants | **Yes** | No | No |

MPAndroidChart covers the widest set of *classic* chart types (radar, bubble, candlestick). Vico is Cartesian-first and recently added pie/donut. SalusChart trades some of that general breadth for **health-specific marks** — range bars, activity rings, sleep timelines, calendar overviews, gauges, and card-sized minimal charts — that the other two do not provide out of the box. See the [chart reference](../charts/) for the full list.

## Health and time-series fit

This is where the libraries differ most.

**SalusChart** treats health records as first-class input. Platform records map into shared health models, `core:transform` normalizes them into time-indexed data, and charts render the resulting marks:

```text
platform records -> data:model -> TemporalDataSet -> ChartMark -> chart composable
```

Aggregation by minute, hour, day, week, month, or year — with sums, averages, duration totals, and min/max ranges — is built in, along with guidance on when to fill gaps (steps) versus leave them sparse (weight, blood pressure). See [Data Transform](./data-transform) and [Known Limitations](./known-limitations#sparse-health-data).

**MPAndroidChart** and **Vico** use numeric x-axes with no native date/time axis. To show dates you store numeric x-values and supply a custom value formatter to convert them back to labels yourself. Both support real-time updates (MPAndroidChart via `notifyDataSetChanged()` + viewport moves; Vico via its transaction-based model producer), but neither offers health-domain aggregation, units, or reference ranges — you build that layer in the app.

## API style

**SalusChart** — declarative Compose; pass domain data straight to a composable:

```kotlin
BarChart(
    modifier = Modifier.fillMaxWidth().height(300.dp),
    data = listOf(
        ChartMark(x = 0.0, y = 4200.0, label = "Mon"),
        ChartMark(x = 1.0, y = 7800.0, label = "Tue"),
    ),
    title = "Step count",
    barColor = Color(0xFF7C4DFF),
)
```

**MPAndroidChart** — imperative; build `Entry` → `DataSet` → `ChartData`, then invalidate:

```kotlin
val entries = listOf(Entry(0f, 4200f), Entry(1f, 7800f))
val dataSet = LineDataSet(entries, "Steps")
chart.data = LineData(dataSet)
chart.invalidate()
```

**Vico** — declarative Compose, fed through a persistent model producer kept in a `ViewModel`:

```kotlin
val modelProducer = remember { CartesianChartModelProducer() }
LaunchedEffect(Unit) {
    modelProducer.runTransaction { lineSeries { series(4200, 7800) } }
}
CartesianChartHost(
    rememberCartesianChart(
        rememberLineCartesianLayer(),
        startAxis = VerticalAxis.rememberStart(),
        bottomAxis = HorizontalAxis.rememberBottom(),
    ),
    modelProducer,
)
```

SalusChart aims for the shortest path from health data to a rendered chart. MPAndroidChart's imperative API is verbose and predates Compose. Vico's model-producer pattern is powerful for streaming data but has a steeper learning curve and has changed its API across major versions.

## Wear OS

| | SalusChart | MPAndroidChart | Vico |
|---|---|---|---|
| Official Wear OS support | Yes — `ui:wear-compose` | No | No (Glance app-widget module only) |

SalusChart ships a dedicated Wear OS module with glanceable variants tuned for small round screens. Neither general-purpose library has a Wear-specific offering; they can technically render on a watch but are not optimized for it. See [Wear OS Charts](../charts/wear-os-charts).

## Accessibility

All three libraries render charts to a Canvas, so none expose chart marks as full semantic nodes for TalkBack out of the box:

- **SalusChart** — marks and selected values are not yet fully exposed as semantic nodes. Pair charts with accessible summary text. See [Known Limitations](./known-limitations#accessibility).
- **MPAndroidChart** — no accessibility support in any released version (a screen-reader PR remains open and unmerged).
- **Vico** — no built-in TalkBack/semantics support documented.

For all three, production health screens should add accessible summary text or selected-value labels alongside the chart.

## Maintenance and distribution

| | SalusChart | MPAndroidChart | Vico |
|---|---|---|---|
| Status | Active, early-stage (pre-1.0) | Stale — last release Mar 2019 | Active — regular releases |
| Distribution | Maven Central (`io.github.hdilys`) | JitPack (`com.github.PhilJay`) | Maven Central (`com.patrykandpatrick.vico`) |
| Maturity | Newest / smallest | Most established, largest user base | Mature, actively developed |

MPAndroidChart is the most battle-tested by install base but is effectively in maintenance limbo. Vico is actively maintained and the strongest general-purpose Compose option. SalusChart is the newest and least mature of the three, and the only one purpose-built for health.

## When to choose which

**Choose SalusChart when** you are building a mobile or wearable **health** app, you use Jetpack Compose, you can target minSdk 30, and you want ready-made health charts plus a record-to-chart data pipeline rather than building that domain layer yourself.

**Choose MPAndroidChart when** you maintain a View-based (XML) app, need chart types like radar, bubble, or candlestick, and can accept that the library is no longer actively developed.

**Choose Vico when** you want an actively maintained, general-purpose Compose (or multiplatform) charting library for non-health data, value Compose Multiplatform reach, and are comfortable building any domain modeling and time handling yourself.

## Adding each library

Concrete coordinates verified in June 2026. Confirm the latest version before adopting.

**SalusChart** — Maven Central:

```kotlin
// settings.gradle.kts → repositories { mavenCentral() }
implementation("io.github.hdilys:saluschart-ui-compose:0.1.8")
implementation("io.github.hdilys:saluschart-ui-theme:0.1.8")
implementation("io.github.hdilys:saluschart-data-model:0.1.8")
```

**MPAndroidChart** — JitPack (not published to Maven Central):

```kotlin
// settings.gradle.kts → repositories { maven { url = uri("https://jitpack.io") } }
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
```

**Vico** — Maven Central (group `com.patrykandpatrick.vico`):

```kotlin
// settings.gradle.kts → repositories { mavenCentral() }
implementation("com.patrykandpatrick.vico:compose:3.2.3")     // base Compose
implementation("com.patrykandpatrick.vico:compose-m3:3.2.3")  // Material 3 theming
implementation("com.patrykandpatrick.vico:views:3.2.3")       // optional, Android Views
// Compose Multiplatform flavor is versioned separately:
// implementation("com.patrykandpatrick.vico:multiplatform:2.5.2")
```

## Appendix: detailed library profiles

These are the full verified facts behind the comparison tables above, kept for reference. SalusChart facts come from this repository; MPAndroidChart and Vico facts were gathered from each project's GitHub repository, releases, and official documentation, **verified June 2026**.

### MPAndroidChart

| Field | Value |
|---|---|
| Repository | `PhilJay/MPAndroidChart` (author Philipp Jahoda) |
| Latest release | `v3.1.0`, published 2019-03-20 |
| Activity | ~38k stars, ~9k forks; not archived; ~2,000 open issues, ~120 open PRs; last push June 2025 (community PR, no new release) |
| Maintenance | Effectively stale — no release since 2019; README solicits help and donations |
| Tech | Android `View` + Canvas, Java 100%, AndroidX |
| SDK | minSdk 14; compile/target SDK 28 in the published artifact |
| Compose | No official support; community wraps it in `AndroidView` |
| Chart types | Line, Bar, HorizontalBar, Combined, Scatter, Bubble, Pie, Radar, CandleStick |
| API model | Imperative: `Entry` → typed `DataSet` → `ChartData` → `chart.data = …` → `invalidate()` |
| Features | X/Y animations with easing; pinch-zoom, fling, pan, viewport control; tap highlight (`OnChartValueSelectedListener`); `MarkerView` tooltips; `XAxis`/`YAxis` config; `Legend`; `ValueFormatter` |
| Time / health | No native date/time axis (use a custom `ValueFormatter`); realtime via `notifyDataSetChanged()` + viewport moves; no health-domain primitives |
| Wear OS | None official |
| Accessibility | None in any released version; screen-reader PR #4936 open and unmerged since 2020 |
| License | Apache 2.0 |
| Distribution | JitPack: `com.github.PhilJay:MPAndroidChart:v3.1.0` (no official Maven Central artifact) |
| Common complaints | Stale maintenance; no Compose; jank with large/high-frequency data; verbose Java API; no accessibility; no date axis; old toolchain defaults |

### Vico

| Field | Value |
|---|---|
| Repository | `patrykandpatrick/vico` (Patryk Goworowski & Patrick Michalik; sponsored by Software Mansion) |
| Latest release | `3.2.3` (Android/Compose family, Jun 2026); `2.5.2` (Compose Multiplatform family, Jun 2026) |
| Activity | ~3k stars; actively maintained (174 releases, frequent commits) |
| Maintenance | Active |
| Tech | Kotlin 100%; Jetpack Compose **and** Android Views; Compose Multiplatform |
| SDK | minSdk 23 |
| Modules | `core`, `compose`, `compose-m2`, `compose-m3`, `compose-glance` (Glance widgets), `views`, `multiplatform`, `multiplatform-m3` |
| KMP targets | Android, JVM (desktop), Kotlin/Native (incl. iOS), Wasm/JS |
| Chart types | Line, Column, Candlestick, Combined/layered; Pie/donut (since 3.1.0). No scatter, no radar |
| API model | Declarative: `rememberCartesianChart` of layers + axes inside `CartesianChartHost`, fed by a persistent `CartesianChartModelProducer` updated via `runTransaction` |
| Features | Animated transitions; scrolling (`VicoScrollState`); zoom (`VicoZoomState`); markers/tooltips incl. persistent markers; axis customization; Material 2 / Material 3 theming; transaction-based runtime updates |
| Time / health | General-purpose Cartesian; numeric x-axis with custom `valueFormatter`; realtime via transactions; no health-domain primitives |
| Wear OS | No official support; Glance app-widget module exists; watch use is community-only |
| Accessibility | No built-in TalkBack/semantics support documented |
| License | Apache 2.0 |
| Distribution | Maven Central, group `com.patrykandpatrick.vico` |
| Common complaints | API churn across major versions; complex docs; learning curve of the producer/transaction model; confusing dual (2.x / 3.x) version lines; limited non-Cartesian types |

### SalusChart

| Field | Value |
|---|---|
| Repository | `HDIL-YS/SalusChart` |
| Latest release | `0.1.8` (pre-1.0) |
| Tech | Kotlin; Jetpack Compose only, Canvas-rendered |
| SDK | minSdk 30; compileSdk 36 |
| Modules | `data:model`, `core:transform`, `core:chart`, `core:util`, `ui:compose`, `ui:theme`, `ui:wear-compose` |
| Chart types | 16 full + 10 minimal health composables (bar, line, scatter, combo, range bar, stacked bar, pie, progress rings, sleep stage, calendar heatmap, gauges, and compact variants) plus Wear OS variants |
| Health pipeline | Built-in health models + `core:transform` aggregation (minute → year; sum/avg/duration/min-max; gap-fill guidance) |
| Wear OS | Dedicated `ui:wear-compose` module |
| Accessibility | Canvas-rendered; marks/selected values not yet fully exposed as semantic nodes — pair with summary text |
| License | Apache 2.0 |
| Distribution | Maven Central, group `io.github.hdilys` |

## Sources and verification

SalusChart facts are taken from this repository. The third-party facts above were verified in **June 2026** against the following sources. Versions, maintenance status, and star counts change over time — re-confirm before adopting any library.

**MPAndroidChart**

- Repository, README, wiki: <https://github.com/PhilJay/MPAndroidChart>
- Release `v3.1.0` (2019-03-20): <https://github.com/PhilJay/MPAndroidChart/releases/tag/v3.1.0>
- Library build config (minSdk 14, SDK 28, Java/AndroidX): `MPChartLib/build.gradle` on the default branch
- License (Apache 2.0): `LICENSE` file in the repository
- Accessibility PR #4936 (open, unmerged): <https://github.com/PhilJay/MPAndroidChart/pull/4936>
- Distribution via JitPack: <https://jitpack.io/p/PhilJay/mpandroidchart>

**Vico**

- Repository: <https://github.com/patrykandpatrick/vico>
- Releases: <https://github.com/patrykandpatrick/vico/releases>
- Official guide: <https://guide.vico.patrykandpatrick.com/>
- Pie chart (since 3.1.0) and candlestick discussions: <https://github.com/patrykandpatrick/vico/discussions/426>, <https://github.com/patrykandpatrick/vico/discussions/427>
- KMP targets: <https://klibs.io/project/patrykandpatrick/vico>
- Maven Central (group `com.patrykandpatrick.vico`): <https://central.sonatype.com/namespace/com.patrykandpatrick.vico>
