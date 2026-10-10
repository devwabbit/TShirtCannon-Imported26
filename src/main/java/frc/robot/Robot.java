// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.


// Rev Blinkin Color Codes
// https://1166281274-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2F-ME3KPEhFI6-MDoP9nZD%2Fuploads%2FMOYJvZmWgxCVKJhcV5fn%2FREV-11-1105-LED-Patterns.pdf?alt=media&token=e8227890-6dd3-498d-834a-752fa43413fe

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  // private Command m_autonomousCommand;

  private final RobotContainer robotContainer;
  // public final driveSubSystem driveSystem = new driveSubSystem();
  // public final XboxController controller = new XboxController(0);

  public Robot() {
    robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    robotContainer.updateSmartDashboard();
    
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    // m_autonomousCommand = robotContainer.getAutonomousCommand();

    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.schedule();
    // }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.cancel();
    // }
    robotContainer.antiDrift();

    
    

  }

  @Override
  public void teleopPeriodic() {
    // double leftStick = controller.getLeftY();
    // double rightStick = controller.getRightY();
    // driveSystem.leftDrive(leftStick);
    // driveSystem.rightDrive(-rightStick);
    // SmartDashboard.putNumber(" leftStick ", leftStick);
    // SmartDashboard.putNumber(" rightStick ", rightStick);
  }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
