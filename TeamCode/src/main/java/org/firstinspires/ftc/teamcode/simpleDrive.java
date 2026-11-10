
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

//We use this to limit the power levels on specific motors, such as the drive ones.
//The Range function limits a specific value to a top limit and a lower limit.
//As an example, if we input a top limit of 1, any value below 1 stays the same whereas any number above 1 gets shrunk back down into 1.
import com.qualcomm.robotcore.util.Range;

//This is the interface that declares the code below as TeleOp.
//Interfaces use @ to start and do not require ; to end them off. We don't use interfaces too often.
@TeleOp

//Within this function is the rest of our code that is not imports or packages.
//The "meat of the steak" if you will, everything above is just the "spices" that make it work (or for steaks, taste good.)
//99% of your coding will occur within this function.
public class simpleDrive extends OpMode {

    //Declares that there is a DcMotor called leftFrontDrive.
    //The "= null" is present due to the fact that we declared this "variable" to be a DcMotor, which does not accept other variable types.
    private DcMotor leftFrontDrive = null;

    //Extrapolate from above comment.
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;


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


        // Adds telemetry data to inform the driver of the robots status as Initialized.
        telemetry.addData("status", "Initialized");
    }

    // Once more, @Override interface to make this play over ANY other process the bot may currently be running.
    @Override
    // Starts the teleop loop, which runs hundreds of times a second. Most of the time, the code you modify will be in here.
    public void loop() {
        // Initializing the motors used shortly for driving.
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

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



        // Declaring and setting more drive variables. Since this is within the loop, these variables are constantly changing.
        // See how we set the Y stick to negative? Look back at that note from earlier.
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        // Here is the main drive chunk.
        // A LOT of stuff to break down here, so let's go piece by piece.
        // We're starting with "x =" where 'x' is a predeclared variable. So, we're setting that variable to the number after the '='.
        // Then, "Range.clip". Range is a function where we take a number and make sure it doesn't exceed a given maximum or minimum.
        // Number is "drive +/- turn +/- strafe". Those are other variables, set as the joystick inputs from earlier.

        // Minimum is -1 and the maximum is 1, so let r = range; {r | r >= -1, r =< 1}. You'll get that in grade 10. Maybe.
        // Basically, any number between -1 and 1, or a double.

        // Here, the number given is \/ after this.     The minimum \/       \/ and the maximum are after their respective labels.
        leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
        rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
        leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
        rightBackPower = Range.clip(drive - turn + strafe, -1, 1);
        // Next, a few if statements.
        // This one is a great example in turning a double into a boolean like mentioned earlier.
        // We check to see if the right trigger is more than halfway pressed. If it is, the code within is played.
        if(gamepad1.right_trigger > 0.5){
            // Of which, the code within just divides all the power variables by 2.
            //In summary, if right trigger is pressed, half the power.
            leftFrontPower /= 2;
            leftBackPower /= 2;
            rightFrontPower /= 2;
            rightBackPower /= 2;
        } else if(gamepad1.left_trigger > 0.5){
            leftFrontPower /= 4;
            leftBackPower /= 4;
            rightFrontPower /= 4;
            rightBackPower /= 4;
        }




        //setting final power levels
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);



        // telemetry
        telemetry.addData("left front motor expected", "power = %.2f", leftFrontPower);
        telemetry.addData("right front motor expected", "power = %.2f", rightFrontPower);
        telemetry.addData("left rear motor expected", "power = %.2f", leftBackPower);
        telemetry.addData("right rear motor expected", "power = %.2f", rightBackPower);
    }


    @Override
    public void stop() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);


    }

}

