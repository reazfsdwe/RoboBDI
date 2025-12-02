!start.

// ---------------------------------------------------------
+!start : true
    <-  .print("Game Started - I'm Middle Defender");
        !go_on_defense.

+goal_against
    <-  .print("BottomDef: goal against us! Resetting to defensive flag.");
        .drop_all_intentions;
        !go_on_defense.


// GO_ON_DEFENSE: use centre penalty flag
+!go_on_defense
    : not see_defense_center_flag(_, _)
    <-  .print("MidDef: searching for defense flag (center).");
        turn(45);
        !go_on_defense.

+!go_on_defense
    : see_defense_center_flag(D, Dir)
      & D >= 5.0
    <-  .print("MidDef: moving toward defense flag. Dist=", D, " Dir=", Dir);
        dash(100, Dir);
        !go_on_defense.

+!go_on_defense
    : see_defense_center_flag(D, _)
      & D < 5.0
    <-  .print("MidDef: reached defensive position (Dist=", D, "). Switching to defend.");
        !defend.

// DEFEND (same structure)
+!defend
    : not see_ball(_, _)
    <-  .print("MidDef: defending, no ball visible - scanning.");
        turn(45);
        !defend.

+!defend
    : see_ball(D, Dir)
      & D >= 20.0
    <-  .print("MidDef: ball far (Dist=", D, "). Tracking direction only.");
        turn(Dir);
        !defend.

+!defend
    : see_ball(D, _)
      & D >= 1.0
      & D < 20.0
    <-  .print("MidDef: ball in range (Dist=", D, "). Going to get it.");
        !get_ball.

+!defend
    : see_ball(D, _)
      & D < 1.0
    <-  .print("MidDef: ball at feet (Dist=", D, "). Supporting immediately.");
        !support.

// GET_BALL
+!get_ball
    : not see_ball(_, _)
    <-  .print("MidDef: lost ball while chasing. Returning to defend.");
        !go_on_defense.

+!get_ball
    : see_ball(D, Dir)
      & D >= 1.0
    <-  .print("MidDef: chasing ball. Dist=", D, " Dir=", Dir);
        dash(100, Dir);
        !get_ball.

+!get_ball
    : see_ball(D, _)
      & D < 1.0
    <-  .print("MidDef: reached ball (Dist=", D, "). Switching to support.");
        !support.

// SUPPORT
+!support
    : not see_center_flag(_, _)
    <-  .print("MidDef: support mode - centre flag not visible, searching...");
        turn(45);
        !support.

+!support
    : see_center_flag(DC, DirC)
    <-  .print("MidDef: clearing ball toward centre. DistCenter=", DC, " DirCenter=", DirC);
        kick(100, DirC);
        .print("MidDef: ball cleared, going back to defense.");
        !go_on_defense.
