//---------------------------------------------------------------------
// Goalie behavior: go to goal, defend it, clear the ball
//---------------------------------------------------------------------

// Parameters
goal_stop_dist(5.0).          // distance at which we consider ourselves "at" the goal
clear_range(20.0).            // distance under which we attempt to clear the ball
ball_close_threshold(1.0).    // distance under which we can kick the ball

// Initial goal
!start.

// On start, immediately go to goal
+!start : true
    <-  .print("Goalie started. Going to my goal.");
        !go_to_goal.

// When opponents score, reset behavior: go back to goal
+goal_against
    <-  .print("Goal against us! Resetting and going back to goal.");
        .drop_all_intentions;
        !go_to_goal.


// ====================================================================
// GO TO GOAL
// ====================================================================

// Case 1: own goal not visible yet -> turn to search
+!go_to_goal
    : not goalie_see_own_goal(_, _)
    <-  .print("go_to_goal: own goal not visible, turning to find it.");
        turn(60);
        !go_to_goal.

// Case 2: own goal visible and far (D >= goal_stop_dist) -> dash toward it
+!go_to_goal
    : goalie_see_own_goal(D, A)
      & goal_stop_dist(Stop)
      & D >= Stop
    <-  .print("go_to_goal: own goal visible at Dist=", D, " Dir=", A, ". Dashing toward it.");
        dash(100, A);
        !go_to_goal.

// Case 3: own goal visible and close enough (D < goal_stop_dist) -> start defending
+!go_to_goal
    : goalie_see_own_goal(D, _)
      & goal_stop_dist(Stop)
      & D < Stop
    <-  .print("go_to_goal: close to own goal (Dist=", D, "). Switching to defend_goal.");
        !defend_goal.

// ====================================================================
// DEFEND GOAL
// ====================================================================

// Case 1: no ball in sight -> rotate in place to search
+!defend_goal
    : not see_ball(_, _)
    <-  .print("defend_goal: no ball visible, searching...");
        turn(45);
        !defend_goal.

// Case 2: ball visible but far (D >= clear_range) -> face the ball, stay near goal
+!defend_goal
    : see_ball(D, A)
      & clear_range(R)
      & D >= R
    <-  .print("defend_goal: ball far (Dist=", D, "). Just turning to face it.");
        turn(A);
        !defend_goal.

// Case 3: ball visible and within clearing range (D < clear_range) -> switch to clear_ball
+!defend_goal
    : see_ball(D, A)
      & clear_range(R)
      & D < R
    <-  .print("defend_goal: ball in clearing range (Dist=", D, "). Switching to clear_ball.");
        !clear_ball.

// ====================================================================
// CLEAR BALL
// ====================================================================

// Case 1: ball visible, 1 <= Dist < clear_range -> dash toward ball
+!clear_ball
    : see_ball(D, A)
      & clear_range(R)
      & ball_close_threshold(Close)
      & D >= Close
      & D < R
    <-  .print("clear_ball: chasing ball. Dist=", D, " Dir=", A);
        dash(100, A);
        !clear_ball.

// Case 2: ball very close (Dist < 1) -> kick hard in ball direction, then return to goal
+!clear_ball
    : see_ball(D, A)
      & ball_close_threshold(Close)
      & D < Close
    <-  .print("clear_ball: ball at feet (Dist=", D, "). Kicking hard!");
        !support;
        .print("clear_ball: ball cleared, going back to goal.");
        !go_to_goal.

// Case 3: ball visible but now far again (Dist >= clear_range) -> stop chasing, go back to goal
+!clear_ball
    : see_ball(D, _)
      & clear_range(R)
      & D >= R
    <-  .print("clear_ball: ball got far again (Dist=", D, "). Going back to goal.");
        !go_to_goal.

// Case 4: ball lost while clearing -> back to defend_goal near own goal
+!clear_ball
    : not see_ball(_, _)
    <-  .print("clear_ball: lost sight of ball. Returning to defend_goal.");
        !defend_goal.

// Centre flag not visible -> search
+!support
    : not see_center_flag(_, _)
    <-  .print("Goalie: support mode - centre flag not visible, searching...");
        turn(45);
        !support.

// Centre flag visible -> turn to it and clear ball, then go back to defense
+!support
    : see_center_flag(DC, DirC)
    <-  .print("Goalie: clearing ball toward centre. DistCenter=", DC, " DirCenter=", DirC);
        kick(100, DirC);
        .print("BottomDef: ball cleared, going back to defense.");
        !go_on_defense.