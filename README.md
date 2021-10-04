# Bedroom
Due to the pandemic people have started working remotely, _working from the bedroom couldn't get any easier!_
Or... maybe it just did. Bedroom is an open source Java application developed in order to aid call center 
agents in keeping track of their orders and orders per hour to meet their quotas effectively.

This was my first Java program as a self taught high school student, so it has grown and improved as i 
improved but there are definitely still some ugly parts of the code. It was originally created for me and my 
friends working at a call center to keep track of our orders/hr without doing the math ourselves, enabling us 
to share it quicker.

## Table of contents
* [Prerequisites](https://github.com/swiftsatchel/bedroom#prerequisites)
* [How to set up](https://github.com/swiftsatchel/bedroom#how-to-set-up)
* [How to use](https://github.com/swiftsatchel/bedroom#how-to-use)
* [Shortcuts](https://github.com/swiftsatchel/bedroom#shortcuts)
* [Compiling from source](https://github.com/swiftsatchel/bedroom#compiling-from-source)
* [License](https://github.com/swiftsatchel/bedroom#license)

## Prerequisites
**For running and/or compiling the program** the newest version of Java is recommended, while the minimum 
required version is stated under the release you are trying to run. Ex: For Bedroom 3 Java 16+ is required.

### _How to set up_
Download the .jar file from the Releases section, and double click it like any other application.

**A start script** could be used to reduce Bedroom's memory usage if you are into that stuff. For Windows, 
a .bat file can be made in Bedroom's location with the text ```start javaw -Xmx32M -Xms16M -jar filename.jar``` 
(replacing _filename_ with the name of the jar you are trying to run) and then making a shortcut to it wherever 
you like. If you want to keep the command prompt opened for errors, etc. change ```javaw```  to ```java```. The 
argument "-Xmx32M" is the maximum amount of memory Bedroom is allowed to allocate to itself (In this case 32MB) 
while "-Xms16M" is the initial allocation. Actual memory usage may be more due to the JVM, more information can 
be found online on this topic.

## How to use
After opening Bedroom you will be asked for your clock in and out times, if you ever mess up on these dialogs 
you may close them to go to the previous (closing the clock in time dialog will close Bedroom.) On the left 
will be the Set Break button, on the center will be your Add Order button, and on the right will be information 
about your current shift. These buttons can be substituted by shortcuts, while some actions can only be done
through these shortcuts. 

Here are some examples of shortcut-only actions: to remove orders, press **down arrow** on your keyboard, to 
open Bedroom's settings dialog you can press either **Backspace** or **Delete**. Finally, Bedroom tracks
your shift's performance once it is closed, saving your final orders per hour with the ending date of your 
shift, this past history can be viewed in a histogram opened with backslash (```\```). More shortcuts can be 
seen below:

### _Shortcuts:_
* **Adding/removing orders:** _Up Arrow_ & _Down Arrow_ respectively.
* **Opening Set Break dialog:** _Number 0_
* **Exiting select time dialogs:** _Escape_
* **Accepting time in select time dialogs:** _Enter_
* **Open Performance History Chart:** _Back-slash_ (```\```)
   * _Right-clciking_ on or above a date's bar will open an option to delete this date's data.
* **Open Settings:** _Backspace_ or _Delete_
   * Holding _Shift_ while dragging the color sliders will make them all the same value.
* Any default Swing shortcuts.

_These shortcuts are meant to be unobtrusive to work applications,
hence their seemingly random keyboard placements._

## Compiling from source
_This is not supported, there could be loss of data or other bugs with things currently being experimented on._

After downloading the source code, extract the folder inside and delete the original zipped file. Then, open the 
extracted folder with your Terminal/Command Prompt and running gradle's build command.

* On Windows this can be done by selecting the address bar on the top of the File Explorer window while viewing 
the folder, typing ```cmd```, pressing **Enter** and running ```gradlew build```.
* On macOS or other unix type operating systems, (on MacOS you may need to run ```chmod +x gradlew``` before this
works) open the Terminal in the location of the extracted folder and run ```./gradlew build```. Once finished, 
the resulting files will be in the ```build``` folder. The .jar will be in ```build > libs``` and gradle's 
default build scripts will be in ```build > bin```.

## License
This program is licensed under the GPLv3 license, more information can be seen in the license file.
