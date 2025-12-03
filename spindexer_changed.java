package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="SpindexerControl", group="Mechanisms")
public class SpindexerControl extends LinearOpMode {

    private DcMotor discControl;

    // Encoder constants
    private static final int TICKS_PER_REV = 1440;        // full rotation
    private static final int TICKS_PER_INDEX = TICKS_PER_REV / 3;   // three positions

    private int currentIndex = 0;   // slot 0, 1, or 2

    @Override
    public void runOpMode() {

        discControl = hardwareMap.get(DcMotor.class, "discControl");

        discControl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        discControl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        discControl.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addLine("Spindexer Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.x || gamepad1.y) {
                currentIndex--;
                if (currentIndex < 0) currentIndex = 2;

                int targetPos = currentIndex * TICKS_PER_INDEX;
                discControl.setTargetPosition(targetPos);
                discControl.setPower(0.6);
            }

            if (gamepad1.a || gamepad1.b) {
                currentIndex++;
                if (currentIndex > 2) currentIndex = 0;

                int targetPos = currentIndex * TICKS_PER_INDEX;
                discControl.setTargetPosition(targetPos);
                discControl.setPower(-0.8);
            }

            telemetry.addData("Index", currentIndex);
            telemetry.addData("Target Pos", discControl.getTargetPosition());
            telemetry.addData("Current Encoder", discControl.getCurrentPosition());
            telemetry.update();
        }
    }
}
