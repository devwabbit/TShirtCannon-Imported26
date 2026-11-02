package frc.robot.subsystems;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.*;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDv3 extends SubsystemBase {
    public AddressableLED m_Led = new AddressableLED(7);
    public AddressableLEDBuffer m_ledBuffer = new AddressableLEDBuffer(96);

    public void LEDv3_INIT() {
        m_Led.setLength(m_ledBuffer.getLength());
        LEDPattern red = LEDPattern.solid(Color.kRed);
        LEDPattern m_rainbow = LEDPattern.rainbow(255, 128);
        Distance kLedSpacing = Meters.of(1 / 120.0);
        LEDPattern m_scrollingRainbow = m_rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(1), kLedSpacing);

        m_rainbow.applyTo(m_ledBuffer);
        
        m_Led.setData(m_ledBuffer);
        
        m_Led.start();
    }
}
