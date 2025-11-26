
import jason.asSyntax.Literal;
import java.util.*;

public class PerceptsForJason {
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
            
            for (ObjectInfo obj : objects) {
                String type = obj.m_type;
                float dist = obj.m_distance;
                float dir = obj.m_direction;
                
                if (type == null) continue;

                if (type.startsWith("ball")) {
                
                    percepts.add(Literal.parseLiteral(String.format("see_ball(%f, %f)", dist, dir)));
                    ballFound = true;
                } else if (type.startsWith("flag")) {
                    if (side == 'l' && obj.m_type.equals("flag p l c") || (side == 'r' && obj.m_type.equals("flag p r c"))) {
                        percepts.add(Literal.parseLiteral(String.format("see_my_flag(\"%s\", %f, %f)", type, dist, dir)));
                        flagFound = true;
                    }else{flagFound = false;}
                    

                } else if (type.startsWith("goal")) {
                    
                    percepts.add(Literal.parseLiteral(String.format("see_goal(\"%s\", %f, %f)", type, dist, dir)));
                    goalFound = true;
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
            }
            
            return percepts;
    }


}

