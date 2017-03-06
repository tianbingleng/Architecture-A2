/******************************************************************************************************************
 * File:SecurityController.java
 * Course: 17655
 * Project: Assignment A2
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 *   1.0 March 2017 - Initial version (Junyuan Zhang, Tianbing Leng).
 *
 *Description
 * This class simulates a device that controls the alarm to armed or disarmed. It polls the message manager for message
 * id = 7 and reacts to them by enable/disable the alarm. The following command are valid
 * strings for controlling the alarm:
 *
 *   A = Enable the alarm (armed)
 *   D = Disable the alarm (disarmed)
 *
 *
 ******************************************************************************************************************/
package systemA;

import InstrumentationPackage.*;
import MessagePackage.*;

class SecurityController {

    static MessageManagerInterface em = null; // Interface object to the message
                                              // manager
    static Indicator securityArmIndicator; // Security Arm Indicator
    static MessageWindow mw; // Controller status window

    private static final int SECURITY_EXIT_CODE = 98; // exit message ID
    private static final int RECEIVE_MESSAGE_ID = 7; // message ID get from
                                                     // monitor

    public static void main(String args[]) {
        Message Msg = null; // Message object
        MessageQueue eq = null; // Message Queue
        int Delay = 2500; // The loop delay (2.5 seconds)
        boolean Done = false; // Loop termination flag
        StatusSender ss = new StatusSender("SC1",
                "This is Security Controller One.");

        // register the controller
        initialize(args);

        // Here we check to see if registration worked. If ef is null then the
        // message manager interface was not properly created.

        if (em != null) {
            System.out.println("Registered with the message manager.");

            // create the message window and indicator
            armWindow();

            /********************************************************************
             ** Here we start the main simulation loop
             *********************************************************************/
            // keep running when the system is not closed
            while (!Done) {
                // keep send status message MaintenanceMonitor through the
                // message manager interface
                ss.PostState(em);
                // get the message from the mq
                try {
                    eq = em.GetMessageQueue();
                }

                catch (Exception e) {
                    mw.WriteMessage("Error getting message queue::" + e);
                }

                int qlen = eq.GetSize();
                // iterate the message queue one by one
                for (int i = 0; i < qlen; i++) {
                    Msg = eq.GetMessage();
                    // check whether current message is sent from the sensor by
                    // the messageID
                    if (Msg.GetMessageId() == RECEIVE_MESSAGE_ID) {
                        if (Msg.GetMessage().equalsIgnoreCase("A")) // Arm
                        {
                            mw.WriteMessage("Received arm message");
                            securityArmIndicator.SetLampColorAndMessage("ARM",
                                    1);
                        }
                        if (Msg.GetMessage().equalsIgnoreCase("D")) // Disarm
                        {
                            mw.WriteMessage("Received disarm message");
                            securityArmIndicator.SetLampColorAndMessage(
                                    "DISARM", 0);
                        }
                    }
                    // if monitor get action to exit
                    if (Msg.GetMessageId() == SECURITY_EXIT_CODE) {
                        Done = true;
                        try {
                            em.UnRegister();

                        }

                        catch (Exception e) {
                            mw.WriteMessage("Error unregistering: " + e);

                        }

                        mw.WriteMessage("\n\nSimulation Stopped. \n");

                        // Get rid of the indicators.
                        securityArmIndicator.dispose();

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
     * CONCRETE METHOD:: armWindow() Purpose: This method initilates a message
     * window as well as an alarm indicator.
     * 
     * Arguments: None.
     * 
     * Exceptions: LocatingMessageManagerException, RegistrationException,
     * ParticipantAlreadyRegisteredException
     * 
     ****************************************************************************/
    private static void armWindow() {
        /*
         * Now we create the temperature control status and message panel* We
         * put this panel about 1/3 the way down the terminal, aligned to the
         * left* of the terminal. The status indicators are placed directly
         * under this panel
         */

        float WinPosX = 0.0f; // This is the X position of the message
                              // window in terms
                              // of a percentage of the screen height
        float WinPosY = 0.3f; // This is the Y position of the message
                              // window in terms
                              // of a percentage of the screen height

        mw = new MessageWindow("Security Controller Status Console", WinPosX,
                WinPosY);

        // Put the status indicators under the panel...

        securityArmIndicator = new Indicator("ARM", mw.GetX(), mw.GetY()
                + mw.Height(), 1);

        mw.WriteMessage("Registered with the message manager.");

        try {
            mw.WriteMessage("   Participant id: " + em.GetMyId());
            mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime());

        } // try

        catch (Exception e) {
            System.out.println("Error:: " + e);

        } // catch
    }

    /***************************************************************************
     * CONCRETE METHOD:: initialize() Purpose: This method registers
     * participants with the message manager.
     * 
     * Arguments: args.
     * 
     * Exceptions: LocatingMessageManagerException, RegistrationException,
     * ParticipantAlreadyRegisteredException
     * 
     ****************************************************************************/
    private static void initialize(String[] args) {
        if (args.length == 0) {
            // message manager is on the local system
            System.out
                    .println("\n\nAttempting to register on the local machine...");

            try {
                // Here we create an message manager interface object. This
                // assumes
                // that the message manager is on the local machine

                em = new MessageManagerInterface();
            } catch (Exception e) {
                System.out
                        .println("Error instantiating message manager interface: "
                                + e);

            } // catch

        } else {

            // message manager is not on the local system
            // ///////////////////////////////////////////////////////////////////////////////
            // Get the IP address of the message manager
            // ///////////////////////////////////////////////////////////////////////////////
            String MsgMgrIP = args[0];

            System.out.println("\n\nAttempting to register on the machine:: "
                    + MsgMgrIP);

            try {
                // Here we create an message manager interface object. This
                // assumes
                // that the message manager is NOT on the local machine

                em = new MessageManagerInterface(MsgMgrIP);
            }

            catch (Exception e) {
                System.out
                        .println("Error instantiating message manager interface: "
                                + e);
            } // catch
        }
    }

} // TemperatureController