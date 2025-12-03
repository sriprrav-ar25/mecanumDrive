package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.Spindexer;
import org.firstinspires.ftc.teamcode.mechanisms.Webcam;

@TeleOp(name = "MainTeleOp", group = "Main")
public class MainTeleOp extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    private Spindexer spindexer = new Spindexer();
    private Webcam webcam = new Webcam();

    @Override
    public void runOpMode() throws InterruptedException {


        drive.init(hardwareMap);
        spindexer.init(hardwareMap);
        webcam.init(hardwareMap, telemetry);

        telemetry.addLine("TeleOp Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double forward = -gamepad1.left_stick_y;
            double strafe  = gamepad1.left_stick_x;
            double rotate  = gamepad1.right_stick_x;

            drive.driveFieldRelative(forward, strafe, rotate);

            if (gamepad1.x || gamepad1.y) {
                spindexer.rotateCounterClockwise();
            }

            if (gamepad1.a || gamepad1.b) {
                spindexer.launchClockwise();
            }

            webcam.updateDetection();

            telemetry.addLine("===== Webcam =====");
            telemetry.addData("AprilTag", webcam.lastTagID);
            telemetry.addData("Color Combo", webcam.colorCombo);
            telemetry.addLine();

            telemetry.addLine("===== Spindexer =====");
            telemetry.addData("Index", spindexer.getIndex());
            telemetry.addData("Current Position", spindexer.getPosition());

            telemetry.update();
        }
    }
}
