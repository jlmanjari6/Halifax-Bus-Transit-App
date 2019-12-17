# Halifax Bus Transit 
A Maps application that displays the live stream bus data in Halifax with the help of markers


# Description
This application meets the following requirements:

1) On launch, a map is displayed, with indicators showing the current positions of all buses.
Until the buses are displayed, a dialog box with "Loading, please wait.." is displayed. (until thread execution is completed)
2) The marker for each bus includes the route number (1,2,9A,9B etc).
3) The positions of the buses are updated approximately every 15 seconds.
4) The user can choose to centre the map on his or her current location with the standard button.
5) The user can zoom and pan the map freely.
6) When returning to the app, the map shows the same region it was showing when the app was paused or closed 
(even if the app is terminated and relaunched).
7) The entire functionality specified above works even when the device screen is rotated.

# Exceptional:
1) User can choose the bus of choice by clicking yellow filter icon at bottom right of the application. All buses are displayedin 
dropdown along with "SHOW ALL" to display all the buses.
2) On click on bus marker icon, bus "route id" along with bus "delay status" is displayed-
	If delayed, "Delayed by XXX seconds",
	If early, "Early by XXX seconds",
	If on time, "On time"

# Known bugs:
There are no known bugs as of now except that application hangs once in a while.
Also, initially when the app is launched, "Loading" message appears for a while which means its taking more time to execute the thread.

# References:
[1] Dal.brightspace.com, 2019. [Online]. Available: https://dal.brightspace.com/d2l/le/content/97458/viewContent/1347182/View. [Accessed: 15- Jul- 2019].

[2] "MobilityData/gtfs-realtime-bindings", GitHub, 2019. [Online]. Available: https://github.com/MobilityData/gtfs-realtime-bindings. [Accessed: 15- Jul- 2019].

[3] "Get Started  |  Maps SDK for Android  |  Google Developers", Google Developers, 2019. [Online]. Available: https://developers.google.com/maps/documentation/android-sdk/start. [Accessed: 15- Jul- 2019].




