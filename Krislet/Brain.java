import java.util.regex.*;
import java.lang.Math;
import jason.asSemantics.ActionExec; 

class Brain extends Thread implements SensorInput {

    private JasonAgentWrapper m_jasonAgent;

    private SendCommand m_krislet;
    private Memory      m_memory;
    private char        m_side;
    volatile private boolean m_timeOver;
    private String      m_playMode;

    // === CONFIGURATION OPTIONS ===
    // Whether to use repeat movement when no new Jason action arrives
    private boolean enableRepeatMovement = true;

    // Power level for repeat dash (e.g., when repeating dash1 or dash2)
    // You can tune this (e.g., 10, 20, etc.)
    private double repeatDashPower = 10.0;

    // Cache of last *movement* type and parameters
    // lastActionType ∈ { "dash1", "dash2", "kick", "turn", null }
    private String lastActionType = null;
    private double lastDashPower  = 0.0;
    private double lastDashDir    = 0.0;    // only used for dash2
    private double lastKickDir    = 0.0;    // direction of last kick

    public Brain(SendCommand krislet, 
                 String team, 
                 char side, 
                 int number, 
                 String playMode) {

        m_timeOver = false;
        m_krislet  = krislet;
        m_memory   = new Memory();
        m_side     = side;
        m_playMode = playMode;

        // store initial play mode in memory so Jason sees it immediately
        m_memory.setPlayMode(m_playMode);

        m_jasonAgent = new JasonAgentWrapper(number, side);

        start();
    }

    public void run() {

        // Initial random positioning before kick off (same as original)
        if (Pattern.matches("^before_kick_off.*", m_playMode)) {
            m_krislet.move(-Math.random()*52.5, 34 - Math.random()*68.0);
        }

        while (!m_timeOver) {

            // Block until new visual info arrives
            m_memory.waitForNewInfo();

            // One Jason reasoning cycle with the current memory
            ActionExec action = m_jasonAgent.reason(m_memory);

            if (action != null) {
                // New action from Jason
                executeAction(action);
            } else {
                // No new Jason action: repeat something based on last movement
                repeatLastMovement();
            }
        }

        m_krislet.bye();
    }

    // Called when Jason gave us *no* new action this cycle
    private void repeatLastMovement() {
        if (!enableRepeatMovement) {
            return;  // do nothing when disabled
        }

        try {
            if ("dash1".equals(lastActionType)) {
                // Repeat forward dash with reduced power
                m_krislet.dash(repeatDashPower);

            } else if ("dash2".equals(lastActionType)) {
                // Repeat directional dash with reduced power
                m_krislet.dash(repeatDashPower, lastDashDir);

            } else if ("kick".equals(lastActionType)) {
                // After a kick, dash in kick direction with reduced power
                m_krislet.dash(repeatDashPower, lastKickDir);

            } else if ("turn".equals(lastActionType)) {
                // After a turn, just "wait" by turning 0
                m_krislet.turn(0.0);

            } else {
                // No repeatable movement: do nothing
            }
        } catch (Exception e) {
            System.err.println("Error repeating last movement: " + e);
        }
    }

    private void executeAction(ActionExec action) {
        String functor = action.getActionTerm().getFunctor();

        try {
            if (functor.equals("turn")) {

                double moment = Double.parseDouble(
                    action.getActionTerm().getTerm(0).toString()
                );
                m_krislet.turn(moment);

                // Cache that the last movement was a turn
                lastActionType = "turn";

            } else if (functor.equals("dash")) {

                int arity = action.getActionTerm().getArity();
                if (arity == 1) {
                    double power = Double.parseDouble(
                        action.getActionTerm().getTerm(0).toString()
                    );
                    m_krislet.dash(power);
                    lastActionType = "dash1";
                    lastDashPower  = power;

                } else if (arity == 2) {
                    double power = Double.parseDouble(
                        action.getActionTerm().getTerm(0).toString()
                    );
                    double dir   = Double.parseDouble(
                        action.getActionTerm().getTerm(1).toString()
                    );
                    m_krislet.dash(power, dir);
                    lastActionType = "dash2";
                    lastDashPower  = power;
                    lastDashDir    = dir;
                }

            } else if (functor.equals("kick")) {

                double power = Double.parseDouble(
                    action.getActionTerm().getTerm(0).toString()
                );
                double dir   = Double.parseDouble(
                    action.getActionTerm().getTerm(1).toString()
                );
                m_krislet.kick(power, dir);

                // Cache last kick direction for follow-up dash
                lastActionType = "kick";
                lastKickDir    = dir;

            } else if (functor.equals(".print")) {

                System.out.println("Agent says: " + action.getActionTerm().getTerm(0));
                // NO caching here

            }
        } catch (Exception e) {
            System.err.println("Error executing action: " + action + " — " + e);
        }
    }

    // Perception hooks
    public void see(VisualInfo info) {
        m_memory.store(info);
    }

    public void hear(int time, int direction, String message) {
        // not used (non-referee hear with direction)
    }

    public void hear(int time, String message) {
        // Referee messages (play modes, time_over, goals, etc.)
        System.out.println("[Brain.hear] time=" + time + " message='" + message + "'");

        if (message.compareTo("time_over") == 0) {
            m_timeOver = true;
        } else if (message.compareTo("half_time") == 0) {
            m_jasonAgent.switchSide();
        }
        else{
            // treat any other referee message as a play mode update
            m_playMode = message;
            m_memory.setPlayMode(message);
            System.out.println("[Brain.hear] updated playMode to '" + m_playMode + "'");
        }
    }
}
