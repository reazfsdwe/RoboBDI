//
// File: JasonAgentWrapper.java
//
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.infra.local.RunLocalMAS;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JasonAgentWrapper extends AgArch {

    private static Logger logger = Logger.getLogger(JasonAgentWrapper.class.getName());
    
    private Memory currentMemory; 

    private ActionExec selectedAction; 

    public JasonAgentWrapper() {
       
        try {
            new RunLocalMAS().setupLogger(); 
            new TransitionSystem(ag, null, null, this);
            ag.initAg();
            ag.load("player1.asl"); 
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
    }

    
    public ActionExec reason(Memory memory) {
        this.currentMemory = memory;
        this.selectedAction = null; 

        try {
            
            getTS().reasoningCycle();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Reasoning error", e);
        }

        return this.selectedAction; 
    }

    @Override
    public List<Literal> perceive() {
        List<Literal> l = new ArrayList<Literal>();
        
        
        if (currentMemory != null) {
            ObjectInfo ball = currentMemory.getObject("ball");
            if (ball != null) {
            
                l.add(Literal.parseLiteral(String.format("see_ball(%f, %f)", ball.m_distance, ball.m_direction)));
            } else {
               
                l.add(Literal.parseLiteral("ball_lost"));
            }
        }
        return l;
    }

    @Override
    public void act(ActionExec action) {
       
        this.selectedAction = action;
        
       
        actionExecuted(action);
    }

    @Override
    public boolean canSleep() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return true;
    }
    

    @Override public void sendMsg(jason.asSemantics.Message m) throws Exception {}
    @Override public void broadcast(jason.asSemantics.Message m) throws Exception {}
    @Override public void checkMail() {}
}