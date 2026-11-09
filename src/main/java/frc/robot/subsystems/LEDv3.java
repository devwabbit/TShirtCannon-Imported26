package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDv3 extends SubsystemBase {
    public double ledState = 0;

    public AddressableLED m_Led = new AddressableLED(7);
    public AddressableLEDBuffer m_ledBuffer = new AddressableLEDBuffer(96);

    LEDPattern red = LEDPattern.solid(Color.kRed);
    LEDPattern green = LEDPattern.solid(Color.kGreen);
    LEDPattern blue = LEDPattern.solid(Color.kBlue);
    LEDPattern white = LEDPattern.solid(Color.kWhite);
    LEDPattern m_rainbow = LEDPattern.rainbow(255, 128);
    LEDPattern m_scrollingRainbow;

    public void LEDv3_INIT() {
        m_Led.setLength(m_ledBuffer.getLength());
        Distance kLedSpacing = Meters.of(1 / 120.0);
        m_scrollingRainbow = m_rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(1), kLedSpacing);
        
        m_Led.setData(m_ledBuffer);
        
        m_Led.start();
    }

    @Override
    public void periodic() {
        if (ledState == 1) {
            red.applyTo(m_ledBuffer);
            m_Led.setData(m_ledBuffer);
        }
        if (ledState == 2) {
            green.applyTo(m_ledBuffer);
            m_Led.setData(m_ledBuffer);
        }
        if (ledState == 3) {
            blue.applyTo(m_ledBuffer);
            m_Led.setData(m_ledBuffer);
        }
        if (ledState == 4) {
            white.applyTo(m_ledBuffer);
            m_Led.setData(m_ledBuffer);
        }
        if (ledState == 0 || ledState >= 5) {
            m_scrollingRainbow.applyTo(m_ledBuffer);
            m_Led.setData(m_ledBuffer);
        }
    }
}
