package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This is code that will work with either a Mecanum Drive or an X-Drive with holonomic wheels.
*/
@TeleOp(name="OmniDrive", group="Linear OpMode")
//@Disabled
public class CentralDiscControl extends LinearOpMode {

    // variables
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor discControl = null;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. 
        // Names are the same as on the Control Hub. (and are semi-arbetrary)
        discControl = hardwareMap.get(DcMotor.class, "discControl"); //expansion 0

        // reset the position of the disc, and set the proper motor function
        // this needs an encoder
        discControl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        discControl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        discControl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // ^ defines at 0 power what the motor should do
        discControl.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // 1440 ticks per revolution
        int motorSpeed = 1435;
        int rotations = 0;
        // to show which position of the three states the system is actually in
        int absPosition = 0;
        // GET THIS FROM THE APRIL TAG
        int[] posOrderCorrect = {0,0,1};
        // GET THIS FROM THE COLOR SENSOR (this shows what pattern is needed)
        int[] posOrderInitial = {1 ,0,0};
        // this will show the actual poisions (this shows what the sensors think)
        int[] posOrderCurrent = posOrderInitial;
        // 0 being green, 1 being purple        
        /*
        all the order arrays look like this
        1     2

           0
        */
       int[] lastShot = posOrderCorrect;
       // this will make sure the order of shots remains consistent
       int nextShot = lastShot[0];
       // this one is to make the rotation easier

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Run until the end of the match (Driver presses STOP)
        while (opModeIsActive()) {

            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

           // to figure out the absolute position of the disk
           absPosition = rotations;
            if absPosition > 2 { // to get it between 0-2 if it is > 2
                while absPosition > 2 {
                    absPosition -= 3;
                }
            } elif absPosition < 0 { // to get it between 0-2 if it is < 0
                whlie absPosition < 0 {
                    absPosition += 3;
                }
            }

            /*
                THIS IS FOR THE MANUAL MODE ON THE CENTRAL DISK
            */

            // rotate counterclockwise
            if (gamepad1.x || gamepad1.y){
                rotations -= 1;
                discControl.setTargetPosition(motorSpeed * (rotations/3));
            }

            // rotate clockwise
            if (gamepad1.b || gamepad1.a){
                rotations += 1;
                discControl.setTargetPosition(motorSpeed * (rotations/3));
            }

            /* FOR THE AUTOMATIC */

            // to make sure the actual positions of the balls are properly recorded
            if (rotations % 3 = 0){ // first position
                posOrderCurrent = posOrderInitial;
            } elif (rotations % 3 = 2){ // second position
                posOrderCurrent[0] = posOrderInitial[1];
                posOrderCurrent[1] = posOrderInitial[2];
                posOrderCurrent[2] = posOrderInitial[0];
            } elif (rotations % 3 = 1){ //third position
                posOrderCurrent[0] = posOrderInitial[2];
                posOrderCurrent[1] = posOrderInitial[0];
                posOrderCurrent[2] = posOrderInitial[1];
            }

            // track what the prvious shots were so it can stay organized
            if (lastShot[2] == posOrderCorrect[0]) { // 1 correct (shot 2)
            nextShot = posOrderCorrect[1];
            } elif (lastShot[1] == posOrderCorrect[0] && lastShot[2] == posOrderCorrect[1] ) { // 2 correct (shot 3)
            nextShot = posOrderCorrect[2];   
            } elif (lastShot[0] = posOrderCorrect[0] && lastShot[1] = posOrderCorrect[1] && lastShot[2] = posOrderCorrect[2]) { // 0 correct (shot 1)
            nextShot = posOrderCorrect[0];
            }

            // lets say 2 is the launch position
            // this will move the correct ball into the launch position
            if (posOrderCurrent[2] != nextShot){
                if (posOrderCurrent[0] == nextShot){
                    // rotate counterclockwise
                    rotations -= 1;
                    discControl.setTargetPosition(motorSpeed * (rotations/3));
                } elif (posOrderCurrent[1] == nextShot){
                    // rotate clockwise
                    rotations += 1;
                    discControl.setTargetPosition(motorSpeed * (rotations/3));
                }
            }

            // AFTER IT SHOOTS
            // cycle last shot one
            lastShot[0] = lastShot[1];
            lastShot[1] = lastShot[2];
            lastShot[2] = nextShot;

        }
    }

}
