// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
// import frc.robot.subsystems.LEDv2;
import frc.robot.subsystems.driveSubSystem;
import frc.robot.subsystems.turrentSubSystem;

public class RobotContainer {
  public boolean slowmode = false;
  public double slowmodeval = 1;
  public boolean safetyFire = false;
  public boolean antiRunaway = false;
  XboxController controller = new XboxController(0);

  //Debug Values
  public boolean debug = false;
  public boolean disableDrive = false;
  public boolean disableFire = false;
  public boolean setCannonSystemOpen = false;
  

  public final driveSubSystem driveSystem = new driveSubSystem();
  public final turrentSubSystem turrentSubSystem = new turrentSubSystem();
  //public final ledControllerSubSystem ledSystem = new ledControllerSubSystem();
  // public final LEDv2 lightSystem = new LEDv2();
  
  public RobotContainer() {
    driveSystem.driveInit();
    configureBindings();
    // updateSmartDashboard();
  }

  private void configureBindings() {
    //DRIVE
    driveSystem.setDefaultCommand(
      Commands.run(() -> {
        double left = controller.getLeftY() * (!slowmode ? 1 : slowmodeval) * (!antiRunaway ? 1 : 0);
        double right = controller.getRightY() * (!slowmode ? 1 : slowmodeval) * (!antiRunaway ? 1 : 0);
        if (Math.abs(controller.getLeftY()) > .1) {
          driveSystem.leftDrive(left);
        } else {
          driveSystem.leftDrive(0);
        }
        if (Math.abs(controller.getRightY()) > .1) {
          driveSystem.rightDrive(right);
        } else {
          driveSystem.rightDrive(0);
        }
        
      }, driveSystem)
    );
    // .onFalse(
    //   Commands.run(() -> {

    //     driveSystem.rightDrive(0);
    //   })
    // );

    new Trigger(() -> controller.getStartButtonPressed()).whileTrue(Commands.runOnce(() -> {
      slowmode = !slowmode;
      // if (slowmode){
      //   slowmodeval = .5;
      // } else {
      //   slowmodeval = 1;
      // }
      System.out.println("slowmode: "+slowmode+" val: "+slowmodeval);
    }));

    //Turn Turrent Left & Right
    new Trigger(() -> controller.getRightBumperButton()).whileTrue(new InstantCommand(() -> 
    turrentSubSystem.turnTurrent(-.1)
    ));
    new Trigger(() -> controller.getLeftBumperButton()).whileTrue(new InstantCommand(() -> 
    turrentSubSystem.turnTurrent(.1)
    ));
    new Trigger(() -> controller.getRightBumperButtonReleased() || controller.getLeftBumperButtonReleased()).whileTrue(new InstantCommand(() -> 
    turrentSubSystem.turnTurrent(0)
    ));

    // Fire Cannon
    new Trigger(() -> controller.getLeftTriggerAxis() > .8 ).whileTrue(new InstantCommand(() -> {
      safetyFire = true;
      // lightSystem.setlightMode(-.93);
    }));
    new Trigger(() -> controller.getLeftTriggerAxis() < .8).whileTrue(new InstantCommand(() -> {
      safetyFire = false;
      // lightSystem.setlightMode(0.61);
    }));
    new Trigger(() -> controller.getRightTriggerAxis() > .1).whileTrue(new InstantCommand(() -> {
      if (safetyFire == true) {
        turrentSubSystem.fire();
      }
    }));
    
    
    // Bring Controller Up & Down
    new Trigger(() -> controller.getPOV()==180).whileTrue(new InstantCommand(() -> 
      turrentSubSystem.cannonGoUpDown(1)
    ));

    new Trigger(() -> controller.getPOV()==0).whileTrue(new InstantCommand(() -> 
    turrentSubSystem.cannonGoUpDown(-1)
    ));




    // new Trigger(() -> controller.getXButton()).whileTrue(new InstantCommand(() -> 
    // turrentSubSystem.cannonGoUpDown(1)
    // ));

    // new Trigger(() -> controller.getBButton()).whileTrue(new InstantCommand(() -> 
    // turrentSubSystem.cannonGoUpDown(-1)
    // ));

    new Trigger(() -> controller.getPOV()==-1).whileTrue(new InstantCommand(() -> 
    turrentSubSystem.cannonGoUpDown(0)
    ));
  }

  // public Command getAutonomousCommand() {
  //   return Commands.print("No autonomous command configured");
  // }


  public void updateSmartDashboard() {
    //Local Varibles to act as defaults
    double lightOverideVal = 0.61;
    boolean lightOverideBol = false;

    // Check Changes
    // slowmode = SmartDashboard.getBoolean("Slow Mode", slowmode);
    // safetyFire = SmartDashboard.getBoolean("Safety Fire", safetyFire);
    slowmodeval = SmartDashboard.getNumber(" Slow Mode Value ", slowmodeval);
    lightOverideVal = SmartDashboard.getNumber(" Overide Value ", lightOverideVal);
    lightOverideBol = SmartDashboard.getBoolean(" light OverRide ", lightOverideBol);
    // lightSystem.debug(lightOverideBol, lightOverideVal);

    // Set Data To Board
    SmartDashboard.putNumber(" leftStick ", controller.getLeftY());
    SmartDashboard.putNumber(" rightStick ", controller.getRightY());

    // SmartDashboard.putNumber(" Current Light Value ", lightSystem.lastSetLightVal);
    SmartDashboard.putBoolean(" Slow Mode ", slowmode);
    SmartDashboard.putBoolean(" Safety Fire ", safetyFire);
    SmartDashboard.putNumber(" Slow Mode Value ", slowmodeval);
    SmartDashboard.putBoolean(" light OverRide ", lightOverideBol);
    SmartDashboard.putNumber(" Overide Value ", lightOverideVal);
    // SmartDashboard.putNumber(" Turrent Rotations ", turrentSubSystem.returnRotation());

    debug = SmartDashboard.getBoolean(" Enable Debug ", debug);
    if (debug) {
      disableDrive = SmartDashboard.getBoolean(" Disable Drive ", disableDrive);
      antiRunaway = disableDrive;
      disableFire  = SmartDashboard.getBoolean(" Disable Cannon Fire ", disableFire);
      setCannonSystemOpen  = SmartDashboard.getBoolean(" Force Cannon Open  ", setCannonSystemOpen);
    } else {
      disableDrive = false;
      disableFire = false;
      setCannonSystemOpen = false;
    }
    SmartDashboard.putBoolean(" Enable Debug ", debug);
    SmartDashboard.putBoolean(" Debug ", debug);
    SmartDashboard.putBoolean(" Disable Drive ", disableDrive);
    SmartDashboard.putBoolean(" Disable Cannon Fire ", disableFire);
    SmartDashboard.putBoolean(" Force Cannon Open  ", setCannonSystemOpen);
    turrentSubSystem.disableFire(disableFire);
    turrentSubSystem.forceFire(setCannonSystemOpen);
  }

  public void antiDrift() {
    antiRunaway = false;
    if (Math.abs(controller.getLeftY()) > 0.05 || Math.abs(controller.getRightY()) > 0.05) {
      antiRunaway = true;
      System.err.println("Controller drift detected, replug in controller.");
      //DriverStation.
    }
  }
}
