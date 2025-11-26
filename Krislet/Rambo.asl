!start.


+!start : true 
    <- .print("Game Started - Entering Loop, Im Rambo");
       !play.



+!play 
    : see_ball(Dist, Dir) & Dist <= 1.0 
    <- kick(100, 0);
       !play.


+!play 
    : see_ball(Dist, Dir) & Dist < 5
    <- dash(100);
       !play.


+!play 
    : see_ball(Dist, Dir) & Dist > 5
    <- dash(100);
       !play.


+!play 
    : ball_lost
    <- turn(40);
       !play.


+!play 
    : true 
    <- .print("Waiting for visual info...");
       !play.