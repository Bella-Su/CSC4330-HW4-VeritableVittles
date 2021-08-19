//====================================================================
//
// Application: Veritable Vittles
// Activity:    Shared
// Description:
//   This Android application allows customers to submit a dinner reservation.
//
//====================================================================

package com.example.veritablevittles;

//--------------------------------------------------------------------
// enum Shared
//--------------------------------------------------------------------
public enum Shared
{
    //Define enum value
    Data;

    //Declare enum fields
    public String sharedPartyName;
    public String sharedNumberOfPeople;
    public String sharedSeatingType;
    public String sharedOccasion;
    public int sharedWaitingTime;
    public String sharedWaitingTimeMessage1 = "Your wait time is ";
    public String sharedWaitingTimeMessage2 = " minutes.";
    public String getSharedWaitingTimeMessage3 = " minute.";
    public String sharedWaitingTimeMessage;
}
