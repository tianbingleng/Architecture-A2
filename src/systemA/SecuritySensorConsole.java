/******************************************************************************************************************
 * File:SecuritySensorConsole.java
 * Course: 17655
 * Project: Assignment A2
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 *   1.0 March 2017 - Initial version (Junyuan Zhang, Tianbing Leng).
 *
 * Description: This class is the security sensor console for the museum environmental control system. This process consists of two
 * threads. The SecurityMonitor object is a thread that is started that is responsible for the monitoring and control of
 * the museum security status.
 *
 ******************************************************************************************************************/
package systemA;

import TermioPackage.*;

public class SecuritySensorConsole {
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
        System.out.println("Security Sensor Console: \n");

        if (args.length != 0)
            System.out.println("Using message manger at: " + args[0] + "\n");
        else
            System.out.println("Using local message manger \n");

        while (true) {
            String option = "";
            System.out.println("Select an Option: \n");
            System.out.println("1: Window break");
            System.out.println("2: Cancel window break");
            System.out.println("3: Door break");
            System.out.println("4: Cancel door break");
            System.out.println("5: Motion detected");
            System.out.println("6: Cancel motion detected\n");
            // get the option
            option = userInput.KeyboardReadString();

            if (option.equals("1") || option.equals("2") || option.equals("3")
                    || option.equals("4") || option.equals("5")
                    || option.equals("6"))
                return option;

            System.out.println("Not expected input: " + option);

        }
    }

    public static void main(String args[]) {
        Termio userInput = new Termio(); // Termio IO Object
        boolean done = false; // Main loop flag
        String option = null; // Menu choice from user
        SecuritySensor sensor = null; // The environmental control system
                                      // monitor

        // register the security sensor
        if (args.length != 0) {
            sensor = new SecuritySensor(args[0]);
        } else {
            sensor = new SecuritySensor();
        }
        if (sensor.IsRegistered()) {
            sensor.start();
            while (!done) {
                // check whether the sensor is running or not
                if (sensor.isAlive()) {
                    option = promptUser(args, userInput);
                    // if the sensor no more running
                    if (!sensor.isAlive()) {
                        done = true;
                        break;
                    }
                    // get user's input
                    switch (option) {
                    case "1":
                        sensor.windowBreak();
                        System.out.println("Window break\n");
                        break;
                    case "2":
                        sensor.windowBreakCancel();
                        System.out.println("Cancel window break\n");
                        break;
                    case "3":
                        sensor.doorBreak();
                        System.out.println("Door break\n");
                        break;
                    case "4":
                        sensor.doorBreakCancel();
                        System.out.println("Cancel door break\n");
                        break;
                    case "5":
                        sensor.motionDetected();
                        System.out.println("Motion detected\n");
                        break;
                    case "6":
                        sensor.motionDetectedCancel();
                        System.out.println("Cancel motion detected\n");
                        break;

                    default:
                        System.out.println("Not expected input: " + option);
                    }
                } else
                    done = true;
            }

        } else {
            System.out.println("\n\nUnable start the monitor.\n\n");
        }

        System.out.println("Sensor console stopped");

    }

}
