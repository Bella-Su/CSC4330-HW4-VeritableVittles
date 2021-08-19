//====================================================================
//
// Class: Task
// Description:
//   This Android class represents a timer task that runs once per
// second, updates the shared data, and signals that the main screen
// should update the screen.  The updates include:
//   -The wait time.
//   -Spoken message.
//
//====================================================================
package com.example.veritablevittles;

import java.util.Random;
import java.util.TimerTask;

//--------------------------------------------------------------------
// class Task
//--------------------------------------------------------------------
public class Task extends TimerTask
{
    //----------------------------------------------------------------
    // run
    //----------------------------------------------------------------
    public void run()
    {
        //generate a subtract waiting time
        Random rand = new Random();
        int subtractTime = rand.nextInt((3-1)+1) + 1;

        //Update shared value
        Shared.Data.sharedWaitingTime = Shared.Data.sharedWaitingTime -  subtractTime ;
        if(Shared.Data.sharedWaitingTime < 0)
            Shared.Data.sharedWaitingTime = 0;

        //Spoken message
        if(Shared.Data.sharedWaitingTime ==1)
            Shared.Data.sharedWaitingTimeMessage = Shared.Data.sharedWaitingTimeMessage1 + Shared.Data.sharedWaitingTime + Shared.Data.getSharedWaitingTimeMessage3;
        else
            Shared.Data.sharedWaitingTimeMessage = Shared.Data.sharedWaitingTimeMessage1 + Shared.Data.sharedWaitingTime + Shared.Data.sharedWaitingTimeMessage2;

        //Test if message set
        if(ActMain.timerTaskHandler.sendEmptyMessage(0))
            System.out.println("[Task] Timer task " + "message sent to main thread.");
        else
            System.out.println("[Task] Error: " + "timer task message NOT sent to main thread.");

    }
}
