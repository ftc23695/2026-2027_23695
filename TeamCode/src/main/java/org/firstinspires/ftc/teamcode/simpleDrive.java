package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;



import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class simpleDrive extends OpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor intakeForward = null;
    private DcMotor intakeBack = null;
    private DcMotorEx shooter = null;
    private Servo blocker = null;
    private Servo blockerFront = null;
    double shooterVelocity = 0;
    boolean isReversed = false;

    @Override
    public void init() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back");
        intakeForward = hardwareMap.get(DcMotor.class, "intakeForward");
        intakeBack = hardwareMap.get(DcMotor.class, "intakeBack");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        blockerFront = hardwareMap.get(Servo.class, "frontBlocker");
        blocker = hardwareMap.get(Servo.class,"blocker");

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        intakeForward.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter.setDirection(DcMotorEx.Direction.FORWARD);
        blockerFront.setDirection(Servo.Direction.FORWARD);
        blocker.setDirection(Servo.Direction.FORWARD);


        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeForward.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        telemetry.addData("status", "Initialized");
    }

    @Override
    public void loop() {
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;
        double intakeForwardSpeed = 0.0;
        double intakeBackSpeed = 0.0;

//
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

        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        if(gamepad1.bWasPressed()){
            isReversed=!isReversed;
        }
        if(isReversed){
            drive=-drive;
            strafe = -strafe;

        }
        // Here, the number given is \/ after this.     The minimum \/       \/ and the maximum are after their respective labels.
        leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
        rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
        leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
        rightBackPower = Range.clip(drive - turn + strafe, -1, 1);

//        if(gamepad1.right_trigger > 0.5){
//            // Of which, the code within just divides all the power variables by 2.
//            //In summary, if right trigger is pressed, half the power.
//            leftFrontPower /= 2;
//            leftBackPower /= 2;
//            rightFrontPower /= 2;
//            rightBackPower /= 2;
//        } else if(gamepad1.left_trigger > 0.5){
//            leftFrontPower /= 4;
//            leftBackPower /= 4;
//            rightFrontPower /= 4;
//            rightBackPower /= 4;
//        }
        //Front Intake Code
        if (gamepad1.left_bumper || gamepad2.left_bumper || gamepad2.left_trigger > 0.5 && shooter.getVelocity() > 1000){
            intakeForwardSpeed=1;
        }else if(gamepad1.dpad_down || gamepad2.dpad_down){
            intakeForwardSpeed=-1;
        } else {
            intakeForwardSpeed=0;

        }

        //Back Intake Code
        if (gamepad1.right_bumper || gamepad2.right_bumper || gamepad2.left_trigger > 0.5 && shooter.getVelocity()>1000){
            intakeBackSpeed=1;
        } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
            intakeBackSpeed=-1;
        } else {
            intakeBackSpeed=0;
        }
        //Shooter Code
        if(gamepad2.left_trigger > 0.5){
            shooterVelocity=1000;
            if(shooter.getVelocity()>1000){
                blockerFront.setPosition(0.36);
            }
        } else if (gamepad2.right_trigger > 0.5 || gamepad1.a) {
            shooterVelocity=1800;
            if(shooter.getVelocity()>1800){
                blocker.setPosition(0.36);
            }
        } else {
            shooterVelocity=0;
            blockerFront.setPosition(0);
            blocker.setPosition(0);
        }





            //setting final power levels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            shooter.setVelocity(shooterVelocity);
            if (isReversed) {
                intakeForward.setPower(intakeBackSpeed);
                intakeBack.setPower(intakeForwardSpeed);
            } else {
                intakeForward.setPower(intakeForwardSpeed);
                intakeBack.setPower(intakeBackSpeed);
            }

        // telemetry
        telemetry.addData("left front motor expected", "power = %.2f", leftFrontPower);
        telemetry.addData("right front motor expected", "power = %.2f", rightFrontPower);
        telemetry.addData("left rear motor expected", "power = %.2f", leftBackPower);
        telemetry.addData("right rear motor expected", "power = %.2f", rightBackPower);
        telemetry.addData("shooter velocity", "velocity = %.2f", shooter.getVelocity());
        telemetry.addData("Reversed:", isReversed);

    }


    @Override
    public void stop() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        intakeBack.setPower(0);
        intakeForward.setPower(0);
        shooter.setPower(0);
    }

}

