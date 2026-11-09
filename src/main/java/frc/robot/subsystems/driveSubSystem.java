package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class driveSubSystem extends SubsystemBase {

    public final SparkMax right1 = new SparkMax(1, MotorType.kBrushless);
    public final SparkMax right2 = new SparkMax(2, MotorType.kBrushless);

    public final SparkMax left1 = new SparkMax(3, MotorType.kBrushless);
    public final SparkMax left2 = new SparkMax(4, MotorType.kBrushless);

    SparkClosedLoopController R1_pidController = right1.getClosedLoopController();
    SparkClosedLoopController R2_pidController = right2.getClosedLoopController();

    SparkClosedLoopController L1_pidController = left1.getClosedLoopController();
    SparkClosedLoopController L2_pidController = left2.getClosedLoopController();

    public void driveInit() {
        SparkMaxConfig config = new SparkMaxConfig();
        config.openLoopRampRate(0);   // Time ramp for open-loop commands
        config.closedLoopRampRate(0); // Time ramp for PID targets

        right1.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        right2.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        left1.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        left2.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

    }
    
    public void leftDriveRPM(double speed) {
        L1_pidController.setSetpoint(speed, ControlType.kVelocity);
        L2_pidController.setSetpoint(speed, ControlType.kVelocity);
    }

    public void rightDriveRPM(double speed) {
        R1_pidController.setSetpoint(speed, ControlType.kVelocity);
        R2_pidController.setSetpoint(speed, ControlType.kVelocity);
    }

    public void leftDrivePercent(double speed) {
        left1.set(speed);
        left2.set(speed);
    }

    public void rightDrivePercent(double speed) {
        right1.set(speed);
        right2.set(speed);
    }

}
