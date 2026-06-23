---
layout: home

hero:
  name: SalusChart
  text: Mobile health visualization components for Jetpack Compose
  tagline: Normalize health records, aggregate them into time-indexed data, and render consistent charts for mobile and wearable health apps.
  actions:
    - theme: brand
      text: Get Started
      link: /guide/getting-started
    - theme: alt
      text: View Charts
      link: /charts/
    - theme: alt
      text: Emulator Screens
      link: /examples/health-emulator-screens
    - theme: alt
      text: GitHub
      link: https://github.com/HDIL-YS/SalusChart

features:
  - title: Emulator Screen Checks
    details: Samsung Health-style, Apple Health-style, and Wear OS screenshots across phone, foldable, and round watch emulators.
    link: /examples/health-emulator-screens
    linkText: View screenshots
  - title: Health-specific Components
    details: Reusable charts for common mHealth patterns such as activity rings, sleep stages, heart-rate ranges, goal progress, and health dashboards.
  - title: Health Data Pipeline
    details: Map Apple Health, Samsung Health, Wear OS, Health Connect, or backend records into shared models, then normalize them into chart-ready marks.
  - title: Health-first Data Models
    details: Built-in models for sleep sessions, heart rate ranges, blood pressure, activity rings, and more.
  - title: Sensible Defaults
    details: Charts work with concise inputs first, while reference lines, paging, styling, and advanced layout controls stay opt-in.
  - title: 20+ Chart Types
    details: Bar, line, range bar, scatter, pie, progress rings, sleep stage, gauge, calendar, stacked bar, and compact minimal variants.
  - title: Data Transform
    details: Aggregate records by minute, hour, day, week, month, or year with totals, daily averages, duration sums, and min/max ranges.
  - title: Paging & Scrolling
    details: Every time-series chart supports static, horizontally scrollable, and paged display modes out of the box.
  - title: Wear OS Support
    details: A separate ui:wear-compose module provides glanceable variants for small round watch faces.
  - title: Modular
    details: Pick only the modules you need. Add ui:compose for charts, data:model for health types, core:transform for aggregation.
  - title: Maven Central
    details: Published to Maven Central with shared module versions and release notes.
---

## Emulator screen checks

Samsung Health-style, Apple Health-style, and Wear OS screens were checked across representative phone, foldable, and round watch emulators.

<div class="home-screen-preview">
  <a class="home-screen-preview-card" href="./examples/health-emulator-screens.html">
    <span>Samsung Health-style</span>
    <strong>Dashboard and detail views across 3 emulator sizes</strong>
    <img src="/examples/health-emulator-screens/samsung-pixel9profold-dashboard.png" alt="Samsung Health-style foldable dashboard emulator screenshot" />
  </a>
  <a class="home-screen-preview-card" href="./examples/health-emulator-screens.html">
    <span>Apple Health-style</span>
    <strong>Dashboard and detail views across 3 emulator sizes</strong>
    <img src="/examples/health-emulator-screens/apple-pixel9profold-dashboard.png" alt="Apple Health-style foldable dashboard emulator screenshot" />
  </a>
  <a class="home-screen-preview-card" href="./examples/health-emulator-screens.html">
    <span>Wear OS</span>
    <strong>Activity rings, SleepStage, and heart rate on round screens</strong>
    <img src="/examples/health-emulator-screens/wear-xl-round-activity-rings.png" alt="Wear OS XL Round activity rings emulator screenshot" />
  </a>
</div>

[View all emulator screenshots](./examples/health-emulator-screens)
