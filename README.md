# Halifax Bus Transit 
A Maps application that displays the live stream bus data in Halifax with the help of markers

# Demo
<p>The .apk file is attached to the repository. Sample images are attached here!</p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<b> App Logo </b> <br> 
<a href="https://imgbb.com/"><img src="https://i.ibb.co/ZWjMDr2/app-logo-icon.png" alt="app-logo-icon" border="0" height="300" width="180"></a> <br>
<a href="https://ibb.co/jyMmVYt"><img src="https://i.ibb.co/PchKTyk/Screenshot-2019-12-17-20-24-09.png" alt="Screenshot-2019-12-17-20-24-09" border="0" height="300" width="180"></a>
<a href="https://ibb.co/QPL0dML"><img src="https://i.ibb.co/jymtRDm/Screenshot-2019-12-17-20-24-21.png" alt="Screenshot-2019-12-17-20-24-21" border="0" height="300" width="180"></a>
<a href="https://ibb.co/2hyG8mx"><img src="https://i.ibb.co/h9y3Xhb/Screenshot-2019-12-17-20-25-02.png" alt="Screenshot-2019-12-17-20-25-02" border="0" height="300" width="180"></a>
<a href="https://ibb.co/xfd5RTs"><img src="https://i.ibb.co/P90CfHY/Screenshot-2019-12-17-20-24-53.png" alt="Screenshot-2019-12-17-20-24-53" border="0" height="300" width="180"></a>

# Description
Below is the workflow of the application:

1) On launch, a map is displayed, with indicators showing the current positions of all buses.
Until the buses are displayed, a dialog box with "Loading, please wait.." is displayed. (until thread execution is completed)
2) The marker for each bus includes the route number (1,2,9A,9B etc).
3) The positions of the buses are updated approximately every 15 seconds.
4) The user can choose to centre the map on his or her current location with the standard button.
5) The user can zoom and pan the map freely.
6) When returning to the app, the map shows the same region it was showing when the app was paused or closed 
(even if the app is terminated and relaunched).
7) The entire functionality specified above works even when the device screen is rotated.
8) User can choose the bus of choice by clicking yellow filter icon at bottom right of the application. All buses are displayedin 
dropdown along with "SHOW ALL" to display all the buses.
9) On click on bus marker icon, bus "route id" along with bus "delay status" is displayed-
	If delayed, "Delayed by XXX seconds",
	If early, "Early by XXX seconds",
	If on time, "On time"

# Known bugs:
There are no known bugs as of now except that application hangs once in a while.
Also, initially when the app is launched, "Loading" message appears for a while which means its taking more time to execute the thread.



