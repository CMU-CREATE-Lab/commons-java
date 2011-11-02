========================================================================================================================
RXTX
------------------------------------------------------------------------------------------------------------------------

This is a slightly modified version of the RxTx library (http://rxtx.qbang.org/wiki/index.php/Main_Page).  I created it
by taking a CVS snapshot on 2011.02.03 and modifying a few files.  Full explanation of changes are detailed below.

I checked out the source like this:

      $ export CVSROOT=:pserver:anonymous@qbang.org:/var/cvs/cvsroot
      $ cvs login
      (Logging in to :pserver:anonymous@qbang.org:2401/var/cvs/cvsroot)
      CVS password:
      $ cvs checkout -r commapi-0-0-1 rxtx-devel

I've included here zipped versions of both the original, unmodified code (RxTx20110203_unmodified.zip) and my modified
version (RxTx20110203.zip).

In all cases I'm assuming use of Java 1.6.

The rxtx-native-macosx.jar and rxtx-native-windows.jar are simply jarred versions of the native libraries for use with
Java Web Start.

========================================================================================================================
USAGE NOTES
------------------------------------------------------------------------------------------------------------------------

This modifed version of RxTx lets you narrow the set of ports the RxTx will consider by specifying the system property
"gnu.io.rxtx.SerialPorts" or "gnu.io.rxtx.ParallelPorts".  You can set the property either via the -D command line
switch, as follows...

      -Dgnu.io.rxtx.SerialPorts=/dev/tty.brainlink

      -Dgnu.io.rxtx.SerialPorts=/dev/tty.brainlink:/dev/cu.brainlink

...or you can create a properties file named "gnu.io.rxtx.properties" containing the property definition(s) and put it
on the classpath.   The properties file takes precedence over the system properties.

Note that ports are delimited by the path separator character for whatever platform you're running under.

========================================================================================================================
CHANGES
------------------------------------------------------------------------------------------------------------------------

The RxTx library as of 2011.02.03 requires a few changes to work the way we need it to for our various CREATE Lab
projects.  Some changes are for functionality, others are simply bugfixes or things required to get it to build.

I changed the following files:

      * /src/gnu/io/CommPortIdentifier.java
      * /src/gnu/io/RXTXCommDriver.java
      * /src/gnu/io/RXTXVersion.java
      * /src/SerialImp.c
      * /src/win32termios.h

Explanations for my changes follow...

* /src/gnu/io/CommPortIdentifier.java

As of 2011.02.03, there was a bug in CommPortIdentifier.java on line 123.  It was still loading the RxTx native library
using System.loadLibrary() rather than RXTXVersion.loadLibrary().  The RXTXVersion.loadLibrary() method checks whether
the 64-bit library is required and, if so, looks for a native library named "librxtxSerial64" (plus whatever extension
is appropriate for the platform).  I submitted this as a bug (http://bugzilla.qbang.org/show_bug.cgi?id=151).

* /src/gnu/io/RXTXCommDriver.java

I changed the implementation of the registerSpecifiedPorts() method in RXTXCommDriver.java so that it checks the system
properties (e.g. specified via a -D command line switch) for the gnu.io.rxtx.SerialPorts and gnu.io.rxtx.ParallelPorts
properties (the motivation is that it's easier to specify the properties as system properties rather than have to use a
properties file). Full source code is zipped and included here.  You'll find various posts which claim that support
already exists for setting the gnu.io.rxtx.SerialPorts system property, but I sure don't see it in the code, and never
got it to work until making the change described above.

* /src/gnu/io/RXTXVersion.java
* /src/SerialImp.c

RxTx complains if the native library version and the jar version don't match.  This is a good idea, but as of 2011.02.03
the versions don't match in the source.  The version in the native code is "RXTX-2.2pre2" and the version in the jar
is "RXTX-2.2".  So you end up with a bogus warning which just confuses users.  To fix this, and to make it clear that
this is a modified version of a snapshot, I changed the version in RXTXVersion.java (line 79) to:

      "RXTX-2.2 (CVS snapshot 2011.02.03, modified by CMU CREATE Lab, http://code.google.com/p/create-lab-commons/)"

Yeah, it's verbose, I know.

I changed line 4241 of SerialImp.c to match.

* /src/win32termios.h

When building under Windows 7 x64 (see instructions below), I got an error which complained that the timespec struct
had already been defined.  To fix it, I simply put a #ifndef around the definition, like this (lines 102-109):

      #ifndef _TIMESPEC_DEFINED
      #define _TIMESPEC_DEFINED
      struct timespec
      {
         time_t	tv_sec;
         long	tv_nsec;
      };
      #endif

========================================================================================================================
BUILDING FOR MAC OS X
------------------------------------------------------------------------------------------------------------------------
Instructions for building the 32-bit and 64-bit versions are pretty much the same.  To create the 32-bit version, I
built under Mac OS X 10.5.8.  To create the 64-bit version, I built under Mac OS X 10.6.6.

WARNINGS AND NOTES:

* Before trying to build, first make sure that the PATH environment variable has /usr/bin and /bin first.  I modified my
  .profile so that the various places where I set the PATH (e.g. "export PATH=$PATH:/opt/local/bin:/opt/local/sbin")
  have my additions at the end.

* Make sure there are no spaces in the path absolute path to the rxtx-devel directory!  Make will fail with a
  useless error message about there being no target for "i386-apple-darwin9.8.0" or somesuch.

* I have the environment variable JAVA_HOME defined like this (which is the recommended way according to the java_home
  man page):

      export JAVA_HOME=`/usr/libexec/java_home`

  I mention this because when running configure, there's a warning that it's using the JAVA_HOME environment variable.
  It's not a problem because it's defined correctly, but I wanted to mention it here for completeness.
      
Here's what I did to build:

* Installed XCode tools.

* Opened a command prompt in the rxtx-devel directory and did the following:

      $ ./configure

  Under Mac OS X 10.6.6, configure complained with the following:

     ./configure: line 21808: cd: /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/../../../Headers: No such file or directory
     ./configure: line 21809: cd: /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/../../../Headers: No such file or directory

     WARNING: configure is having a hard time determining which
     directory contains the file jni_md.h. Edit Makefile and fix the
     variable JAVANATINC to point to the correct directory.

  So I simply changed line 107 in the generated Makefile to read:

      JAVAINCLUDEDIR = /System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers

  I changed JAVAINCLUDEDIR instead of JAVANATINC because both JAVAINCLUDE and JAVANATINC simply reference JAVAINCLUDEDIR:

      JAVAINCLUDE = -I$(JAVAINCLUDEDIR)
      JAVAINCLUDEDIR = /System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers
      JAVANATINC = -I$(JAVAINCLUDEDIR)/

  So fixing JAVAINCLUDEDIR fixes them both.

* Finally, just run make:

      $ make

  That produced the RXTXcomm.jar and the librxtxSerial.jnilib.  The RXTXcomm.jar will be in the rxtx-devel directory.
  The librxtxSerial.jnilib will be in the i386-apple-darwin9.8.0 directory when built under Mac OS X 10.5.8.  It'll be
  in the i386-apple-darwin10.6.0 directory when built under Mac OS X 10.6.6.

* After building under Mac OS X 10.6.6, I renamed the libary librxtxSerial64.jnilib.

========================================================================================================================
BUILDING FOR 32-BIT WINDOWS
------------------------------------------------------------------------------------------------------------------------
I started with a clean install of Windows XP (running under VMWare on my Mac).  I then did the following...

* Shared my CREATELabCommons project directory with the Windows XP virtual machine.  It's mapped to the Z drive.  It's
  not critical, but I used it when referencing the junit jar in the Makefile--see below.

* Installed JDK 1.6.0_23-b05 to C:\jdk1.6.0_23

* Installed MinGW.  I chose the mingw-get-inst-20101030.exe installer.  During the installation, I chose to download the
  latest repository catalogues (rather than using the pre-packaged catalogues).  When prompted to select components to
  install, the only additional one I chose was the "C++ Compiler".

* I then created a MINGW_HOME environment variable (set to C:\MinGW) and put "%MINGW_HOME%\bin" on my PATH.

* In the root of the RxTx directory, I edited the Makefile.mingw32 file to use the following settings:

      ######################
      #  user defined variables
      ######################

      # path to the source code (directory with SerialImp.c) Unix style path
      SRC=../src

      # path to the jdk directory that has include, bin, lib, ... Unix style path
      JDKHOME=C:/jdk1.6.0_23

      #path to mingw32
      MINGHOME="C:\MinGW"
      
      # path to install RXTXcomm.jar DOS style path
      COMMINSTALL="Z:\CREATELabCommons\trunk\java\lib\rxtx\RxTx20100928\rxtx-devel\build"

      # path to install the shared libraries DOS style path
      LIBINSTALL="Z:\CREATELabCommons\trunk\java\lib\rxtx\RxTx20100928\rxtx-devel\build"

      # path to the mingw32 libraries (directory with libmingw32.a) DOS style path
      LIBDIR="$(MINGHOME)\lib"

      # path to the junit library. Only needed for running the tests.
      JUNIT_JAR=Z:\CREATELabCommons\trunk\java\lib\junit\junit-4.7.jar

      ######################
      #  End of user defined variables
      ######################

* I then opened a command prompt in the rxtx-devel directory and did the following:

      Z:\RxTx20110203\rxtx-devel>mkdir build

      Z:\RxTx20110203\rxtx-devel>copy Makefile.mingw32 build\Makefile
              1 file(s) copied.

      Z:\RxTx20110203\rxtx-devel>cd build

      Z:\RxTx20110203\rxtx-devel\build>mingw32-make SHELL=cmd

That's it!  No errors.  The RXTXcomm.jar, rxtxSerial.dll, and rxtxParallel.dll are all created in the build directory.
I moved them out for safe keeping and then deleted the build directory so as not to interfere with the 64-bit build.

========================================================================================================================
BUILDING FOR 64-BIT WINDOWS
------------------------------------------------------------------------------------------------------------------------

I started with a clean install of Windows 7 Professional x64 (running under VMWare on my Mac).  I then did the
following...

* Shared my CREATELabCommons project directory with the Windows 7 virtual machine.  It's mapped to the Z drive.  It's
  not critical, but I used it when referencing the junit jar in the Makefile--see below.

* Installed JDK 1.6.0_23-b05 to C:\jdk1.6.0_23

* Downloaded TDM-GCC by going to:

      http://tdm-gcc.tdragon.net/download

  I chose the tdm64-gcc-4.5.1 Bundler Installer and went with all the default options, except I installed it to
  C:\TDM_GCC because there was some warning about installing it to C:\MinGW or somesuch.  Whatever.

* In the root of the RxTx directory, I copied Makefile.mingw32 to Makefile.mingw64 and then modified Makefile.mingw64
  using the following settings:

      ######################
      #  user defined variables
      ######################

      # path to the source code (directory with SerialImp.c) Unix style path
      SRC=../src

      # path to the jdk directory that has include, bin, lib, ... Unix style path
      JDKHOME=C:/jdk1.6.0_23

      #path to mingw32
      MINGHOME="C:\TDM_GCC\x86_64-w64-mingw32"

      # path to install RXTXcomm.jar DOS style path
      COMMINSTALL="Z:\CREATELabCommons\trunk\java\lib\rxtx\RxTx20100928\rxtx-devel\build"

      # path to install the shared libraries DOS style path
      LIBINSTALL="Z:\CREATELabCommons\trunk\java\lib\rxtx\RxTx20100928\rxtx-devel\build"

      # path to the mingw32 libraries (directory with libmingw32.a) DOS style path
      LIBDIR="$(MINGHOME)\lib"

      # path to the junit library. Only needed for running the tests.
      JUNIT_JAR=Z:\CREATELabCommons\trunk\java\lib\junit\junit-4.7.jar

      ######################
      #  End of user defined variables
      ######################

* I then opened a command prompt in the rxtx-devel directory and did the following:

      Z:\RxTx20110203\rxtx-devel>mkdir build

      Z:\RxTx20110203\rxtx-devel>copy Makefile.mingw64 build\Makefile
              1 file(s) copied.

      Z:\RxTx20110203\rxtx-devel>cd build

      Z:\RxTx20110203\rxtx-devel\build>mingw32-make SHELL=cmd

  I noticed several "cast to pointer from integer of different size" warnings during the build (twice or so for the
  serial lib, and maybe 8 for the parallel), but so far I haven't come across a case where it's actually a problem.

* I then renamed the generated DLLs:

      Z:\RxTx20110203\rxtx-devel\build>rename rxtxParallel.dll rxtxParallel64.dl

      Z:\RxTx20110203\rxtx-devel\build>rename rxtxSerial.dll rxtxSerial64.dll

Finally, I copied the DLLs elsewhere for safekeeping and deleted the build directory.

========================================================================================================================
BUILDING FOR 32-BIT LINUX
------------------------------------------------------------------------------------------------------------------------

I built the 32-bit linux versions under Ubuntu Server 10.04.  I simply did the following:

$ sudo apt-get install binutils libtool cvs gcc make build-essential linux-headers-generic
$ ./configure
$ make

After make finished, I found the .so files under the i686-pc-linux-gnu/.libs directory.  They were symlinks to files
containing the version number.  For simplicity, I just copied the .so files I wanted and renamed them librxtxSerial.so
and librxtxParallel.so.

========================================================================================================================
BUILDING FOR 64-BIT LINUX
------------------------------------------------------------------------------------------------------------------------

I built the 64-bit linux versions under Ubuntu 10.04 64-bit.  I simply did the following:

$ sudo apt-get install binutils libtool cvs gcc make build-essential linux-headers-generic
$ ./configure
$ make

After make finished, I found the .so files under the x86_64-unknown-linux-gnu/.libs directory.  They were symlinks to
files containing the version number.  For simplicity, I just copied the .so files I wanted and renamed them
librxtxSerial64.so and librxtxParallel64.so.

========================================================================================================================
