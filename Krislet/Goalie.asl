!start.


+!start : true 
    <- .print("Game Started - Entering Loop, Im Goalie");
       !play.

+play
    : see_ball(Dbc, Abc) & Dbc <= 1
    <- .print("Ball extremely close, kicking away!");
       turn(Abc);       
       kick(0, 100);       
       !play.

+play
    : see_ball(Dbc, Abc) & Dbc < 15 & Dbc > 1
    <- .print("Ball close, going for it!");
       turn(Abc);       
       dash(100);       
       !play.


+!play 
    : see_my_flag("flag p l c", Dgc, Agc) & Dgc > 10 & see_ball(Dbc, Abc) & Dbc > 30
    
    <- .print("ball far, go back to own goal!");
       turn(Agc);
       dash(80);       
       !play.


+!play 
    : see_my_flag("flag p r c", Dgc, Agc) & Dgc > 10
     
    <- .print("ball far, go back to own goal!");
       turn(Agc);      
       dash(80);       
       !play.



+!play 
    : flag_lost
    <- print("Flag lost, turning to find it");
        turn(40);
        
       !play.



+!play 
    : no_info
    <- .print("Waiting for visual info...");
       !play.