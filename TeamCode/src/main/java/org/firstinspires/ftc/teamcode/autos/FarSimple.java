package org.firstinspires.ftc.teamcode.autos;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
@Autonomous(name = "FarSimple", group = "Autonomous")
public class FarSimple extends LinearOpMode {
    private DcMotorEx shooter = null;
    private DcMotor frontIntake = null;
    int restShooterVeloc = 500;
    boolean setupTrue = false;

    //private Servo hood = null;
    //private Servo turret = null;
    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(54, 8, 90);
    private final Pose path1 = poseFactory.of(54, 12, 90);
    private final Pose point2Start = poseFactory.of(54, 8, 90);
    private final Pose point2 = poseFactory.of(9, 98, 90);
    private final Pose point2Control1 = poseFactory.of(10, 28, 90);
    private final Pose point2Control2 = poseFactory.of(44, 94, 90);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, path1()),
                follow(follower, path2())
        );
    }
    public void shoot(int power
                    //, int turret, double hood
                    , int pause){
        if (setupTrue) {
            shooter.setVelocity(power);
            sleep(pause);
            frontIntake.setPower(1);
            sleep(pause);
            frontIntake.setPower(0);
            shooter.setVelocity(restShooterVeloc);
        }
    }
    public void setup(){
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter.setDirection(DcMotorEx.Direction.FORWARD);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontIntake = hardwareMap.get(DcMotor.class, "intakeForward");
        frontIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontIntake.setDirection(DcMotor.Direction.FORWARD);
        frontIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setupTrue = true;
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        shooter.setVelocity(restShooterVeloc);
        setup();

        waitForStart();
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
        shoot(700, 1000);
        return Paths.line(start, path1).linear(start, path1);
    }

    public Path path2() {
        return Paths.curve(point2Start, point2Control1, point2Control2, point2).linear(point2Start, point2);
    }
}
