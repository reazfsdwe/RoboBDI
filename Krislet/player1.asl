!start.


+!start : true 
    <- .print("Game Started - Entering Loop");
       !play.



+!play 
    : see_ball(Dist, Dir) & Dist <= 1.0 
    <- kick(100, 0);
       !play.


+!play 
    : see_ball(Dist, Dir) & (Dir > 10 | Dir < -10)
    <- turn(Dir);
       !play.


+!play 
    : see_ball(Dist, Dir) & Dist > 1.0
    <- dash(80);
       !play.


+!play 
    : ball_lost
    <- turn(40);
       !play.


+!play 
    : true 
    <- .print("Waiting for visual info...");
       !play.