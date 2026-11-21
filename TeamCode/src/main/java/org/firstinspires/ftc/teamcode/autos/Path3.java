package org.firstinspires.ftc.teamcode.autos;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Path3", group = "Autonomous")
public class Path3 extends LinearOpMode {
    private DcMotorEx shooter = null;
    private DcMotor frontIntake = null;
    private Servo blocker = null;
    private Servo frontBlocker = null;
    int restShooterVeloc = 1000;
    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(82.2365, 8.6878, 90);
    private final Pose path1 = poseFactory.of(82.3348, 8.9774, 90);
    private final Pose point2 = poseFactory.of(82.6865, 29.6702, 89.0262);
    private final Pose point3 = poseFactory.of(128.9134, 29.5629, 179.867);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                shoot(1600, 3000),
                follow(follower, path1()),
                follow(follower, path2()),
                follow(follower, path3()),
                stopping()
        );
    }
    public Command shoot(int power, int pause) {
        return sequential(
                instant(() -> shooter.setVelocity(power)),
                waitMs(pause / 2),
                instant(() -> frontBlocker.setPosition(0.36)),
                waitMs(pause/2),
                instant(() -> frontIntake.setPower(1)),
                waitMs(pause),
                instant(() -> frontIntake.setPower(0)),
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
        return Paths.line(start, path1).linear(start, path1);
    }

    public Path path2() {
        return Paths.line(path1, point2).tangent();
    }

    public Path path3() {
        return Paths.line(point2, point3).reverseTangent();
    }
}
