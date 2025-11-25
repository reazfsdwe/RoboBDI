
import java.util.regex.*;
import java.lang.Math;
import jason.asSemantics.ActionExec; 

class Brain extends Thread implements SensorInput
{
    private JasonAgentWrapper m_jasonAgent; 

    public Brain(SendCommand krislet, 
		 String team, 
		 char side, 
		 int number, 
		 String playMode)
    {
        m_timeOver = false;
        m_krislet = krislet;
        m_memory = new Memory();
        m_side = side;
        m_playMode = playMode;
        m_jasonAgent = new JasonAgentWrapper();

        start();
    }

    public void run()
    {
        
        if(Pattern.matches("^before_kick_off.*",m_playMode))
            m_krislet.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );

        while( !m_timeOver )
        {        
            ActionExec action = m_jasonAgent.reason(m_memory);

            if (action != null) {
                System.out.println("doing aciton");
				System.out.println(action.toString());
				executeAction(action);
            } else {
				System.out.println("no action selected, waiting for new info...");
            }

            try{
                Thread.sleep(2*SoccerParams.simulator_step);
            }catch(Exception e){}
        }
        m_krislet.bye();
    }

   
    private void executeAction(ActionExec action) {
        String functor = action.getActionTerm().getFunctor();
        
        try {
            if (functor.equals("turn")) {
               
                double degree = Double.parseDouble(action.getActionTerm().getTerm(0).toString());
				System.out.println(degree);
                m_krislet.turn(degree);
            } 
            else if (functor.equals("dash")) {
                
                double power = Double.parseDouble(action.getActionTerm().getTerm(0).toString());
                m_krislet.dash(power);
            } 
            else if (functor.equals("kick")) {
                
                double power = Double.parseDouble(action.getActionTerm().getTerm(0).toString());
                double dir = Double.parseDouble(action.getActionTerm().getTerm(1).toString());
                m_krislet.kick(power, dir);
            }
            else if (functor.equals(".print")) {
                 System.out.println("Agent says: " + action.getActionTerm().getTerm(0));
            }
        } catch (Exception e) {
            System.err.println("Error executing action: " + action);
        }
    }


    public void see(VisualInfo info) { m_memory.store(info); }
    public void hear(int time, int direction, String message) {}
    public void hear(int time, String message) { if(message.compareTo("time_over") == 0) m_timeOver = true; }


    private SendCommand	        m_krislet;
    private Memory			    m_memory;
    private char			    m_side;
    volatile private boolean	m_timeOver;
    private String              m_playMode;
}