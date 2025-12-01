
state(normal).
position(incorrect).


!start.

+!play 
    : state(normal) & not see_ball(_, _) 
    <- .print("Normal Mode: Find ball");
       turn(50);
       !play.  

+!start : true 
    <- .print("Goalie Started - I'm goalie!");
       !!play.  

+!play 
    : state(normal) & see_ball(D, A) & D <= 1.0
    <- .print("Normal Mode: kick the ball");
       kick(100, A);
       !play.  

+!checkPosition 
    : state(normal) & goalie_see_own_goal(D, A) & D >= 5
    <- .print("Adjusting Position to center of Goal");
       -+position(incorrect);
       !play.

+!checkPosition 
    : state(normal) & not goalie_see_own_goal(_, _)
    <- .print("Adjusting Position to center of Goal");
       turn(90);
      !checkPosition.
           
+!play 
    : state(normal) & see_ball(D, A) & D < 20
    <- .print("Normal Mode: Attacking ball");
       dash(100);
       !!play.  

+!play 
    : position(incorrect) & state(normal) & see_ball(D, _) & D >= 20
    <- .print("Ball too far! SWITCHING TO RETURN MODE.");
       -+state(returning);  
       !play.  

+!play 
    : position(correct) & state(normal) & see_ball(D, _) & D >= 20
    <- .print("Watching Ball from Goal. "); 
       !play.  


+!play 
    : position(correct) & state(normal) & not see_ball(_, _)
    <- .print("Normal Mode: Searching for ball...");
       turn(50);
       !play.


+!play 
    : state(returning) & goalie_see_own_goal(D, A) & D >= 3 
    <- .print("Returning... Ignoring ball.");
       dash(100);
       !play. 


+!play 
    : state(returning) & goalie_see_own_goal(D, A) & D <= 3  
    <- .print("Arrived! Turning around & Switching to Normal.");
       -+state(normal);
       -+position(correct);
       !play.


+!play 
    : state(returning) & not goalie_see_own_goal(_, _)
    <- .print("Returning: Lost own goal, searching...");
       turn(40);
       !play.

