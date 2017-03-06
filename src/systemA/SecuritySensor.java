/******************************************************************************************************************
 * File:SecuritySensor.java
 * Course: 17655
 * Project: Assignment A2
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 *   1.0 March 2017 - Initial version (Junyuan Zhang, Tianbing Leng).
 *
 * Description:
 *
 * This class simulates a security sensor. It polls the message manager for messages corresponding to changes in state
 * of the security and reacts to them by getting input from the user text console (simulator). The current
 * relative status of the security is posted to the message manager.
 *
 *
 ******************************************************************************************************************/
package systemA;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class SecuritySensor extends Thread {
    private MessageManagerInterface em = null; // Interface object to the
                                               // message manager
    private String MsgMgrIP = null; // Message Manager IP address
    private boolean Registered = true;

    MessageWindow messageWindow = null; // This is the message window
    private boolean windowBreak = false; // window break
    private boolean doorBreak = false; // door break
    private boolean motionDetected = false; // motion detected
    private final int MESSAGEID = 6; // message ID sent outs
    private static final int SECURITY_EXIT_CODE = 98; // message ID to exit the
                                                      // program

    /***************************************************************************
     * CONSTRUCTOR:: SecuritySensor() Purpose: This method registers
     * participants with the message manager without a specified IP address.
     * This instantiation is used when the MessageManager is on a local machine.
     * 
     * Arguments: None.
     * 
     * Exceptions: LocatingMessageManagerException, RegistrationException,
     * ParticipantAlreadyRegisteredException
     * 
     ****************************************************************************/
    public SecuritySensor() {
        // message manager is on the local system
        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is on the local machine
            em = new MessageManagerInterface();
        } catch (Exception e) {
            System.out
                    .println("SecuritySensor::Error instantiating message manager interface: "
                            + e);
            Registered = false;
        }
    }

    /***************************************************************************
     * CONSTRUCTOR:: SecuritySensor( String IPAddress )
     * 
     * Purpose: This method registers participants with the message manager at a
     * specified IP address. This instantiation is used when the MessageManager
     * is not on a local machine.
     * 
     * Arguments: MsgIpAddress.
     * 
     * Exceptions: LocatingMessageManagerException, RegistrationException,
     * ParticipantAlreadyRegisteredException
     * 
     ****************************************************************************/
    public SecuritySensor(String MsgIpAddress) {
        // message manager is not on the local system
        MsgMgrIP = MsgIpAddress;
        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is NOT on the local machine
            em = new MessageManagerInterface(MsgMgrIP);
        } catch (Exception e) {
            System.out
                    .println("SecuritySensor::Error instantiating message manager interface: "
                            + e);
            Registered = false;
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: initializeWindow()
     * 
     * Purpose: This method initializes the security sensor console window.
     * 
     ****************************************************************************/
    private void initializeWindow() {
        messageWindow = new MessageWindow("Security Sensor Console", 0, 0);
        messageWindow.WriteMessage("Registered with the message manager.");

        try {
            messageWindow.WriteMessage("   Participant id: " + em.GetMyId());
            messageWindow.WriteMessage("   Registration Time: "
                    + em.GetRegistrationTime());
        } catch (Exception e) {
            System.out.println("Error:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: reportWindowStatus()
     * 
     * Purpose: This method reports the status to the message manager when
     * there's message related to the window.
     * 
     ****************************************************************************/
    private void reportWindowStatus() {
        Message msg;
        if (windowBreak)
            msg = new Message(MESSAGEID, "W");
        else
            msg = new Message(MESSAGEID, "WC");

        try {
            em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error report window break:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: reportDoorStatus()
     * 
     * Purpose: This method reports the status to the message manager when
     * there's message related to the door.
     * 
     ****************************************************************************/
    private void reportDoorStatus() {
        Message msg;
        if (doorBreak)
            msg = new Message(MESSAGEID, "D");
        else
            msg = new Message(MESSAGEID, "DC");

        try {
            em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error report door break:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: reportMotionStatus()
     * 
     * Purpose: This method reports the status to the message manager when
     * there's message related to the motion detection.
     * 
     ****************************************************************************/
    private void reportMotionStatus() {
        Message msg;
        if (motionDetected)
            msg = new Message(MESSAGEID, "M");
        else
            msg = new Message(MESSAGEID, "MC");

        try {
            em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error report motion detected:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: reportWorkNormally()
     * 
     * Purpose: This method reports the status to the message manager when
     * there's message showing all are fine.
     * 
     ****************************************************************************/
    private void reportWorkNormally() {
        Message msg = new Message(MESSAGEID, "F");

        try {
            em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error work normally:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: windowBreak()
     * 
     * Purpose: This method will change the window status and start to report.
     * 
     ****************************************************************************/
    public void windowBreak() {
        windowBreak = true;
        messageWindow.WriteMessage("Window break");
        reportWindowStatus();
    }

    /***************************************************************************
     * CONCRETE METHOD:: doorBreak()
     * 
     * Purpose: This method will change the door status and start to report.
     * 
     ****************************************************************************/
    public void doorBreak() {
        doorBreak = true;
        messageWindow.WriteMessage("Door break");
        reportDoorStatus();
    }

    /***************************************************************************
     * CONCRETE METHOD:: motionDetected()
     * 
     * Purpose: This method will change the motion detection status and start to
     * report.
     * 
     ****************************************************************************/
    public void motionDetected() {
        motionDetected = true;
        messageWindow.WriteMessage("Motion detected");
        reportMotionStatus();
    }

    /***************************************************************************
     * CONCRETE METHOD:: windowBreakCancel()
     * 
     * Purpose: This method will change BACK the window status and start to
     * report.
     * 
     ****************************************************************************/
    public void windowBreakCancel() {
        windowBreak = false;
        messageWindow.WriteMessage("Window break cancel");
        reportWindowStatus();
    }

    /***************************************************************************
     * CONCRETE METHOD:: doorBreakCancel()
     * 
     * Purpose: This method will change BACK the door status and start to
     * report.
     * 
     ****************************************************************************/
    public void doorBreakCancel() {
        doorBreak = false;
        messageWindow.WriteMessage("Door break cancel");
        reportDoorStatus();
    }

    /***************************************************************************
     * CONCRETE METHOD:: motionDetectedCancel()
     * 
     * Purpose: This method will change BACK the motion detection status and
     * start to report.
     * 
     ****************************************************************************/
    public void motionDetectedCancel() {
        motionDetected = false;
        messageWindow.WriteMessage("Motion detected cancel");
        reportMotionStatus();
    }

    public void run() {
        Message Msg = null; // Message object
        MessageQueue eq = null; // Message Queue
        int Delay = 2500; // The loop delay (1 second)
        boolean Done = false;
        StatusSender ss = new StatusSender("SS1",
                "This is Security Sensor One.");

        if (em != null) {
            // start to create a security sensor console
            initializeWindow();
            while (!Done) {
                // keep send status message MaintenanceMonitor through the
                // message manager interface
                ss.PostState(em);

                try {
                    eq = em.GetMessageQueue();
                } catch (Exception e) {
                    messageWindow.WriteMessage("Error getting message queue::"
                            + e);
                }
                // keep get whether the current security status is need to
                // report or not
                if (windowBreak) {
                    reportWindowStatus();
                    messageWindow.WriteMessage("window break");
                }
                if (doorBreak) {
                    reportDoorStatus();
                    messageWindow.WriteMessage("Door break");
                }
                if (motionDetected) {
                    reportMotionStatus();
                    messageWindow.WriteMessage("Motion detected");
                }
                if (!windowBreak && !doorBreak && !motionDetected) {
                    reportWorkNormally();
                    messageWindow.WriteMessage("Work normally");
                }

                int qlen = eq.GetSize();
                // iterate the message queue one by one
                for (int i = 0; i < qlen; i++) {
                    Msg = eq.GetMessage();
                    // check whether current message is sent from the sensor by
                    // the messageID
                    // if monitor get action to exit
                    if (Msg.GetMessageId() == SECURITY_EXIT_CODE) {
                        Done = true;
                        try {
                            em.UnRegister();
                        } catch (Exception e) {
                            messageWindow.WriteMessage("Error unregistering: "
                                    + e);
                        }
                        messageWindow
                                .WriteMessage("\n\nSimulation Stopped. \n");

                    }
                }

                try {
                    Thread.sleep(Delay);
                } catch (Exception e) {
                    System.out.println("Sleep error:: " + e);
                }

            }

        } else {
            System.out
                    .println("Unable to register with the message manager.\n\n");
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: IsRegistered Purpose: This method returns the
     * registered status
     * 
     * Arguments: none
     * 
     * Returns: boolean true if registered, false if not registered
     * 
     * Exceptions: None
     * 
     ***************************************************************************/
    public boolean IsRegistered() {
        return (Registered);
    }

}
