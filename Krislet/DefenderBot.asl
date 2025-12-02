!start.

// ---------------------------------------------------------
// Startup: go to bottom penalty-box flag
// ---------------------------------------------------------
+!start : true 
    <-  .print("Game Started - I'm Bottom Defender");
        !go_on_defense.

+goal_against
    <-  .print("BottomDef: goal against us! Resetting to defensive flag.");
        .drop_all_intentions;
        !go_on_defense.


// ---------------------------------------------------------
// GO_ON_DEFENSE: move to assigned defensive flag
// ---------------------------------------------------------

// Flag not visible -> search by turning
+!go_on_defense
    : not see_defense_bottom_flag(_, _)
    <-  .print("BottomDef: searching for defense flag (bottom).");
        turn(45);
        !go_on_defense.

// Flag visible but still far (D >= 5) -> dash toward it
+!go_on_defense
    : see_defense_bottom_flag(D, Dir)
      & D >= 5.0
    <-  .print("BottomDef: moving toward defense flag. Dist=", D, " Dir=", Dir);
        dash(100, Dir);
        !go_on_defense.

// Flag visible and close (D < 5) -> start defending
+!go_on_defense
    : see_defense_bottom_flag(D, _)
      & D < 5.0
    <-  .print("BottomDef: reached defensive position (Dist=", D, "). Switching to defend.");
        !defend.

// ---------------------------------------------------------
// DEFEND: watch ball from defensive position
// ---------------------------------------------------------

// No ball -> scan in place
+!defend
    : not see_ball(_, _)
    <-  .print("BottomDef: defending, no ball visible - scanning.");
        turn(45);
        !defend.

// Ball far (D >= 20) -> track ball direction, stay in zone
+!defend
    : see_ball(D, Dir)
      & D >= 20.0
    <-  .print("BottomDef: ball far (Dist=", D, "). Tracking direction only.");
        turn(Dir);
        !defend.

// Ball at medium distance (1 <= D < 20) -> go chase it
+!defend
    : see_ball(D, _)
      & D >= 1.0
      & D < 20.0
    <-  .print("BottomDef: ball in range (Dist=", D, "). Going to get it.");
        !get_ball.

// Ball already very close (D < 1) -> go directly to support (clear)
+!defend
    : see_ball(D, _)
      & D < 1.0
    <-  .print("BottomDef: ball at feet (Dist=", D, "). Supporting immediately.");
        !support.

// ---------------------------------------------------------
// GET_BALL: chase the ball at full power
// ---------------------------------------------------------

// Lost sight of ball -> return to defend near flag
+!get_ball
    : not see_ball(_, _)
    <-  .print("BottomDef: lost ball while chasing. Returning to defend.");
        !go_on_defense.

// See ball and still not at feet (D >= 1) -> dash toward ball direction
+!get_ball
    : see_ball(D, Dir)
      & D >= 1.0
    <-  .print("BottomDef: chasing ball. Dist=", D, " Dir=", Dir);
        dash(100, Dir);
        !get_ball.

// Reached ball (D < 1) -> support
+!get_ball
    : see_ball(D, _)
      & D < 1.0
    <-  .print("BottomDef: reached ball (Dist=", D, "). Switching to support.");
        !support.

// ---------------------------------------------------------
// SUPPORT: clear ball toward centre flag, then reset to defense
// ---------------------------------------------------------

// Centre flag not visible -> search
+!support
    : not see_center_flag(_, _)
    <-  .print("BottomDef: support mode - centre flag not visible, searching...");
        turn(45);
        !support.

// Centre flag visible -> turn to it and clear ball, then go back to defense
+!support
    : see_center_flag(DC, DirC)
    <-  .print("BottomDef: clearing ball toward centre. DistCenter=", DC, " DirCenter=", DirC);
        kick(100, DirC);
        .print("BottomDef: ball cleared, going back to defense.");
        !go_on_defense.
