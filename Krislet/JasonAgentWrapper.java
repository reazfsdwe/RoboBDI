
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
    private PerceptsForJason perceptsGenerator = new PerceptsForJason();
    private char side;
    private Memory currentMemory; 
 
    private ActionExec selectedAction; 

    public JasonAgentWrapper(int playerNumber, char side) {
        this.side = side;
        
        try {
            new RunLocalMAS().setupLogger(); 
            Agent ag = new Agent();
            new TransitionSystem(ag, null, null, this);
            ag.initAg();

            if (playerNumber == 1){
                ag.load("Rambo.asl");
            }
            else if (playerNumber == 2){
                ag.load("Goalie.asl");
            }else{
                ag.load("Others.asl");
            }

            
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
        
        VisualInfo currentVisual = currentMemory.getVisualInfo();
        List<Literal> percepts = perceptsGenerator.getPercepts(currentVisual, side);
        
        return percepts;
    }

    @Override
    public void act(ActionExec action) {
        
        this.selectedAction = action;
        
        
        action.setResult(true);
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