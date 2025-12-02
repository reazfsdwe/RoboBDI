// ================== PARAMETERS =======================================

// Distance threshold for being "at" the center (dominance radius)
dominance_radius(3.0).

// Turning angle used while searching (centre/ball)
search_turn_angle(45).

// Distance threshold for ball being "close" while exerting dominance
ball_close_threshold(10.0).

// Alignment tolerance (degrees) to consider "facing" something
facing_tolerance(10).


// ====================================================================

!start.

+!start : true
    <-  .print("Game Started - Rambo: Asserting Dominance (centre)");
        !assert_dominance.

// ====================================================================
// ASSERTING DOMINANCE: going to the centre with omni-directional dashes
// ====================================================================

// Case 1: centre visible and far -> dash toward it with direction Dir
+!assert_dominance
    : see_center_flag(Dist, Dir)
      & dominance_radius(R)
      & Dist > R
    <-  .print("Rambo: asserting dominance (dashing toward centre). Dist=", Dist, ", Dir=", Dir);
        dash(100, Dir);
        !assert_dominance.

// Case 2: centre visible and within dominance radius -> switch to exerting
+!assert_dominance
    : see_center_flag(Dist, _)
      & dominance_radius(R)
      & Dist <= R
    <-  .print("Rambo: entered dominance zone at centre (Dist=", Dist, "). Exerting dominance.");
        !exert_dominance.

// Case 3: centre not visible -> search by turning in place
+!assert_dominance
    : not see_center_flag(_, _)
      & search_turn_angle(A)
    <-  .print("Rambo: asserting dominance but cannot see centre. Searching...");
        turn(A);
        !assert_dominance.



// ====================================================================
// EXERTING DOMINANCE: hold centre, only turn to track the ball
// ====================================================================

// Ball visible and far (>= threshold) -> turn toward ball, no dash
+!exert_dominance
    : see_ball(DistB, DirB)
      & ball_close_threshold(BT)
      & DistB >= BT
    <-  .print("Rambo: exerting dominance - ball far (Dist=", DistB, ", Dir=", DirB, "). Turning to face ball.");
        turn(DirB);
        !exert_dominance.

// Ball visible and close (< threshold) -> transition to proving dominance
+!exert_dominance
    : see_ball(DistB, _)
      & ball_close_threshold(BT)
      & DistB < BT
    <-  .print("Rambo: exerting dominance - ball close (Dist=", DistB, "). Proving dominance!");
        !prove_dominance.

// Ball not visible -> search for it by turning
+!exert_dominance
    : not see_ball(_, _)
      & search_turn_angle(A)
    <-  .print("Rambo: exerting dominance - lost sight of ball. Searching...");
        turn(A);
        !exert_dominance.

// ====================================================================
// PROVING DOMINANCE: engaging with the ball
// ====================================================================

// Case 0: a goal was scored -> go back to asserting dominance at centre
+!prove_dominance
    : goal_scored
    <-  .print("Rambo: goal scored. Returning to centre to assert dominance.");
        !assert_dominance.


// Case 1: ball visible and very far (Dist >= 30) -> go back to asserting dominance
+!prove_dominance
    : see_ball(DistB, _)
      & DistB >= 30.0
    <-  .print("Rambo: ball too far (Dist=", DistB, "). Returning to centre to assert dominance.");
        !assert_dominance.

// Case 2a: 1 <= Dist < 30 and facing the ball (within tolerance) -> dash toward ball
+!prove_dominance
    : see_ball(DistB, DirB)
      & DistB >= 1.0
      & DistB < 30.0
      & facing_tolerance(Tol)
      & DirB <= Tol
      & DirB >= -Tol
    <-  .print("Rambo: proving dominance - dashing toward ball. Dist=", DistB, ", Dir=", DirB);
        dash(100, DirB);
        !prove_dominance.

// Case 2b: 1 <= Dist < 30 and NOT facing the ball -> turn toward ball
+!prove_dominance
    : see_ball(DistB, DirB)
      & DistB >= 1.0
      & DistB < 30.0
      & facing_tolerance(Tol)
      & DirB > Tol
    <-  .print("Rambo: proving dominance - turning toward ball (Dir=", DirB, ").");
        turn(DirB);
        !prove_dominance.

+!prove_dominance
    : see_ball(DistB, DirB)
      & DistB >= 1.0
      & DistB < 30.0
      & facing_tolerance(Tol)
      & DirB < -Tol
    <-  .print("Rambo: proving dominance - turning toward ball (Dir=", DirB, ").");
        turn(DirB);
        !prove_dominance.

// Case 3a: Dist < 1 and can see opponent's goal -> kick toward goal
+!prove_dominance
    : see_ball(DistB, _)
      & DistB < 1.0
      & see_opponent_goal(_, DirG)
    <-  .print("Rambo: proving dominance - ball at feet, shooting toward goal (Dir=", DirG, ").");
        kick(100, DirG);
        !prove_dominance.

// Case 3b: Dist < 1 and cannot see goal -> turn to find goal
+!prove_dominance
    : see_ball(DistB, _)
      & DistB < 1.0
      & not see_opponent_goal(_, _)
      & search_turn_angle(A)
    <-  .print("Rambo: proving dominance - ball at feet but no goal in sight. Searching for goal...");
        turn(A);
        !prove_dominance.

// Fallback: no ball visible during proving dominance -> search for ball
+!prove_dominance
    : not see_ball(_, _)
      & search_turn_angle(A)
    <-  .print("Rambo: proving dominance - lost sight of ball. Searching...");
        turn(A);
        !prove_dominance.

