package org.firstinspires.ftc.teamcode.autos;

import org.firstinspires.ftc.teamcode.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "FarSimple", group = "Autonomous")
public class FarSimple extends LinearOpMode {
    private DcMotorEx shooter = null;
    private DcMotor frontIntake = null;
    private DcMotor backIntake = null;
    private Servo blocker = null;
    private Servo frontBlocker = null;
    int restShooterVeloc = 1000;

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(54, 8, 90);
    private final Pose path1End = poseFactory.of(54, 12, 90);
    private final Pose point2 = poseFactory.of(9, 98, 90);
    private final Pose point2Control1 = poseFactory.of(10, 28, 90);
    private final Pose point2Control2 = poseFactory.of(44, 94, 90);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                shoot(1500, 6000),
                follow(follower, path1()),
                follow(follower, path2()),
                stopping()
        );
    }

    public Command shoot(int power, int pause) {
        return sequential(
                instant(() -> shooter.setVelocity(power)),
                waitMs(pause/2),
                instant(() -> frontBlocker.setPosition(0.36)),
                waitMs(pause/2),
                instant(() -> frontIntake.setPower(0.75)),
                instant(() -> blocker.setPosition(0.36)),
                waitMs(pause),
                instant(() -> backIntake.setPower(0.75)),
                instant(() -> frontBlocker.setPosition(0)),
                instant(() -> frontIntake.setPower(0)),
                waitMs(pause),
                instant(() -> backIntake.setPower(0)),
                instant(() -> blocker.setPosition(0)),
                instant(() -> shooter.setVelocity(restShooterVeloc)),
                instant(() -> frontBlocker.setPosition(0))
        );
    }
    public Command stopping() {
        return sequential (
                instant(() -> OpModeStorage.autonomousEndPose = follower.pose()),
                instant(() -> shooter.setVelocity(0))
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter.setDirection(DcMotorEx.Direction.FORWARD);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontIntake = hardwareMap.get(DcMotor.class, "intakeForward");
        frontIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontIntake.setDirection(DcMotor.Direction.FORWARD);
        frontIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        backIntake = hardwareMap.get(DcMotor.class, "intakeBack");
        backIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backIntake.setDirection(DcMotor.Direction.FORWARD);
        backIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        blocker = hardwareMap.get(Servo.class, "blocker");
        blocker.setDirection(Servo.Direction.FORWARD);

        frontBlocker = hardwareMap.get(Servo.class, "frontBlocker");
        frontBlocker.setDirection(Servo.Direction.FORWARD);

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        shooter.setVelocity(restShooterVeloc);
        frontBlocker.setPosition(0);
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path path1() {
        return Paths.line(start, path1End).linear(start, path1End);
    }

    public Path path2() {
        return Paths.curve(path1End, point2Control1, point2Control2, point2).linear(path1End, point2);
    }

}

