========================================================================================================================
BUILDING RxTx FOR MAC OS X
------------------------------------------------------------------------------------------------------------------------

I had to compile the Mac OS X native library (librxtxSerial.jnilib) from source.  I was getting a PortInUseException
when trying to open ports that I was sure weren't already open.  A little Googling turned up a FAQ[1] which says:

   "Versions prior to 2.1-8 use lock files, which is not the MacOS X way of doing things, and therefore has issues. For
    this reason make sure that you have version 2.1-8 or higher, which makes use of I/O Kit. At this point in time 2.1-8
    is only availble from CVS, in source form. See the section Retrieving Source Code[2], on getting the latest code -
    be sure to get the code from the 'gnu.io' branch."

So, I checked out the source like this:

   [user@myhost]$ export CVSROOT=:pserver:anonymous@cvs.milestonesolutions.com:/usr/local/cvsroot
   [user@myhost]$ cvs login
   (Logging in to anonymous@cvs.milestonesolutions.com)
   CVS password: mousy
   [user@myhost]$ cvs checkout -r commapi-0-0-1 rxtx-devel

I then installed XCode tools and then followed the compilation docs[3].  Compilation was a breeze--it was nothing more
than doing the following in the rxtx-devel directory:

   BartleyMac:rxtx-devel chris$ ./configure
   BartleyMac:rxtx-devel chris$ make

That produced the RXTXcomm.jar and the librxtxSerial.jnilib (the latter located under the i686-apple-darwin9.2.2 directory)

NOTE: The version number reported on the command line when using this version of RxTx reads:

   Native lib Version = RXTX-2.1-7
   Java lib Version   = RXTX-2.1-7

I would've expected it to be 2.1-8 or higher, but, I don't get the PortInUseException anymore so I guess I shouldn't
worry about it.

Finally, the rxtx-native-macosx-universal.jar and rxtx-native-windows-i586.jar jars are simply jars of the native libraries
that I created manually.  They're used by the Java Web Start versions of the apps that use RxTx.
---------------------------------------
[1] http://rxtx.qbang.org/wiki/index.php/FAQ#On_MacOS_X_I_get_a_.27PortInUseException.27.2C_even_though_it_isn.27t.3F
[2] http://rxtx.qbang.org/wiki/index.php/Retrieving_Source_Code
[3] http://rxtx.qbang.org/wiki/index.php/Installation_on_MacOS_X

========================================================================================================================
BUILDING RxTx FOR WINDOWS
------------------------------------------------------------------------------------------------------------------------

I figured it was best to build the Windows native libraries (rxtxParallel.dll and rxtxSerial.dll) from source, since the
Mac version had to be built from source.  To do so, I checked out the source in exactly the same way as I did for the
Mac:

   [user@myhost]$ export CVSROOT=:pserver:anonymous@cvs.milestonesolutions.com:/usr/local/cvsroot
   [user@myhost]$ cvs login
   (Logging in to anonymous@cvs.milestonesolutions.com)
   CVS password: mousy
   [user@myhost]$ cvs checkout -r commapi-0-0-1 rxtx-devel

I then more or less followed the instructions for building on Windows with mingw32:

   http://rxtx.qbang.org/wiki/index.php/Installation_on_MS-Windows#mingw32_tools_in_DOS

I started by opening a command prompt and cd'ing to the rxtx-devel directory created by the CVS checkout above (for me,
that was at D:\CPB\Documents\Work\Projects\RxTx\rxtx-devel).  I then did this (as directed by the RxTx wiki at the above
URL):

   mkdir build
   copy Makefile.mingw32 build\Makefile
   cd build

I needed to install MinGW, so I downloaded the latest (5.1.4 as of this writing) installer from their Sourceforge
site (https://sourceforge.net/project/showfiles.php?group_id=2435&package_id=240780) and ran the installer.  I chose the
"current" package, as recommended, and then chose to install the MinGW base tools, the gcc (or maybe it was g++?  I
dunno) compiler, and MinGW make.  That all went fine.

I then created a MINGW_HOME environment variable (set to D:\MinGW) and put "%MINGW_HOME%\bin" on my PATH.

I then modified the Makefile, using the following settings:

   ######################
   #  user defined variables
   ######################

   # path to the source code (directory with SerialImp.c) Unix style path
   SRC=../src

   # path to the jdk directory that has include, bin, lib, ... Unix style path
   JDKHOME=C:/jdk1.5.0_15

   #path to mingw32
   MINGHOME="D:\MinGW"

   # path to install RXTXcomm.jar DOS style path
   COMMINSTALL="D:\CPB\Documents\Work\Projects\RxTx\rxtx-devel\build"

   # path to install the shared libraries DOS style path
   LIBINSTALL="D:\CPB\Documents\Work\Projects\RxTx\rxtx-devel\build"

   # path to the mingw32 libraries (directory with libmingw32.a) DOS style path
   LIBDIR="$(MINGHOME)\lib"

   # path to the junit library. Only needed for running the tests.
   JUNIT_JAR=D:\CPB\Documents\Work\Projects\NewBuild\TeRKBuild\lib\junit\junit.jar

   ######################
   #  End of user defined variables
   ######################

The RxTx wiki instructions for running make are wrong.  You can't do "make make install", nor just "make install".
Instead, you have to do:

   mingw32-make SHELL=cmd

That'll chug away for a couple seconds but should eventually result in creating the jar and the two DLLs.

========================================================================================================================

