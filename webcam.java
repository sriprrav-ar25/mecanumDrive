package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.List;

public enum ColourCombo {
    PPG, // Purple Purple Green
    PGP, // Purple Green Purple
    GPP  // Green Purple Purple
}

@Autonomous(name = "AprilTag Auto - MecanumDrive", group = "Auto")
public class AprilTagDrive extends LinearOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    // Your working drive system
    private MecanumDrive drive = new MecanumDrive();

    @Override
    public void runOpMode() throws InterruptedException {

        // STEP 1 — initialize drive system
        drive.init(hardwareMap);

        // STEP 2 — initialize camera + AprilTags
        initAprilTag();

        telemetry.addLine("Init complete — Press START");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            List<AprilTagDetection> detections = aprilTag.getDetections();
            telemetry.addData("Tags Detected:", detections.size());

            if (detections.size() > 0) {
                AprilTagDetection tag = detections.get(0);

                telemetry.addData("Tag ID:", tag.id);
                telemetry.addData("X (Right):", tag.ftcPose.x);
                telemetry.addData("Y (Forward):", tag.ftcPose.y);
                telemetry.addData("Yaw:", tag.ftcPose.yaw);

                double strafe = 0;
                double forward = 0;

                // Move left/right to center X = 0
                if (Math.abs(tag.ftcPose.x) > 1.0) { // inches
                    strafe = -Math.signum(tag.ftcPose.x) * 0.3;
                }

                // Move forward/backward to reach Y = desired distance
                double targetDistance = 12; // stop at 12 inches
                double errorY = tag.ftcPose.y - targetDistance;

                if (Math.abs(errorY) > 1.0) {
                    forward = Math.signum(errorY) * 0.3;
                }


                drive.driveFieldRelative(forward, strafe, 0);

            } else {
                // No tags — stay still
                drive.driveFieldRelative(0, 0, 0);
            }

            telemetry.update();
        }


        visionPortal.close();
    }

    private void initAprilTag() {

        aprilTag = new AprilTagProcessor.Builder().build();

        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag);

        visionPortal = builder.build();
    }

}
