!start.


+!start : true 
    <- .print("Game Started - Entering Loop, Im Other");
       !play.





+!play 
    : see_ball(Dist, Dir) & (Dir > 10 | Dir < -10)
    <- turn(40);
       !play.


+!play 
    : ball_lost
    <- turn(40);
       !play.


+!play 
    : true 
    <- .print("Waiting for visual info...");
       !play.