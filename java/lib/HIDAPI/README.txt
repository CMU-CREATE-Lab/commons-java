------------------------------------------------------------------------------------------------------------------------
HIDAPI (http://www.signal11.us/oss/hidapi/ and https://github.com/signal11/hidapi)
------------------------------------------------------------------------------------------------------------------------

Command to fetch from git:

	git clone https://github.com/signal11/hidapi.git

I last fetched the code on 2013.11.22.

------------------------------------------------------------------------------------------------------------------------
BUILDING THE HIDAPI NATIVE LIBRARIES FOR MAC OS X
-------------------------------------------------

I first built under Mac OS X 10.8.2.  Here's the full output of what I did to create the libhidapi64.dylib:

$ cd hidapi
$ mkdir output
$ ./bootstrap
$ ./configure --prefix=/Users/chris/Documents/Work/Projects/HIDAPI/output/
$ make
$ make install
$ cp output/lib/libhidapi.0.dylib output/lib/libhidapi64.dylib

I don't have a Mac with OS X 10.5.x on it anymore, so I couldn't create the libhidapi32.dylib.  I tested the
libhidapi64.dylib and found that it works fine on 10.6 and 10.7.  There's not much motivation for us to support 10.5
anymore, so libhidapi32.dylib is still the old one from 2011 or whatever.

------------------------------------------------------------------------------------------------------------------------
BUILDING THE HIDAPI NATIVE LIBRARIES FOR UBUNTU LINUX 10.04
-----------------------------------------------------------

NOTE: I'm using the libusb implementation of HIDAPI.

Launched my VM of Ubuntu 10.04 32-bit, and then did the following (yes, I know some of it is redundant and unnecessary):

$ sudo apt-get update
$ sudo apt-get upgrade
$ sudo apt-get install build-essential
$ sudo apt-get autoremove
$ sudo apt-get install autoconf
$ sudo apt-get install libusb-1.0-0-dev
$ sudo apt-get install libudev-dev
$ sudo apt-get install libtool

Then followed the general HIDAPI Unix Platform installation instructions to create the libhidapi32.so:

$ cd hidapi
$ mkdir output
$ ./bootstrap
$ ./configure --prefix=/home/chris/HIDAPI/output/
$ make
$ make install
$ cp output/lib/libhidapi-libusb.so output/lib/libhidapi32.so

I then did the exact same thing under Ubuntu 10.04 64-bit to create the libhidapi64.so file, except the last line
copying to libhidapi64.so instead of libhidapi32.so.

In order to be able to test using one of our Java apps, I also installed the Sun Java 6 JDK by doing the following:

$ sudo add-apt-repository "deb http://archive.canonical.com/ lucid partner"
$ sudo apt-get update
$ sudo apt-get install sun-java6-jdk

------------------------------------------------------------------------------------------------------------------------
CREATING THE HIDAPI NATIVE LIBRARY JARS
---------------------------------------

The two jars containing the native libraries are required when using Java Web Start.  The native libraries stored in
the Mac OS jar use the extension ".jnilib" instead of ".dylib" because Java Web Start requires the ".jnilib" extension.
This is from the JNA FAQs (https://github.com/twall/jna/blob/master/www/FrequentlyAskedQuestions.md):

   Q:  I get an UnsatisfiedLinkError on OSX when I provide my native library via Java Web Start

   A:  Libraries loaded via the JNLP class loader on OSX must be named with a .jnilib suffix. The class loader won't
       find resources included with the nativelib tag if they have a .dylib suffix

I did the following to create the jars:

   $ cp libhidapi32.dylib libhidapi32.jnilib
   $ cp libhidapi64.dylib libhidapi64.jnilib
   $ jar cvf hidapi-native-macosx.jar *.jnilib
   added manifest
   adding: libhidapi32.jnilib(in = 53984) (out= 12540)(deflated 76%)
   adding: libhidapi64.jnilib(in = 63676) (out= 16021)(deflated 74%)
   $ jar cvf hidapi-native-linux.jar *.so
   added manifest
   adding: libhidapi32.so(in = 44679) (out= 19035)(deflated 57%)
   adding: libhidapi64.so(in = 57443) (out= 20847)(deflated 63%)
   $ rm *.jnilib
   $

------------------------------------------------------------------------------------------------------------------------
CREATING THE JNA INTERFACE
--------------------------

I then used JNAerator to generate the JNA files.  I did so by running this command (after copying libhidapi64.dylib,
hidapi.h, and jnaerator-0.9.5.jar to the same directory):

   java -jar jnaerator-0.9.5.jar -package edu.cmu.ri.createlab.usb.hid.hidapi -noRuntime -jar jna_hidapi.jar -library hidapi libhidapi64.dylib hidapi.h

NOTE: I had to hand-tweak the generated JNA interface code to make it work.  Specifically, I did the following:

* Renamed the class hid_device_info to HIDDeviceInfo

* Renamed the class HidapiLibrary to HIDAPILibrary

* Changed the signature of HIDAPILibrary.hid_enumerate() from:

      int hid_enumerate(short vendor_id, short product_id);

  to:

      HIDDeviceInfo hid_enumerate(short vendor_id, short product_id);

* Changed the signature of HIDAPILibrary.hid_free_enumeration() from:

      int hid_free_enumeration(final HIDDeviceInfo devs);

  to:

      void hid_free_enumeration(final HIDDeviceInfo devs);

* Changed the path data member in HIDDeviceInfo from a Pointer to a String.

* Changed the signature of HIDAPILibrary.hid_close() from:

      int hid_close(HIDAPILibrary.hid_device device);

   to:

      void hid_close(HIDAPILibrary.hid_device device);

* Finally, I changed the definitions of the JNA_LIBRARY_NAME and INSTANCE in HIDAPILibrary to this:

      String JNA_LIBRARY_NAME = NativeLibraryVersionChooser.getLibraryName("hidapi32", "hidapi64");
      HIDAPILibrary INSTANCE = (HIDAPILibrary)Native.loadLibrary(HIDAPILibrary.JNA_LIBRARY_NAME, HIDAPILibrary.class, MangledFunctionMapper.DEFAULT_OPTIONS);

I also ran IDEA's inspector on both files, cleaning up things like replacing fully-qualified class names with imports,
etc.  I finished off with the IDEA's code formatter.