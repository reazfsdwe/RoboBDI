//
//    File:           Memory.java
//    Author:         Krzysztof Langner (updated by Vladimir’s team)
//    Date:           1997/04/28
//

class Memory {

    //---------------------------------------------------------------------------
    // Constructor initializes all memory fields
    public Memory() {
        m_info    = null;
        playMode  = "";   // track latest referee play mode (goal_l, kick_off_l, etc.)
    }

    //---------------------------------------------------------------------------
    // Store visual information (called by Brain.see())
    public void store(VisualInfo info) {
        m_info = info;
    }

    //---------------------------------------------------------------------------
    // Store play mode (called by Brain.hear())
    public void setPlayMode(String pm) {
        this.playMode = pm;
    }

    //---------------------------------------------------------------------------
    // Retrieve current play mode (used by JasonAgentWrapper -> PerceptsForJason)
    public String getPlayMode() {
        return this.playMode;
    }

    //---------------------------------------------------------------------------
    // This function looks for specified object
    public ObjectInfo getObject(String name) {
        if (m_info == null)
            waitForNewInfo();

        for (int c = 0; c < m_info.m_objects.size(); c++) {
            ObjectInfo object = (ObjectInfo) m_info.m_objects.elementAt(c);
            if (object.m_type.compareTo(name) == 0)
                return object;
        }

        return null;
    }

    //---------------------------------------------------------------------------
    // This function waits for new visual information
    public void waitForNewInfo() {
        // first remove old info
        m_info = null;
        // now wait until we get new copy
        while (m_info == null) {
            // We can get information faster then 75 milliseconds
            try {
                Thread.sleep(SIMULATOR_STEP);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public VisualInfo getVisualInfo() {
        return m_info;
    }

    //===========================================================================
    // Private members
    volatile private VisualInfo m_info;   // place where all information is stored
    volatile private String     playMode; // latest play mode from server

    final static int SIMULATOR_STEP = 100;
}
