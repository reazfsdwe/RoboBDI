!start.


+!start : true 
    <- .print("Game Started - Entering Loop, Im a Defender");
       !play.

+!search : true
    <- .print("Searching for info...");
    turn(50).

+!alignCenter : centerFlag(dir)
    <- .print("Aligning to center of field.");
    turn(dir).

+!alignCenter : true
    <-
    !search;
    !alignCenter.

+!align_top : topFlag(Dist, Dir)
    <- .print("See top flag.");
    turn(dir).

+!align_top : ~topFlag(Dist, Dir)
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
       !alignCenter;
       kick(100, 0);
       !play.

+!play
    : see_ball(Dist, Dir) & Dist < 25
    <- .print("Ball close, chasing!");
       turn(Dir);
       dash(100);
       !play.

+!play
    : see_ball(Dist, Dir) & Dist > 25
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

