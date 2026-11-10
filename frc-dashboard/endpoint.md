 # FRC T-Shirt Cannon Dashboard API Contract

This folder contains a lightweight dashboard for the robot that is designed to be mobile-friendly and fast-loading. It is intentionally simple and should be served directly from the robot web server on the roboRIO or a local FRC network endpoint.

## Goals

- Keep UI small and responsive for phones and laptops
- Show robot state and debug values without heavy libraries
- Support a minimal command API for cannon control and diagnostics
- Avoid touching the main robot project; this is a separate dashboard folder

## Base URL

Use the robot host as the base, usually:

- http://roboRIO-####-FRC.local:5800
- http://10.TE.AM.2:5800

## Endpoint 1: GET /status

Returns the robot telemetry that the dashboard needs to render.

### Response format

```json
{
  "timestamp": 1730000000,
  "robotMode": "teleop",
  "disabled": false,
  "batteryVoltage": 12.6,
  "driveEnabled": true,
  "safetyFire": true,
  "isFiring": false,
  "slowMode": false,
  "slowModeValue": 0.25,
  "ledState": 2,
  "debugEnabled": false,
  "fireDisabled": false,
  "forceFireActive": false,
  "leftStickY": 0.0,
  "rightStickY": 0.0,
  "controllerConnected": true,
  "cannonTilt": 0.0,
  "cannonRotation": 0.0
}
```

### Required fields for the dashboard

- robotMode: current robot state (`disabled`, `autonomous`, `teleop`, `test`)
- disabled: whether the robot is disabled
- batteryVoltage: main battery voltage
- driveEnabled: whether drivetrain is allowed to run
- safetyFire: whether the fire safety latch is active
- isFiring: whether the cannon solenoid is currently active
- slowMode: whether slow mode is enabled
- slowModeValue: percent reduction value for slow mode
- ledState: current LED mode (`0` to `4` is typical)
- debugEnabled: whether debug controls are visible / active
- fireDisabled: whether firing is intentionally blocked
- forceFireActive: whether forced-open fire state is active
- leftStickY / rightStickY: joystick values for debug display
- controllerConnected: whether the controller is present
- cannonTilt / cannonRotation: optional state for turret or cannon movement

## Endpoint 2: POST /command

Used to send actions from the dashboard to the robot.

### Request format

```json
{
  "action": "fire",
  "value": true
}
```

### Supported actions

#### `toggleSafety`

- Purpose: flip the safe-fire state
- Value: optional boolean or omitted
- Notes: used to lock or unlock firing

#### `toggleSlowMode`

- Purpose: switch low-speed drive mode on or off
- Value: optional boolean or omitted

#### `toggleDrive`

- Purpose: enable or disable drivetrain output
- Value: optional boolean or omitted

#### `setLedState`

- Purpose: set LED pattern or mode
- Value: integer from `0` to `4`
- Examples:
  - `0` = rainbow / default
  - `1` = red
  - `2` = green
  - `3` = blue
  - `4` = white

#### `setLedColor`

- Purpose: set a custom LED color using RGB values
- Value: object with `r`, `g`, and `b` numbers from `0` to `255`
- Example:

```json
{
  "action": "setLedColor",
  "value": {
    "r": 255,
    "g": 0,
    "b": 255
  }
}
```

#### `setMode`

- Purpose: force robot mode if needed
- Value: string such as `"disabled"`, `"teleop"`, `"autonomous"`

#### `setSlowModeValue`

- Purpose: update slow mode multiplier
- Value: number between `0.1` and `1.0`

#### `toggleDebug`

- Purpose: enable or disable debug data controls
- Value: optional boolean or omitted

### Response format

```json
{
  "ok": true,
  "message": "Command accepted"
}
```

## Suggested debug payloads

The UI may need any of these values for troubleshooting:

- leftStickY
- rightStickY
- batteryVoltage
- ledState
- fireDisabled
- forceFireActive
- safetyFire
- slowMode
- slowModeValue
- controllerConnected

## Recommended minimal implementation pattern

Keep the robot-side endpoint logic simple:

- Use a single `GET /status` for all telemetry
- Use a single `POST /command` for all actions
- Return only JSON; do not use heavy HTML pages for polling
- Keep the dashboard polling interval low, around `1` to `2` seconds

## Notes for the FRC robot

This dashboard is intentionally small and should not depend on React, Angular, or a large JS framework. A plain static HTML + CSS + JS page is best for roboRIO-like systems and keeps CPU and network usage low.
