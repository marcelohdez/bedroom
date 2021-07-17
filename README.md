# Bedroom
Bedroom is an open source Java application developed in order to aid call center agents 
in keeping track of their orders and orders per hour to meet their quotas effectively.
Due to the pandemic people have started working remotely, working from the bedroom couldn't
get any easier! Or... maybe it just did.

## Prerequisites
To use the program, install the newest version of the JDK at [https://www.oracle.com/java/technologies/javase-downloads.html]
(under Oracle JDK of the version you chose, select JDK download), the minimum version required is stated on the description 
of the Bedroom release you are trying to run. After doing so, double click the jar file of Bedroom and begin.

To build the program from source, install the newest version of the JDK (read above) and run gradle (read Compile From Source
section if you do not know how to do so.)

## How to use
Upon start up, you will be asked for your clock in and clock out time, on the clock out window there is an extra section to choose
your "hourly target" which is the amount of orders per hour you are aiming for. Once finished the program will start counting the time
left until you clocking in or the time you have been clocked in for if your clock in time has already passed. Your break and orders 
can be changed through the shortcuts below or by clicking the "Set Break" and "Add Order" buttons respectively, although removing
orders can only be done through shortcuts. Orders can only be modified while outside of break and being clocked in, so shortcuts 
for them will only work while so. _You can only go on break once currently, setting new break times will override the current ones._

On the right of the window you will see text with information about your current situation, on the first row there will always be a
time shown, whether it is the time left until clocking in, the time you have been clocked in for, or how much time is left until 
the currently set break ends. On the second row will be shown your current orders with the current orders per hour in parentheses.
Finally, the third row will show how many orders are needed for the day, and how many are left. 

**Tool Tips** are shown when you hover your mouse over the "Set Break" or "Add Order" buttons, and they display extra information:
the tool tip for the "Set Break" button will show you the times selected for the currently set break, while the tooltip for the
"Add Order" button will show how many orders are needed to stay on track with your set hourly target.

### _Shortcuts:_
* _Backspace & Down Arrow_ decrease orders by 1.
* _Up Arrow_ increase orders by 1.
* _0_ Opens window to set break times.

_These shortcuts are meant to be unintrusive to work applications,
hence their seemingly random keyboard placements._

## Compile From Source
_This is not supported as there could be bugs or worse problems with things i am currently experimenting on._

After installing the newest JDK (read Prerequisites on top) download the source code by clicking the green code button on the top
of the page and selecting Download ZIP. Then open the downloaded file and copy the folder inside to anywhere else (like your
desktop) and delete the original zipped file. Finally, open the folder you copied into a new location with Terminal/Command Prompt 
(on Windows this can simply be done by selecting the address bar on top, typing ```cmd```, and pressing Enter) and run ```gradlew build```
