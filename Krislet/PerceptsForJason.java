import jason.asSyntax.Literal;
import java.util.*;

public class PerceptsForJason {

    // For positional precepts (defenders)
    static final int NUM_DEFENDERS = 3;
    static final int FIELD_HEIGHT = 100;
    static final int POSITION_TOLERANCE = 10;

    public List<Literal> getPercepts(VisualInfo info, char side, String playMode) {

        List<Literal> percepts = new ArrayList<>();
        percepts.add(Literal.parseLiteral(String.format("side(%c)", side)));

        // ---------- Referee / play mode based percepts ----------
        if (playMode != null && !playMode.isEmpty()) {
            // RoboCup server: "goal_l" or "goal_r" when a goal is scored
            if (playMode.startsWith("goal_")) {
                // For Rambo: any goal scored (by either side)
                percepts.add(Literal.parseLiteral("goal_scored"));
            }

            // For goalie/defenders: goal AGAINST us
            // LEFT scores (goal_l) -> bad for RIGHT side
            if (playMode.startsWith("goal_l") && side == 'r') {
                percepts.add(Literal.parseLiteral("goal_against"));
            }
            // RIGHT scores (goal_r) -> bad for LEFT side
            if (playMode.startsWith("goal_r") && side == 'l') {
                percepts.add(Literal.parseLiteral("goal_against"));
            }
        }

        if (info == null) {
            percepts.add(Literal.parseLiteral("no_info"));
            return percepts;
        }

        Vector<ObjectInfo> objects = info.m_objects;
        boolean ballFound   = false;
        boolean flagFound   = false;
        boolean goalFound   = false;
        boolean topFound    = false;
        boolean centerFound = false;

        for (ObjectInfo obj : objects) {
            String type = obj.m_type;
            float dist  = obj.m_distance;
            float dir   = obj.m_direction;

            if (type == null) continue;

            // ---------------- Ball ----------------
            if (type.startsWith("ball")) {

                percepts.add(Literal.parseLiteral(
                    String.format("see_ball(%f, %f)", dist, dir)));
                ballFound = true;

            // ---------------- Flags ----------------
            } else if (type.startsWith("flag")) {

                // --- Centre of field flag (Rambo & support behavior) ---
                if (type.equals("flag c") || type.equals("f c")) {
                    percepts.add(Literal.parseLiteral(
                        String.format("see_center_flag(%f, %f)", dist, dir)));
                    centerFound = true;
                }

                // --- Penalty-box flags for our own side (defense positions) ---
                // Left team: "flag p l t/c/b"
                // Right team: "flag p r t/c/b"
                if ((side == 'l' && type.startsWith("flag p l")) ||
                    (side == 'r' && type.startsWith("flag p r"))) {

                    if (type.endsWith("b")) {
                        // bottom edge of penalty box
                        percepts.add(Literal.parseLiteral(
                            String.format("see_defense_bottom_flag(%f, %f)", dist, dir)));
                        flagFound = true;
                    } else if (type.endsWith("c")) {
                        // centre edge of penalty box
                        percepts.add(Literal.parseLiteral(
                            String.format("see_defense_center_flag(%f, %f)", dist, dir)));
                        flagFound = true;

                        // legacy / compatibility: "my flag" at penalty spot centre
                        percepts.add(Literal.parseLiteral(
                            String.format("see_my_flag(\"%s\", %f, %f)", type, dist, dir)));
                        percepts.add(Literal.parseLiteral(
                            String.format("see_my_goal_flag(\"%s\", %f, %f)", type, dist, dir)));
                    } else if (type.endsWith("t")) {
                        // top edge of penalty box
                        percepts.add(Literal.parseLiteral(
                            String.format("see_defense_top_flag(%f, %f)", dist, dir)));
                        flagFound = true;
                    }
                }

            // ---------------- Goals ----------------
            } else if (type.startsWith("goal")) {

                // Raw goal percept with the name, for debugging / general use
                percepts.add(Literal.parseLiteral(
                    String.format("see_goal(\"%s\", %f, %f)", type, dist, dir)));
                goalFound = true;

                // Own goal object for goalie: "goal l" if side=l, "goal r" if side=r
                if ((side == 'l' && type.equals("goal l")) ||
                    (side == 'r' && type.equals("goal r"))) {

                    percepts.add(Literal.parseLiteral(
                        String.format("goalie_see_own_goal(%f, %f)", dist, dir)));
                }

                // Enemy/opponent goal (for attackers / Rambo / defenders)
                if ((side == 'l' && type.equals("goal r")) ||
                    (side == 'r' && type.equals("goal l"))) {

                    // Generic enemy-goal percept (used by some roles)
                    percepts.add(Literal.parseLiteral(
                        String.format("see_enemy_goal(%f, %f)", dist, dir)));

                    // Rambo-specific naming
                    percepts.add(Literal.parseLiteral(
                        String.format("see_opponent_goal(%f, %f)", dist, dir)));
                }

            // ---------------- Top line (for defenders positioning) ----------------
            } else if (type.startsWith("line")) {

                LineInfo line = (LineInfo) obj;
                char kind = line.m_kind;

                if (kind == 't') {
                    percepts.add(Literal.parseLiteral(
                        String.format("see_topline(%f, %f)", dist, dir)));
                    topFound = true;
                    computePositionalPrecepts(dist, percepts);
                }
            }

            // Optional debugging:
            // System.out.println("[PerceptsForJason] side=" + side + " percepts now: " + percepts);
        }

        // We don't strictly need a "center_lost" percept; Jason can use not see_center_flag(_, _)
        // if (!centerFound) { ... }

        if (!flagFound) {
            percepts.add(Literal.parseLiteral("flag_lost"));
        }
        if (!goalFound) {
            percepts.add(Literal.parseLiteral("goal_lost"));
        }
        if (!ballFound) {
            percepts.add(Literal.parseLiteral("ball_lost"));
        }
        if (!topFound) {
            percepts.add(Literal.parseLiteral("align_info_lost"));
        }

        return percepts;
    }

    private void computePositionalPrecepts(float dist, List<Literal> percepts) {

        if (dist < (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) - POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("less_quarter"));
            percepts.add(Literal.parseLiteral("less_two_quarter"));
            percepts.add(Literal.parseLiteral("less_three_quarter"));
        } else if (dist < (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) + POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("less_two_quarter"));
            percepts.add(Literal.parseLiteral("less_three_quarter"));
        } else if (dist < 2 * (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) - POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("above_quarter"));
            percepts.add(Literal.parseLiteral("less_two_quarter"));
            percepts.add(Literal.parseLiteral("less_three_quarter"));
        } else if (dist < 2 * (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) + POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("above_quarter"));
            percepts.add(Literal.parseLiteral("less_three_quarter"));
        } else if (dist < 3 * (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) - POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("above_quarter"));
            percepts.add(Literal.parseLiteral("above_two_quarter"));
            percepts.add(Literal.parseLiteral("less_three_quarter"));
        } else if (dist < 3 * (FIELD_HEIGHT / (NUM_DEFENDERS + 1)) + POSITION_TOLERANCE) {
            percepts.add(Literal.parseLiteral("above_quarter"));
            percepts.add(Literal.parseLiteral("above_two_quarter"));
        } else {
            percepts.add(Literal.parseLiteral("above_quarter"));
            percepts.add(Literal.parseLiteral("above_two_quarter"));
            percepts.add(Literal.parseLiteral("above_three_quarter"));
        }
    }
}
