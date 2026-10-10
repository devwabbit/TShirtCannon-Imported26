package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDv2 extends SubsystemBase {
    public final PWMSparkMax lightController = new PWMSparkMax(1);
    public boolean debug = false;
    public double lastSetLightVal = 0;
    public double defaultLightMode = .61;

    public LEDv2() {
        System.out.println("LED CONTROLLER V2 LOADED");
        lastSetLightVal = .61;
        lightController.set(.61);
    }

    public void setlightMode(double lightVal) {
        if (!debug) {
            lightController.set(lightVal);
            lastSetLightVal = lightVal;
            defaultLightMode = lightVal;
            System.out.println("Set Lights To: "+lightVal);
        }
    }

    public void debug(boolean bol, double lightVal) {
        if (bol == true) {
            //System.out.println("Debug Lights Set: "+lightVal);
            lastSetLightVal = lightVal;
            lightController.set(lightVal);
        } else {
            if (lastSetLightVal != defaultLightMode){
                lightController.set(defaultLightMode);
                lastSetLightVal = defaultLightMode;
            }
        }
    }
}
