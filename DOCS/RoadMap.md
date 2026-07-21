# Kitti Oppressor Mk1 Roadmap

This roadmap outlines the planned development of **Kitti Oppressor Mk1**. The goal is not only to recreate the GTA Online Oppressor Mk I in GTA San Andreas, but to do so while respecting the game's engine and continually improving performance, realism, and code quality.

---

# Current Status

## Released
- [x] Version 0.1.2
- [x] Version 0.1.3

## In Development
- [ ] Version 0.1.4

---

# Phase 1 — Flight System

### Objective
Create a satisfying riding experience comparable to GTA Online's Oppressor Mk I.

### Tasks

- [x] Basic Boost
- [x] Basic Gliding
- [x] In-Air Turning
- [ ] Improve Boost feel
- [ ] Improve Glide physics
- [ ] Introduce momentum/inertia system
- [ ] Reduce unrealistic speed retention after collisions
- [ ] Fine tune pitch-based glide behaviour
- [ ] Improve landing transitions

---

# Phase 2 — Machine Gun

### Objective
Provide stable forward mounted machine guns.

### Tasks

- [x] Fire bullets
- [ ] Improve projectile origin
- [ ] Match aircraft firing behaviour
- [ ] Investigate native vehicle machine gun functions
- [ ] Replace scripted bullets with native implementation if possible

---

# Phase 3 — Simulated Missile System

### Objective
Implement a fully scripted missile system while researching GTA SA internals.

### Tasks

- [x] Dumbfire rockets
- [x] Scripted homing missile prototype
- [ ] Improve target acquisition
- [ ] Better target switching
- [ ] Reduce unnecessary calculations
- [ ] Improve missile flight realism
- [ ] Better obstacle detection
- [ ] Improve explosion logic

---

# Phase 4 — Native GTA Missile System

### Objective

Replace simulated behaviour with Rockstar's native missile system.

Instead of manually controlling missile movement every frame, use GTA San Andreas' internal projectile system for improved realism, compatibility and performance.

### Research Tasks

- [ ] Reverse engineer GTA SA missile system
- [ ] Understand projectile memory structures
- [ ] Understand CProjectileInfo
- [ ] Understand CVehicle missile functions
- [ ] Understand heat-seeking target evaluation
- [ ] Discover required memory addresses
- [ ] Test native function calls from CLEO
- [ ] Replace scripted missile movement

---

# Phase 5 — Lock-On System

### Objective

Achieve a lock-on experience similar to the original GTA weapon targeting system.

### Tasks

- [ ] Use player's aimed target
- [ ] Integrate GET_CHAR_PLAYER_IS_TARGETING
- [ ] Obtain target CEntity pointer
- [ ] Use native target evaluation
- [ ] Native lock-on sprite
- [ ] Native missile tracking
- [ ] Native target switching
- [ ] Improve lock consistency

---

# Phase 6 — Polish

### Tasks

- [ ] Better particle effects
- [ ] Improved audio
- [ ] Better balancing
- [ ] Multiplayer compatibility research
- [ ] Code cleanup
- [ ] Performance optimization
- [ ] Better documentation
- [ ] Additional configuration options

---

# Long-Term Vision

The final goal is to eliminate as much simulated behaviour as possible.

Whenever Rockstar already provides an internal implementation that is more efficient, reliable, or realistic, the scripted implementation will be replaced with native game functionality.

Current research focuses on:

- Native projectile system
- Heat-seeking missile system
- Vehicle weapon functions
- Memory structures
- Native function calling through CLEO
- Reverse engineering GTA San Andreas

This approach should result in:

- Better performance
- Lower CPU usage
- Less script complexity
- More accurate GTA-style behaviour
- Easier future expansion