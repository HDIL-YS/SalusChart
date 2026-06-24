# Samsung Health Sample

The Samsung Health sample shows how to connect Samsung Health data to SalusChart charts.

Repository: [SalusChart-SamsungHealth-Sample](https://github.com/HDIL-YS/SalusChart-SamsungHealth-Sample)

Use the sample to inspect:

- Samsung Health data access
- permission and data loading flow
- mapping platform records into `data:model`
- rendering transformed records with SalusChart charts

## Benchmark coverage

The sample is also used by the [Developer Effort Study](../benchmark) as a Samsung-Health-style replication benchmark. It compares six charts implemented with SalusChart, MPAndroidChart, and Vico:

| Chart | SalusChart result | Competitor result |
|---|---|---|
| Heart-rate line | native <code>LineChart</code> | all native, with larger model/setup surfaces in MPAndroidChart and Vico |
| Steps bar + goal line | native pill <code>BarChart</code> + reference line | all native, but MPAndroidChart needs a shared rounded-bar renderer |
| Nutrition stacked bar | native <code>StackedBarChart</code> with <code>showLegend = true</code> | all native; Vico needs the heaviest legend/layer setup |
| Heart-rate range bar | native <code>RangeBarChart</code> | MPAndroidChart and Vico emulate with transparent-base stacked bars |
| SleepStage | native <code>SleepStageChart</code> | MPAndroidChart and Vico hand-draw a partial Compose `Canvas` version |
| Activity rings | native <code>MiniActivityRings</code> | MPAndroidChart overlays donut pies; Vico hand-draws Canvas arcs |

Rollups are reported in two scopes. On the native-only common subset (C1-C3), SalusChart is <mark class="salus-highlight">85 LOC / 9 API symbols</mark>, versus **101 LOC / 19 symbols** for MPAndroidChart and **110 LOC / 44 symbols** for Vico. On the overall six-chart benchmark, SalusChart is <mark class="salus-highlight">160 LOC / 20 API symbols / 6 of 6 native</mark>, versus **183 LOC / 29 symbols / 3 of 6 native** for MPAndroidChart and **186 LOC / 56 symbols / 3 of 6 native** for Vico. Counting MPAndroidChart's shared rounded-bar renderer, its effective total is **209 LOC / 34 API symbols**.

For the general integration architecture, see [Platform Integrations](../guide/platform-integrations).
