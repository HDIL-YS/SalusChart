# Design Principles

SalusChart is designed for mobile health visualization rather than general-purpose charting. The library packages common mHealth patterns into reusable Compose components so apps can present personal health data consistently across dashboards, detail views, and wearable screens.

## Health-specific components

Health apps repeatedly use the same visual patterns: daily goals, heart-rate ranges, sleep stages, calendar overviews, nutrition breakdowns, and compact trend summaries. SalusChart exposes these as direct chart components and mark types instead of requiring every app to rebuild them from lower-level drawing primitives.

## Separate data semantics from rendering

Application data starts as health-domain records such as steps, heart rate, blood pressure, sleep sessions, exercise, or meals. These records are normalized by `core:transform` into time-indexed data and then converted into reusable chart marks.

```text
platform records
    -> data:model health records
    -> TemporalDataSet
    -> ChartMark / RangeChartMark / ProgressChartMark
    -> ui:compose or ui:wear-compose
```

This keeps platform-specific data loading separate from chart rendering. A phone chart and a Wear OS glance view can reuse the same health models, aggregation logic, and chart marks.

## Sensible defaults, opt-in complexity

Charts should render useful output from concise inputs. Defaults cover common colors, axes, layout, and interaction behavior. More advanced controls such as reference lines, paging, custom tooltips, and detailed styling are available as opt-in parameters when a detail view needs them.

## Mobile and wearable constraints

Health visualizations often run on small screens and are used with coarse touch input. SalusChart includes expanded touch targets, tooltips, paging, scrolling, minimal chart variants, and Wear OS-specific components to support dense time-series data without forcing every screen to show the same level of detail.

## Modular packaging

Each layer is published separately so apps can depend only on what they need:

| Layer | Role |
|---|---|
| `data:model` | Shared health-domain records |
| `core:transform` | Time grouping, aggregation, and normalization |
| `core:chart` | Chart marks, math, and drawing primitives |
| `ui:compose` | Phone and tablet chart composables |
| `ui:wear-compose` | Wear OS chart composables |
| `ui:theme` | Shared chart color scheme |
