package com.campusexchange.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Palette (Light Theme) ────────────────────────────────────────────────
val Primary       = Color(0xFF051F20)   // Deep forest — text, icons, primary actions
val Secondary     = Color(0xFF0B2B26)   // Deep teal — secondary text, borders
val Accent        = Color(0xFF235347)   // Forest green — accent elements
val Soft          = Color(0xFF8EB69B)   // Muted sage — secondary text, placeholders
val Background    = Color(0xFFDAF1DE)   // Mint wash — screen background
val SurfaceWhite  = Color(0xFFFFFFFF)   // Pure white — card surfaces
val SurfaceLight  = Color(0xFFF3FAF5)   // Near-white green tint — subtle surfaces

// ── Legacy aliases (kept for backward compat during migration) ─────────────────
val PrimaryDark      = Primary
val DeepGreen        = Secondary
val SecondaryDark    = Color(0xFF163832)
val AccentGreen      = Accent
val SoftGreen        = Soft
val LightBackground  = Background
val CardSurface      = SurfaceWhite
val CardSurface2     = SurfaceLight
val TextPrimary      = Primary
val TextSecondary    = Soft
val Divider          = Color(0xFFCDE8D3)   // Light mint divider

// ── Functional Colors ──────────────────────────────────────────────────────────
val NeonGreen     = Color(0xFF00875A)   // Groww-style deep green CTA (not neon on light bg)
val PositiveGreen = Color(0xFF00875A)   // Gain color
val NegativeRed   = Color(0xFFD32F2F)   // Loss color
val CoinGold      = Color(0xFFF59E0B)   // Amber gold for coins

// ── Chart Colors ───────────────────────────────────────────────────────────────
val ChartGreen    = Color(0xFF00875A)
val ChartRed      = Color(0xFFD32F2F)

// ── Utility ────────────────────────────────────────────────────────────────────
val Transparent   = Color(0x00000000)
