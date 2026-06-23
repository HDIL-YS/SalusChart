# Apple Health Sample

The Apple Health sample shows how to connect platform health data to SalusChart-style chart models.

Repository: [SalusChart-AppleHealth-Sample](https://github.com/HDIL-YS/SalusChart-AppleHealth-Sample)

Use the sample to inspect:

- platform permission flow
- Apple Health data loading
- mapping platform records into chart-ready data
- dashboard-style chart composition

## Benchmark coverage

The sample is also used by the [Developer Effort Study](../guide/benchmark) as an Apple-Health-style replication benchmark. It compares six charts implemented with SalusChart, MPAndroidChart, and Vico:

| Chart | SalusChart result | Competitor result |
|---|---|---|
| Activity rings | native <code>MiniActivityRings</code> | MPAndroidChart emulates with stacked donut pies; Vico hand-draws Canvas arcs |
| Daily steps bar | native minimal rounded bar chart | all native, but MPAndroidChart needs a shared rounded-bar renderer |
| Bar + average line | native bar chart + <code>ReferenceLineSpec</code> | all native, with more setup/API surface in MPAndroidChart and Vico |
| Line + points + axes | native line chart with point flag | all native, Vico needs deeper line/provider/point composition |
| SleepStage | native <code>SleepStageChart</code> | MPAndroidChart and Vico hand-draw with Compose `Canvas` |
| Range / floating bar | native <code>RangeBarChart</code> | MPAndroidChart and Vico emulate with transparent-base stacked bars |

Measured totals: <mark class="salus-highlight">183 LOC / 24 API symbols / 6 of 6 native</mark> for SalusChart, versus **248 LOC / 30 symbols / 3 of 6 native** for MPAndroidChart and **238 LOC / 49 symbols / 3 of 6 native** for Vico. MPAndroidChart also needs a separate **55-LOC** rounded-bar renderer to match the Apple-style rounded bars.

For the general integration architecture, see [Platform Integrations](../guide/platform-integrations).
