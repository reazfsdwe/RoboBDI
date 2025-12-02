
import jason.asSyntax.Literal;
import java.util.*;

public class PerceptsForJason {
    // List assumptions as constants
    static final int NUM_DEFENDERS = 3;
    static final int FIELD_HEIGHT = 100;
    static final int POSITION_TOLERANCE = 10;


    public List<Literal> getPercepts(VisualInfo info, char side) {

            List<Literal> percepts = new ArrayList<Literal>();
            percepts.add(Literal.parseLiteral(String.format("side(%c)", side)));
            if (info == null) {
                percepts.add(Literal.parseLiteral("no_info"));
                return percepts;
            }

            Vector<ObjectInfo> objects = info.m_objects;
            boolean ballFound = false;
            boolean flagFound = false;
            boolean goalFound = false;
            boolean topFound = false;
            
            for (ObjectInfo obj : objects) {
                String type = obj.m_type;
                float dist = obj.m_distance;
                float dir = obj.m_direction;
                
                if (type == null) continue;

                if (type.startsWith("ball")) {
                
                    percepts.add(Literal.parseLiteral(String.format("see_ball(%f, %f)", dist, dir)));
                    ballFound = true;
                } else if (type.startsWith("flag")) {
                    computeFieldFlags((FlagInfo) obj, percepts, side);
                    if (side == 'l' && obj.m_type.equals("flag p l c") || (side == 'r' && obj.m_type.equals("flag p r c"))) {
                        percepts.add(Literal.parseLiteral(String.format("see_my_goal_flag(\"%s\", %f, %f)", type, dist, dir)));
                        flagFound = true;
                    }else {
                        flagFound = false;
                    }
                } else if (type.startsWith("goal")) {
                    
                    percepts.add(Literal.parseLiteral(String.format("see_goal(\"%s\", %f, %f)", type, dist, dir)));
                    goalFound = true;

                    GoalInfo goal = (GoalInfo) obj;
                    char kind = goal.getSide();

                    if((side=='l' && kind=='r') || (side=='r' && kind=='l')) {
                        percepts.add(Literal.parseLiteral(String.format("see_enemy_goal(%f)", dist, dir)));
                    }
                    if((side=='l' && kind=='l') || (side=='r' && kind=='r')) {
                        percepts.add(Literal.parseLiteral(String.format("goalie_see_own_goal(%f, %f)", dist, dir)));
                        
                    }
                }else if (type.startsWith("line")) {
                    LineInfo line = (LineInfo) obj;
                    char kind = line.m_kind;

                    if(kind == 't'){
                        percepts.add(Literal.parseLiteral(String.format("see_topline(%f, %f)",  dist, dir)));
                        topFound = true;
                        computePositionalPrecepts(dist, percepts);
                    }
                }
                System.out.println(percepts);
            
            }

            if (!flagFound) {
                percepts.add(Literal.parseLiteral("flag_lost"));
            }
            if (!goalFound) {
                percepts.add(Literal.parseLiteral("goal_lost"));
            }
            if (!ballFound) {
                percepts.add(Literal.parseLiteral("ball_lost"));
            }if (!topFound) {
                percepts.add(Literal.parseLiteral("align_info_lost"));
            }
            
            return percepts;
    }

    /**
     * Add precepts locating relevant positional flags.
     * Center flag: centerFlag(dir)
     * Top Flag (for positional alignment): topFlag(dist, dir)
     *
     * @param flag The flag info object to parse for precept information
     * @param percepts The list of logical percepts
     * @param side The team side of the agent
     */
    private void computeFieldFlags(FlagInfo flag, List<Literal> percepts, char side) {
        String type = flag.getType();
        // Center flag
        if (type.equals("flag c"))
            percepts.add(Literal.parseLiteral(String.format("centerFlag(%f)", flag.getDirection())));
        // Top flag, in own side (left)
        if (type.equals("flag t l 30") && side == 'l')
            percepts.add(Literal.parseLiteral(String.format("topFlag(%f, %f)", flag.getDistance(), flag.getDirection())));
        // Top flag, in own side (right)
        if (type.equals("flag t r 30") && side == 'r')
            percepts.add(Literal.parseLiteral(String.format("topFlag(%f, %f)", flag.getDistance(), flag.getDirection())));
    }

    /**
     * Identify if the agent falls within intended position ranges.
     * Field is split in 4 sections (vertically!),
     * defenders are expected to be at the center of these sections, with some tolerance.
     * 4, to evenly split amongst the three defenders.
     *
     * @param dist The distance from the top of the field
     * @param percepts The list of logical percepts
     */
    private void computePositionalPrecepts(float dist, List<Literal> percepts) {

        // The very minimum bound, progressively going up to the top bound
        if(dist < (FIELD_HEIGHT/(NUM_DEFENDERS+1))-POSITION_TOLERANCE) { // Bound here: less than 1/4 - tolerance
            percepts.add(Literal.parseLiteral(String.format("less_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_two_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_three_quarter")));
        } else if (dist < (FIELD_HEIGHT/(NUM_DEFENDERS+1))+POSITION_TOLERANCE) { // Bound here: more than 1/4 + tolerance
            // First percept removed, in position!
            percepts.add(Literal.parseLiteral(String.format("less_two_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_three_quarter")));
        } else if (dist < 2* (FIELD_HEIGHT/(NUM_DEFENDERS+1))-POSITION_TOLERANCE) { // Repeat... for 2nd/third quarters
            percepts.add(Literal.parseLiteral(String.format("above_quarter"))); // Past first position
            percepts.add(Literal.parseLiteral(String.format("less_two_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_three_quarter")));
        } else if (dist < 2* (FIELD_HEIGHT/(NUM_DEFENDERS+1))+POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral(String.format("above_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_three_quarter")));
        } else if (dist < 3* (FIELD_HEIGHT/(NUM_DEFENDERS+1))-POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral(String.format("above_quarter")));
            percepts.add(Literal.parseLiteral(String.format("above_two_quarter")));
            percepts.add(Literal.parseLiteral(String.format("less_three_quarter")));
        } else if (dist < 3* (FIELD_HEIGHT/(NUM_DEFENDERS+1))+POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral(String.format("above_quarter")));
            percepts.add(Literal.parseLiteral(String.format("above_two_quarter")));
        } else { // Above everything, top bound
            percepts.add(Literal.parseLiteral(String.format("above_quarter")));
            percepts.add(Literal.parseLiteral(String.format("above_two_quarter")));
            percepts.add(Literal.parseLiteral(String.format("above_three_quarter")));
        }
    }


}

