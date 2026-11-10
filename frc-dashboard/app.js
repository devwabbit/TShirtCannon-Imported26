const elements = {
  robotMode: document.getElementById('robotMode'),
  batteryValue: document.getElementById('batteryValue'),
  driveState: document.getElementById('driveState'),
  fireState: document.getElementById('fireState'),
  ledState: document.getElementById('ledState'),
  leftStick: document.getElementById('leftStick'),
  rightStick: document.getElementById('rightStick'),
  slowValue: document.getElementById('slowValue'),
  debugState: document.getElementById('debugState'),
  fireDisabledState: document.getElementById('fireDisabledState'),
  forceFireState: document.getElementById('forceFireState'),
  rgbR: document.getElementById('rgbR'),
  rgbG: document.getElementById('rgbG'),
  rgbB: document.getElementById('rgbB'),
  rgbPreview: document.getElementById('rgbPreview')
};

function formatBoolean(value) {
  return value ? 'ON' : 'OFF';
}

function syncRgbPreview() {
  const r = Number(elements.rgbR.value);
  const g = Number(elements.rgbG.value);
  const b = Number(elements.rgbB.value);
  elements.rgbPreview.style.background = `rgb(${r}, ${g}, ${b})`;
}

function updateStatus(data) {
  const mode = data.robotMode || 'UNKNOWN';
  const driveState = data.driveEnabled ? 'ENABLED' : 'DISABLED';
  const fireState = data.safetyFire ? 'ARMED' : 'READY';

  elements.robotMode.textContent = mode.toUpperCase();
  elements.batteryValue.textContent = data.batteryVoltage ? `${data.batteryVoltage.toFixed(2)} V` : '--';
  elements.driveState.textContent = driveState;
  elements.fireState.textContent = fireState;
  elements.ledState.textContent = data.ledState ?? '--';
  elements.leftStick.textContent = data.leftStickY != null ? data.leftStickY.toFixed(2) : '--';
  elements.rightStick.textContent = data.rightStickY != null ? data.rightStickY.toFixed(2) : '--';
  elements.slowValue.textContent = data.slowModeValue != null ? data.slowModeValue.toFixed(2) : '--';
  elements.debugState.textContent = formatBoolean(data.debugEnabled);
  elements.fireDisabledState.textContent = formatBoolean(data.fireDisabled);
  elements.forceFireState.textContent = formatBoolean(data.forceFireActive);

  if (data.ledColor) {
    const { r = 255, g = 0, b = 255 } = data.ledColor;
    elements.rgbR.value = r;
    elements.rgbG.value = g;
    elements.rgbB.value = b;
  }

  syncRgbPreview();
}

async function fetchStatus() {
  try {
    const response = await fetch('/status', { cache: 'no-store' });
    if (!response.ok) {
      throw new Error(`Status request failed: ${response.status}`);
    }
    const data = await response.json();
    updateStatus(data);
  } catch (error) {
    console.error(error);
    elements.robotMode.textContent = 'OFFLINE';
    elements.batteryValue.textContent = '--';
    elements.driveState.textContent = '--';
    elements.fireState.textContent = '--';
    elements.ledState.textContent = '--';
    elements.leftStick.textContent = '--';
    elements.rightStick.textContent = '--';
    elements.slowValue.textContent = '--';
    elements.debugState.textContent = '--';
    elements.fireDisabledState.textContent = '--';
    elements.forceFireState.textContent = '--';
  }
}

async function sendCommand(action, value) {
  try {
    const response = await fetch('/command', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action, value })
    });

    if (!response.ok) {
      throw new Error(`Command failed: ${response.status}`);
    }

    await fetchStatus();
  } catch (error) {
    console.error(error);
  }
}

document.querySelectorAll('[data-action]').forEach((button) => {
  button.addEventListener('click', () => {
    const action = button.dataset.action;

    if (action === 'toggleSafety') {
      sendCommand('toggleSafety');
    }

    if (action === 'toggleSlowMode') {
      sendCommand('toggleSlowMode');
    }

    if (action === 'toggleDrive') {
      sendCommand('toggleDrive');
    }
  });
});

document.querySelectorAll('[data-led]').forEach((button) => {
  button.addEventListener('click', () => {
    sendCommand('setLedState', Number(button.dataset.led));
  });
});

const rgbInputs = [elements.rgbR, elements.rgbG, elements.rgbB];
rgbInputs.forEach((input) => {
  input.addEventListener('input', syncRgbPreview);
});

document.getElementById('applyRgb').addEventListener('click', () => {
  const rgb = {
    r: Number(elements.rgbR.value),
    g: Number(elements.rgbG.value),
    b: Number(elements.rgbB.value)
  };

  sendCommand('setLedColor', rgb);
});

syncRgbPreview();
fetchStatus();
setInterval(fetchStatus, 2000);
