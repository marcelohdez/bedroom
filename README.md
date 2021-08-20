# Bedroom
Bedroom is an open source Java application developed in order to aid call center agents in keeping track of their orders 
and orders per hour to meet their quotas effectively. Due to the pandemic people have started working remotely, working 
from the bedroom couldn't get any easier! Or... maybe it just did.

## Prerequisites
**For running and/or compiling the program** the newest version of Java is recommended, while the minimum required
version is stated under the release you are trying to run. Ex: For Bedroom 3 Java 16+ is required.

### _How to set up_
After downloading the .jar file from the releases section, you can simply double click to open like other applications. 
It is recommended to make a start script for Bedroom to reduce its memory usage if you are into that stuff. For Windows, 
a .bat file can be made in Bedroom's location with the text ```start javaw -jar -Xmx32M filename.jar``` (replacing 
_filename_ with the name of the jar you are trying to run) and then making a shortcut to it wherever you like. If you 
want to keep the command prompt opened for errors, etc. change javaw to java. "-Xmx32M" is the maximum amount of memory 
Bedroom is allowed to allocate to itself (In this case it is 32MB, actual memory usage may be more due to the JVM, more 
information can be found [here](https://plumbr.io/blog/memory-leaks/why-does-my-java-process-consume-more-memory-than-xmx).)

## How to use
After opening Bedroom you can input your clock in and out times, if you every mess up on these select time dialogs you
may close them to go to the previous one (although closing the clock in time dialog will close Bedroom.) On the right will
be information about your current shift, on the center will be your Add Order button, and on the left will be the Set
Break button. These buttons can be substituted by shortcuts, which you can see below. To remove orders you have to use
a shortcut, which is currently the down arrow on your keyboard. Bedroom also contains a Settings dialog for customizing
your theme and other things to make your experience better, this can be opened through either Backspace or Delete.

### _Shortcuts:_
* **Adding/removing orders:** _Up Arrow_ & _Down Arrow_ respectively.
* **Settings:** _Backspace_ or _Delete_
* **Opening Set Break dialog:** _Number 0_
* **Exiting select time dialogs:** _Escape_
* **Selecting time in select time dialogs, without pressing Select button:** _Enter_
* Any default Swing shortcuts.

_These shortcuts are meant to be unobtrusive to work applications,
hence their seemingly random keyboard placements._

## Compiling from source
_This is not supported; there could be loss of data or other bugs with things currently being experimented on._

After downloading the source code, extract the folder inside and delete the original zipped file. Then, open the 
extracted folder with your Terminal/Command Prompt (on Windows this can simply be done by selecting the address bar on 
top, typing ```cmd```, then pressing Enter) and run ```gradlew build```. In macOS, you may need to run ```chmod +x gradlew``` 
before this works, open the Terminal in the location of the extracted folder and run ```./gradlew build```. Once 
finished, the resulting files will be in the ```build``` folder. The .jar will be in ```build > libs``` and gradle's
default build scripts will be in ```build > bin```.
