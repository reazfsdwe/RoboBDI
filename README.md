## 🚀 Project Compilation and Execution Instructions


### 🔨 Compilation

This project uses the **Jason BDI Agent Programming Language, version 3.2.0**.

* **Java Requirement:** Compilation and execution require **at least JAVA 19**.
* **Dependencies:** The source code includes the necessary Jason BDI library (`jason.jar`). Depending on your specific JAVA and Jason BDI setup, the `javax.json-api-1.1.4.jar` dependency might also be required, and it has been included in the project directory for convenience.

To compile the source code, open a terminal in **both** Krislet and oringin_Krislet and execute the following command:

```bash
javac -cp *.java

javac -cp ".;jason.jar" *.java
```

### ⚙️ Execution Instructions

To run the system, follow these steps and execute the required files in the specified sequence:

1.  **Run the Krislet Server**
 
2.  **Run the Monitor**
 
3.  **Enter the Krislet Folder, Run the Agent Start Script (BDI teams VS original Krislet team):**
    ```bash
    teamStart_1.bat
    ```

    **Enter the Krislet Folder, Run the Agent Start Script (BDI teams VS BDI team):**
    ```bash
    teamStart_.bat
    ```

    ### ⚠️ Important Reminder

If both teams simultaneously use logic driven by this BDI system, it may lead to a deadlock situation where the ball remains between players, with no agent actively chasing or attempting to kick the ball. This is because Rambo (the attacker) and defenders are required to remain within close distance to their positions.
