package org.usfirst.frc.team4915.stronghold.commands;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team4915.stronghold.Robot;
import org.usfirst.frc.team4915.stronghold.subsystems.DriveTrain;

import java.util.ArrayList;
import java.util.List;

public class MoveStraightPositionModeCommand extends Command {

    public static List<CANTalon> motors = Robot.driveTrain.motors;
    public double inputDistance;
    public double inputSpeed;
    private DriveTrain driveTrain = Robot.driveTrain;
    private List<Double> desiredTicksValue;
    private double driveStraightValue = 0.7;

    public MoveStraightPositionModeCommand(double inputDistance, double inputSpeed) {

        requires(this.driveTrain);

        System.out.println("***MoveStraightPositionModeCommand inputDistance: " + inputDistance + "*******");
        System.out.println("***MoveStraightPositionModeCommand inputSpeed: " + inputSpeed + "*******");

        this.inputDistance = inputDistance;
        this.inputSpeed = inputSpeed;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time

    /**
     * This initializes the variables for the distance calculator.
     */
    @Override
    protected void initialize() {
        this.desiredTicksValue = new ArrayList<Double>();

        double ticksToMove = this.inputDistance * 12 * 1000 / (14 * Math.PI);

        for (int i = 0; i < motors.size(); i++) {
            CANTalon motor = motors.get(i);

            double startingTickValue = motor.getPosition();
            double endValue = startingTickValue + ticksToMove;
            if (i >= 2) {
                // right motors are inverted
                endValue = startingTickValue - ticksToMove;
            }
            System.out.println("!!!!!!!!!" + this.inputSpeed + "!!!!!!!!!!!");

            this.desiredTicksValue.add(endValue);
        }
    }

    /**
     * This uses the wheel circumference and the number of rotations to compute
     * the distance traveled.
     */

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        System.out.println("+++++++++" + this.inputSpeed + "++++++++");
        if (this.inputDistance < 0) {
            this.driveTrain.driveStraight(this.driveStraightValue);
        } else {
            this.driveTrain.driveStraight(-this.driveStraightValue);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        // checking to see if the front motors have finished regardless of
        // driving direction
        if (this.inputDistance > 0) {
            return isMotorFinished(0) || isMotorFinished(2);
        } else {
            return isMotorFinished(1) || isMotorFinished(3);
        }
    }

    private boolean isMotorFinished(int i) {
        boolean finished = false;
        double currentPosition = motors.get(i).getPosition();

        double desiredPosition = this.desiredTicksValue.get(i);
        System.out.println("Motor " + i + ": current position: " + currentPosition + ", desired position " + desiredPosition);

        if (i >= 2) {
            // right motors are inverted
            if (this.inputDistance < 0) {
                finished = currentPosition >= desiredPosition;
            } else {
                finished = currentPosition <= desiredPosition;
            }
        } else {
            if (this.inputDistance < 0) {
                finished = currentPosition <= desiredPosition;
            } else {
                finished = currentPosition >= desiredPosition;
            }
        }
        return finished;

    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        this.driveTrain.robotDrive.stopMotor();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        end();
    }
}
