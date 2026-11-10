
//Hello! This is a very simple drive program that *totally* isn't just copied over from last season, that *absolutely* wasn't a very, very customized premade tank drive.
//I'll be going through this on a mostly line-by-line basis unless it is a repeat of a line already seen before. -Adam
//The comment for any line will be above said line, never below.

//This line is used to group the folder together to avoid name conflicts and the like.
package org.firstinspires.ftc.teamcode;

//The four lines below this are where we import the functions we use for literally everything.
//You most likely will have to add more of these later on in the season.

//This one imports the OpMode, basically the whole loop and stop and everything else under the sun. It's in every program
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

//This one imports TeleOp, which is constant in TeleOp programs. In auto programs, it is not required.
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//If you couldn't guess from the last word in this line, this one gives you the functions for motors, such as "DcMotor.setPower(power)"
import com.qualcomm.robotcore.hardware.DcMotor;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

//This is the interface that declares the code below as TeleOp.
//Interfaces use @ to start and do not require ; to end them off. We don't use interfaces too often.
@TeleOp(name = "simpleDrivePedro")

//Within this function is the rest of our code that is not imports or packages.
//The "meat of the steak" if you will, everything above is just the "spices" that make it work (or for steaks, taste good.)
//99% of your coding will occur within this function.
public class simpleDrivePedro extends OpMode {

    //Declares that there is a DcMotor called leftFrontDrive.
    //The "= null" is present due to the fact that we declared this "variable" to be a DcMotor, which does not accept other variable types.
    private DcMotor leftFrontDrive = null;

    //Extrapolate from above comment.
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    public Follower follower;

    boolean blue = false;


    // The "@Override" interface stops all other functions once this one is called if added onto any.
    @Override

    // The "init" function is called when the "Initialize" button is pressed on the driver hub.
    // Within it, you put how you want motors to run, what their names are in the config, etc.
    // You'd put things such as setting and creating a variable in here (e.x. "double MyVariable = 1").
    // And you do that because otherwise when you first get that variable later on, it would be "null". Can you see the problem there?
    public void init() {

        // These are the name of the connected motors, sensors, cameras, servos, and everything else in the config in the driver hub.
        // All you need to know is that you need to match the name in the hub to the green text at the end of these lines EXACTLY.
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back");



        // Dictates whether the given motor runs using an encoder.
        // As a refresher, an encoder is an additional cable that supplies the hub with data on what the motor is doing.
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        // Dictates what direction a motor will spin.
        // IF YOU NEED TO SWAP A MOTORS DIRECTION, DO IT HERE, NOT ANYWHERE ELSE!!!!!!
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


        // Dictates if a motor will use the "Brake" function to slow down when not given any power. In general, recommended.
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        telemetry.addData("status", "Awaiting input...");

        follower = Constants.create(hardwareMap);
        while (!gamepad1.a && !gamepad1.b) {
            if (gamepad1.a) {
                follower.setPose(OpModeStorage.autonomousEndPose);
                follower.update();
            }
            telemetry.addLine("Press A to use ending pose from auto, B to use default start pose");
        }
        telemetry.clear();
        telemetry.addData("status", "Awaiting input...");
        while (!gamepad1.dpad_up && !gamepad1.dpad_down) {
            if (gamepad1.dpad_up) {
                blue = true;
            }
            if (gamepad1.dpad_down) {
                blue = false;
            }
            telemetry.addLine("D-Pad up if on blue, D-Pad Down if on Red");
        }
        telemetry.clear();


        // Adds telemetry data to inform the driver of the robots status as Initialized.
        telemetry.addData("status", "Initialized");
    }

    // Once more, @Override interface to make this play over ANY other process the bot may currently be running.
    @Override
    // Starts the teleop loop, which runs hundreds of times a second. Most of the time, the code you modify will be in here.
    public void loop() {

//  Quick reminder on controller layout.
//
//      left trigger        right trigger
//      left bumper         right bumper
//
//            up                 y
//      left dpad right       x     b
//           down                a
//
//           +y                 +y
//    -x joystickL +x    -x joystickR +x
//           -y                 -y
//
//  Triggers and joysticks are double (any number between -1 and 1).
//  Bumpers, buttons, D-Pad and joystick buttons (what happens when you click it in) are all boolean. (True/False).
//  You can make a double a boolean when needed, not as much the other way around (but it is possible, technically).
//
//  Excluding combinations, there are 6 double inputs and 12 boolean inputs. All transferred into boolean, that's 22 possible inputs.
//  However, that's including every single possible input, every joystick direction, and so on. Realistically, you have 8 boolean inputs.
//  That's excluding triggers, bumpers and joysticks. So? What's the big deal?
//  Well, that's at MAXIMUM. And if every function requested from drivers is mapped onto it using two buttons (one for on, one for off),
//  that's only 4 functions. With every input you could MAYBE get. Basically, try not to be too reliant on using >1 input per function.


        // Note: Joysticks are technically reversed, moving it left gives a right reading. Only really matters on the Y axis.

        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );
        follower.manual(powers);
        follower.update();

        Pose robotPose = follower.pose();


        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());
        telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));
    }


    @Override
    public void stop() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);


    }

}

