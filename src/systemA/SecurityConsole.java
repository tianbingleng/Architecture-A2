/******************************************************************************************************************
 * File:SecurityConsole.java
 * Course: 17655
 * Project: Assignment A2
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 *   1.0 March 2017 - Initial version (Junyuan Zhang, Tianbing Leng).
 *
 * Description: This class is the security console for the museum environmental control system. This process consists of two
 * threads. The SecurityMonitor object is a thread that is started that is responsible for the monitoring and control of
 * the museum security status. The main thread provides a text interface for the user to enable/disable the alarm, 
 * as well as shut down the system.
 *
 *
 ******************************************************************************************************************/
package systemA;

import TermioPackage.*;

public class SecurityConsole {
    private static boolean armed = true; // alarm status

    /***************************************************************************
     * CONCRETE METHOD:: promptUser
     * 
     * Purpose: This method to get user's input.
     * 
     * Arguments: args[], userInput
     * 
     * Returns: String
     * 
     ***************************************************************************/
    private static String promptUser(String args[], Termio userInput) {
        System.out.println("\n\n\n\n");
        System.out.println("Security Control System Command Console: \n");

        if (args.length != 0)
            System.out.println("Using message manger at: " + args[0] + "\n");
        else
            System.out.println("Using local message manger \n");

        // wait for user's input
        while (true) {
            String option = "";
            // if current system is armed
            if (armed) {
                System.out.println("System is armed\n");
                System.out.println("Select an Option: \n");
                System.out.println("D: Disable alarm");
                System.out.println("X: Stop System\n");
                System.out.print("\n>>>> ");
                // get the option
                option = userInput.KeyboardReadString();
                if (option.equals("D") || option.equals("X"))
                    return option;
            }
            // if current system is disarmed
            else {
                System.out.println("System is disarmed\n");
                System.out.println("Select an Option: \n");
                System.out.println("E: Enable alarm");
                System.out.println("X: Stop System\n");
                System.out.print("\n>>>> ");
                option = userInput.KeyboardReadString();
                if (option.equals("E") || option.equals("X"))
                    return option;
            }

            System.out.println("Not expected input: " + option);
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: disableAlarm
     * 
     * Purpose: This method to notify disable action into monitor.
     * 
     * Arguments: monitor
     * 
     * Returns: none
     * 
     ***************************************************************************/
    public static void disableAlarm(SecurityMonitor monitor) {
        armed = false;
        monitor.disarm();
    }

    /***************************************************************************
     * CONCRETE METHOD:: enableAlarm
     * 
     * Purpose: This method to notify enable action into monitor.
     * 
     * Arguments: monitor
     * 
     * Returns: none
     * 
     ***************************************************************************/
    public static void enableAlarm(SecurityMonitor monitor) {
        armed = true;
        monitor.arm();
    }

    public static void main(String args[]) {
        Termio userInput = new Termio(); // Termio IO Object
        boolean done = false; // Main loop flag
        String option = null; // Menu choice from user
        SecurityMonitor monitor = null; // The environmental control system
                                        // monitor
        // with IP address
        if (args.length != 0) {
            monitor = new SecurityMonitor(args[0]);
        } // without IP address
        else {
            monitor = new SecurityMonitor();
        }

        if (monitor.IsRegistered()) {
            monitor.start(); // Separated thread for security monitor
            // the system will be keep running if done == false
            while (!done) {
                // get user's option
                option = promptUser(args, userInput);

                switch (option) {
                case "D":
                    disableAlarm(monitor);
                    break;
                case "E":
                    enableAlarm(monitor);
                    break;
                case "X":
                    monitor.Halt();
                    done = true; // flip the flag to true, means the system is
                                 // closed.
                    System.out
                            .println("\nConsole Stopped... Exit monitor mindow to return to command prompt.");
                    break;
                default:
                    System.out.println("Not expected input: " + option);
                }
            }

        } else {
            System.out.println("\n\nUnable start the monitor.\n\n");
        }

    }

}
