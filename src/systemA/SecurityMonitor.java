/******************************************************************************************************************
 * File:SecurityMonitor.java
 * Course: 17655
 * Project: Assignment A2
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 *   1.0 March 2017 - Initial version (Junyuan Zhang, Tianbing Leng).
 *
 * Description: This class is the security monitor console which shows the current security status of the museum, 
 * such as whether the museum security functionality is armed or disarmed.
 *
 *
 ******************************************************************************************************************/
package systemA;

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class SecurityMonitor extends Thread {
    private MessageManagerInterface em = null; // Interface object to the
                                               // message manager
    private String MsgMgrIP = null; // Message Manager IP address
    private boolean Registered = true;

    MessageWindow messageWindow = null; // This is the message window
    Indicator windowIndicator; // Window indicator
    Indicator doorIndicator; // Door indicator
    Indicator motionIndicator; // Motion indicator
    private static final int messageID = 6; // Receive from sensor
    private boolean armed = true; // Is armed?
    private static final int SECURITY_EXIT_CODE = 98; // MessageID for exit the
                                                      // program
    private static final int SEND_MESSAGE_ID = 7; // MessageID sending to
                                                  // security controller

    /***************************************************************************
     * CONSTRUCTOR:: SecurityMonitor() Purpose: This method registers
     * participants with the message manager without a specified IP address.
     * This instantiation is used when the MessageManager is on a local machine.
     * 
     * Arguments: None.
     * 
     * Exceptions: LocatingMessageManagerException, RegistrationException,
     * ParticipantAlreadyRegisteredException
     * 
     ****************************************************************************/
    public SecurityMonitor() {
        // message manager is on the local system
        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is on the local machine
            em = new MessageManagerInterface();
        } catch (Exception e) {
            System.out
                    .println("SecurityMonitor::Error instantiating message manager interface: "
                            + e);
            Registered = false;
        }
    }

    /***************************************************************************
     * CONSTRUCTOR:: SecurityMonitor( String IPAddress )
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
    public SecurityMonitor(String MsgIpAddress) {
        // message manager is not on the local system
        MsgMgrIP = MsgIpAddress;
        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is NOT on the local machine
            em = new MessageManagerInterface(MsgMgrIP);
        } catch (Exception e) {
            System.out
                    .println("SecurityMonitor::Error instantiating message manager interface: "
                            + e);
            Registered = false;
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: initializeWindow
     * 
     * Purpose: This method to create a Security Monitoring Console to showing
     * the system status. For example, is the system is armed or disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    private void initializeWindow() {
        messageWindow = new MessageWindow("Security Monitoring Console", 0, 0);
        // GUI component indicating window security status
        windowIndicator = new Indicator("Window", messageWindow.GetX()
                + messageWindow.Width(), 0, 1);
        // GUI component indicating door security status
        doorIndicator = new Indicator("Door", messageWindow.GetX()
                + messageWindow.Width(), (int) (messageWindow.Height() / 2), 1);
        // GUI component indicating motion security status
        motionIndicator = new Indicator("Motion", messageWindow.GetX()
                + messageWindow.Width(), (int) (messageWindow.Height()), 1);

        messageWindow.WriteMessage("Registered with the message manager.");

        // write the registration result into the security monitoring console
        try {
            messageWindow.WriteMessage("   Participant id: " + em.GetMyId());
            messageWindow.WriteMessage("   Registration Time: "
                    + em.GetRegistrationTime());
        } catch (Exception e) {
            System.out.println("Error:: " + e);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: windowBreak
     * 
     * Purpose: This method to set the Indicator to Red when the alarm is armed,
     * to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void windowBreak() {
        if (armed)
            windowIndicator.SetLampColorAndMessage("WINDOW BREAK", 3);
        else
            windowIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: windowBreakCancel
     * 
     * Purpose: This method to set back the Indicator to Green when the alarm is
     * armed, to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void windowBreakCancel() {
        if (armed)
            windowIndicator.SetLampColorAndMessage("WINDOW SAFE", 1);
        else
            windowIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: doorBreak
     * 
     * Purpose: This method to set the Indicator to Red when the alarm is armed,
     * to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void doorBreak() {
        if (armed)
            doorIndicator.SetLampColorAndMessage("DOOR BREAK", 3);
        else
            doorIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: doorBreakCancel
     * 
     * Purpose: This method to set back the Indicator to Green when the alarm is
     * armed, to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void doorBreakCancel() {
        if (armed)
            doorIndicator.SetLampColorAndMessage("DOOR SAFE", 1);
        else
            doorIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: motionDetection
     * 
     * Purpose: This method to set the Indicator to Red when the alarm is armed,
     * to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void motionDetection() {
        if (armed)
            motionIndicator.SetLampColorAndMessage("MOTION DETECTED", 3);
        else
            motionIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: motionDetectionCancel
     * 
     * Purpose: This method to set back the Indicator to Green when the alarm is
     * armed, to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void motionDetectionCancel() {
        if (armed)
            motionIndicator.SetLampColorAndMessage("MOTION SAFE", 1);
        else
            motionIndicator.SetLampColorAndMessage("DISARMED", 0);
    }

    /***************************************************************************
     * CONCRETE METHOD:: workNormally
     * 
     * Purpose: This method to set back all the Indicators to Green when the
     * alarm is armed, to Black when the alarm is disarmed.
     * 
     * Arguments: None.
     * 
     ***************************************************************************/
    public void workNormally() {
        if (armed) {
            windowIndicator.SetLampColorAndMessage("WINDOW SAFE", 1);
            doorIndicator.SetLampColorAndMessage("DOOR SAFE", 1);
            motionIndicator.SetLampColorAndMessage("MOTION SAFE", 1);
        } else {
            windowIndicator.SetLampColorAndMessage("DISARMED", 0);
            doorIndicator.SetLampColorAndMessage("DISARMED", 0);
            motionIndicator.SetLampColorAndMessage("DISARMED", 0);
        }
    }

    // monitor thread
    public void run() {
        Message Msg = null; // Message object
        MessageQueue eq = null; // Message Queue
        int Delay = 1000; // The loop delay (1 second)
        boolean Done = false;
        // initiate the StatusSender object to send device info to the
        // MaintenanceMonitor
        StatusSender ss = new StatusSender("SM1",
                "This is Security Monitor One.");

        if (em != null) {
            // create the security monitor console
            initializeWindow();
            // keep running when the system is not closed
            while (!Done) {
                // keep send status message MaintenanceMonitor through the
                // message manager interface
                ss.PostState(em);

                // get the message from the mq
                try {
                    eq = em.GetMessageQueue();
                } catch (Exception e) {
                    messageWindow.WriteMessage("Error getting message queue::"
                            + e);
                }

                int qlen = eq.GetSize();
                // iterate the message queue one by one
                for (int i = 0; i < qlen; i++) {
                    Msg = eq.GetMessage();
                    // check whether current message is sent from the sensor by
                    // the messageID
                    if (Msg.GetMessageId() == messageID) {
                        // retrieve the message context to recognize the type of
                        // emergency
                        switch (Msg.GetMessage()) {
                        // window was break
                        case "W":
                            windowBreak();
                            break;
                        // window was back to normal from break
                        case "WC":
                            windowBreakCancel();
                            break;
                        // door was break
                        case "D":
                            doorBreak();
                            break;
                        // door was back to normal from break
                        case "DC":
                            doorBreakCancel();
                            break;
                        // motion was detected
                        case "M":
                            motionDetection();
                            break;
                        // motion was back to normal from detected
                        case "MC":
                            motionDetectionCancel();
                            break;
                        // everything working fine
                        case "F":
                            workNormally();
                            break;
                        default:
                            System.out.println("Unknown message "
                                    + Msg.GetMessage());
                        }
                    }// if monitor get action to exit
                    else if (Msg.GetMessageId() == SECURITY_EXIT_CODE) {
                        Done = true;
                        // unregister the participant
                        try {
                            em.UnRegister();
                        } catch (Exception e) {
                            messageWindow.WriteMessage("Error unregistering: "
                                    + e);
                        }
                        messageWindow
                                .WriteMessage("\n\nSimulation Stopped. \n");

                        // Get rid of the indicators.
                        windowIndicator.dispose();
                        doorIndicator.dispose();
                        motionIndicator.dispose();
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
     * CONCRETE METHOD:: Halt Purpose: This method posts an message that stops
     * the environmental control system.
     * 
     * Arguments: none
     * 
     * Returns: none
     * 
     * Exceptions: Posting to message manager exception
     * 
     ***************************************************************************/

    public void Halt() {
        messageWindow
                .WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

        Message msg;
        msg = new Message(SECURITY_EXIT_CODE, "XXX");

        try {
            em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
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

    public void disarm() {
        armed = false;
        messageWindow.WriteMessage("System is disarmed");
        Message msg;
        msg = new Message(SEND_MESSAGE_ID, "D");
        try {
            em.SendMessage(msg);
            // messageWindow.WriteMessage("System is disarmed- sent");
        } catch (Exception e) {
            System.out.println("Error sending disarm message:: " + e);
        }
    }

    public void arm() {
        armed = true;
        messageWindow.WriteMessage("System is armed");
        Message msg;
        msg = new Message(SEND_MESSAGE_ID, "A");
        try {
            em.SendMessage(msg);
            // messageWindow.WriteMessage("System is armed- sent");
        } catch (Exception e) {
            System.out.println("Error sending arm message:: " + e);
        }
    }

}
