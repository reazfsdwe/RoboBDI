!start.


+!start : true 
    <- .print("Game Started - Entering Loop, Im a Defender");
       !play.

+!search : true
    <- .print("Searching for info...");
    turn(50).

+!alignEnemyGoal: see_enemy_goal
    <- .print("Aligned to enemy goal.").

+!alignEnemyGoal: ~see_enemy_goal
    <-
    !search;
    !alignEnemyGoal.

+!align_top : see_topline(Dist, Dir)
    <- .print("See topline.").

+!align_top : ~see_topline(Dist, Dir)
    <-
    !search;
    !align_top.


+!play
    : ball_lost
    <- .print("Ball lost, search!");
       !search;
       !play.

+!play
    : see_ball(Dist, Dir) & Dist <= 1
    <- .print("Ball in range, kicking to center field!");
       !alignEnemyGoal;
       kick(100, 0);
       !play.

+!play
    : see_ball(Dist, Dir) & Dist < 20
    <- .print("Ball close, chasing!");
       turn(Dir);
       dash(100);
       !play.

+!play
    : see_ball(Dist, Dir) & Dist > 20
    <- .print("Ball far, returning to position!");
       !returnToDefense.

+!play
    : true
    <- .print("Waiting...");
       !play.

+!returnToDefense
    : align_info_lost
    <- .print("Missing alignment info, searching!");
       !search;
       !returnToDefense.




+!returnToDefense
    : above_quarter
    <- .print("Moving up to defense position");
           dash(100);
           !returnToDefense.

+!returnToDefense
    : less_quarter
    <- .print("Ball far, moving down to defense position");
           dash(-100);
           !returnToDefense.




+!returnToDefense : true
  <- .print("In position, back to regular play");
  !play.

