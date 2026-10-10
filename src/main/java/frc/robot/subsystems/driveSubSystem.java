package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class driveSubSystem extends SubsystemBase {

    public final SparkMax right1 = new SparkMax(1, MotorType.kBrushless);
    public final SparkMax right2 = new SparkMax(2, MotorType.kBrushless);

    public final SparkMax left1 = new SparkMax(3, MotorType.kBrushless);
    public final SparkMax left2 = new SparkMax(4, MotorType.kBrushless);
  

    public void driveInit() {
        SparkMaxConfig config = new SparkMaxConfig();
        config.openLoopRampRate(0);   // Time ramp for open-loop commands
        config.closedLoopRampRate(0); // Time ramp for PID targets

        right1.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        right2.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        left1.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        left2.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

    }

    public void leftDrive(double speed) {
        left1.set(speed);
        left2.set(speed);
        // System.out.println("L1 C:" + left1.getOutputCurrent() + "L1 S:"+ left1.getAbsoluteEncoder().getVelocity());
        // System.out.println("L2 C:" + left2.getOutputCurrent() + "L2 S:"+ left2.getAbsoluteEncoder().getVelocity());
    }

    public void rightDrive(double speed) {
        right1.set(speed);
        right2.set(speed);
        // System.out.println("R1 C:" + right1.getOutputCurrent() + "R1 S:"+ right1.getAbsoluteEncoder().getVelocity());
        // System.out.println("R2 C:" + right2.getOutputCurrent() + "R2 S:"+ right2.getAbsoluteEncoder().getVelocity());
        // System.out.println(speed);
        // right3.set(speed);
        // right4.set(speed);
    }
    // public void drive(double left, double right) {
    //     // Add logic to control motors based on left and right stick inputs
    //     System.out.println("Driving: Left=" + left + ", Right=" + right);
    // }
}
