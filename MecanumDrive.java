package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// AprilTag IDs on the field
private static final int COLOUR_TAG_ID = 1;
private static final int MOTIF_TAG_ID = 2;

// Output from detection
private ColourCombo detectedCombo = null;
private int detectedMotifID = -1;

// Keep your existing drive object EXACTLY as in MecanumDrive.java
MecanumDrive drive;

public class MecanumDrive {
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    private IMU imu;
    static double maxSpeed = 1.0; // change value for training

    public void init(HardwareMap hwMap) {
        frontLeftMotor = hwMap.get(DcMotor.class, "frontLeft"); // port #
        frontRightMotor = hwMap.get(DcMotor.class, "frontRight"); // port #
        backLeftMotor = hwMap.get(DcMotor.class, "backLeft"); // port #
        backRightMotor = hwMap.get(DcMotor.class, "backRight"); // port #

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot revOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);

        imu.initialize(new IMU.Parameters(revOrientation));
    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        double frontLeftPower = newForward + newStrafe + rotate;
        double backLeftPower = newForward - newStrafe + rotate;
        double frontRightPower = newForward - newStrafe - rotate;
        double backRightPower = newForward + newStrafe - rotate;

        double maxPower = Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
        );

        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            backLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backRightPower /= maxPower;
        }

        frontLeftMotor.setPower(maxSpeed * frontLeftPower);
        frontRightMotor.setPower(maxSpeed * frontRightPower);
        backLeftMotor.setPower(maxSpeed * backLeftPower);
        backRightMotor.setPower(maxSpeed * backRightPower);
    }

}
