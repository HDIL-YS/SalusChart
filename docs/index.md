---
layout: home

hero:
  name: SalusChart
  text: A data visualization library for mobile health apps
  tagline: Built with Jetpack Compose, SalusChart turns the chart patterns health apps reach for again and again into reusable components — so you build views from domain records instead of from generic primitives.
  image:
    src: /home-hero.png
    alt: SalusChart Apple Health activity ring and Samsung Health sleep stage charts
  actions:
    - theme: brand
      text: Get Started
      link: /guide/getting-started
    - theme: alt
      text: Design Principles
      link: /guide/design-principles
    - theme: alt
      text: Browse Charts
      link: /charts/
    - theme: alt
      text: GitHub
      link: https://github.com/HDIL-YS/SalusChart

features:
  - title: Time as a first-class citizen
    details: Single readings, ranges, durations, and multi-attribute samples are normalized into one time-indexed dataset (TemporalDataSet). Charts page and scroll through time while their axes stay stable.
  - title: Two levels of detail
    details: Every full chart has a compact counterpart — MinimalBarChart, MiniActivityRings, and more — so a dashboard card and the detail view it expands into share the same data and rendering core.
  - title: Health-aware annotations
    details: Normal ranges, goals, averages, and trend lines are first-class reference lines rather than hand-drawn overlays, and they stay anchored to their data as the chart pages.
  - title: Defaults first, overrides when needed
    details: A chart renders from just a modifier and data. Colors, axis formatting, paging, and in-chart annotations are opt-in arguments on the same call — no restructuring to go from a card to a tuned detail view.
    link: /guide/customization
    linkText: See customization
  - title: Charts taken from real health apps
    details: The catalog was drawn from surveying Samsung Health, Apple Health, Google Fit, and Fitbit — activity rings, sleep-stage timelines, heart-rate range bars, progress gauges, and calendar heatmaps that general-purpose libraries don't ship.
    link: /charts/
    linkText: Chart reference
  - title: From phone to Wear OS
    details: The data:model and core:transform layers carry no Android-UI dependency, so the ui:wear-compose module reuses the same health models, aggregation, and chart marks to render glanceable views on round watch faces.
    link: /charts/wear-os-charts
    linkText: Wear OS charts
---

## Built for mobile health, not adapted to it

General-purpose chart libraries plot numbers well, but a health app still has to model its own records, group them over time, and rebuild patterns like activity rings or sleep timelines from low-level drawing primitives. SalusChart starts a layer earlier. It studies how apps such as Samsung Health, Apple Health, Google Fit, and Fitbit actually present data, then packages those recurring patterns into reusable Compose components fed by one record-to-chart pipeline shared across dashboards, detail views, and watch faces.

The six points above are the visible side of four design goals for mobile health visualization — treating time as first-class, carrying summary and detail in one framework, making health annotations native, and pairing sensible defaults with full overrides.

[Read the design principles](./guide/design-principles)

## The chart catalog

Health-shaped charts ready to drop into a screen — from activity rings and sleep timelines to floating range bars and calendar heatmaps. Each tile links to its reference page.

<div class="home-chart-gallery">
  <a href="./charts/progress-chart.html"><img src="/charts/progress-chart-rings.png" alt="Activity rings progress chart" /><span>Activity rings</span></a>
  <a href="./charts/sleep-stage-chart.html"><img src="/charts/sleep-stage-chart.png" alt="Sleep stage timeline chart" /><span>Sleep stages</span></a>
  <a href="./charts/range-bar-chart.html"><img src="/charts/range-bar-chart-basic.png" alt="Heart-rate range bar chart" /><span>Heart-rate range</span></a>
  <a href="./charts/bar-chart.html"><img src="/charts/bar-chart-basic.png" alt="Daily steps bar chart" /><span>Daily steps</span></a>
  <a href="./charts/line-chart.html"><img src="/charts/line-chart-basic.png" alt="Weight trend line chart" /><span>Weight trend</span></a>
  <a href="./charts/calendar-chart.html"><img src="/charts/calendar-chart-bubble.png" alt="Calendar heatmap chart" /><span>Calendar heatmap</span></a>
  <a href="./charts/pie-chart.html"><img src="/charts/pie-chart-full.png" alt="Nutrition pie chart" /><span>Nutrition breakdown</span></a>
  <a href="./charts/gauge-charts.html"><img src="/charts/multi-segment-gauge.png" alt="Multi-segment health gauge" /><span>Health gauge</span></a>
</div>

[Browse all charts](./charts/)

## From a few inputs to a tuned view

The same `RangeBarChart`: on the left, called with only its required inputs; on the right, with optional parameters — a custom color, time-formatted axis labels, a y-axis highlight, and a shaded sleep-goal zone. Optional parameters refine the chart without restructuring the call.

<div class="home-figure">
  <img src="/charts/minimal-to-customized.png" alt="The same RangeBarChart rendered from minimal inputs on the left and with optional styling parameters on the right" />
</div>

[See how customization works](./guide/customization)

## Less code, fewer concepts

We rebuilt the charts of an Apple-Health-style and a Samsung-Health-style screen three times each — once in SalusChart, once in MPAndroidChart, once in Vico — and styled each to match the real app. Across all 12 target charts, SalusChart was the only library with a native chart type for every one, while writing about 20% fewer lines of code and 24–58% fewer distinct API symbols than the two general-purpose libraries.

[See the library comparison](./library-comparison)

## Checked on real device profiles

The charts were rendered on four emulator profiles — a compact phone (Pixel 9), a large phone (Pixel 10 Pro XL), a foldable (Pixel 9 Pro Fold), and a round Wear OS watch, all on API 36. The same chart composables reflow across the phone form factors, while the dedicated Wear OS components handle the round watch face.

<div class="home-screen-preview">
  <a class="home-screen-preview-card" href="./emulator-screenshots">
    <span>Samsung Health-style</span>
    <strong>Dashboard and detail views across three emulator sizes</strong>
    <img src="/examples/health-emulator-screens/samsung-pixel9profold-dashboard.png" alt="Samsung Health-style foldable dashboard emulator screenshot" />
  </a>
  <a class="home-screen-preview-card" href="./emulator-screenshots">
    <span>Apple Health-style</span>
    <strong>Dashboard and detail views across three emulator sizes</strong>
    <img src="/examples/health-emulator-screens/apple-pixel9profold-dashboard.png" alt="Apple Health-style foldable dashboard emulator screenshot" />
  </a>
  <a class="home-screen-preview-card" href="./emulator-screenshots">
    <span>Wear OS</span>
    <strong>Activity rings, sleep stages, and heart rate on round screens</strong>
    <img src="/examples/health-emulator-screens/wear-xl-round-activity-rings.png" alt="Wear OS XL Round activity rings emulator screenshot" />
  </a>
</div>

[View all emulator screenshots](./emulator-screenshots)
