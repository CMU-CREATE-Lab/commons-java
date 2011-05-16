------------------------------------------------------------------------------------------------------------------------
HIDAPI (http://www.signal11.us/oss/hidapi/ and https://github.com/signal11/hidapi)
------------------------------------------------------------------------------------------------------------------------

Command to fetch from git:

	git clone git://github.com/signal11/hidapi.git

I last fetched the code on 2011.05.02.

------------------------------------------------------------------------------------------------------------------------
CHANGES TO HIDAPI
-----------------

I modified the Makefile for /mac by changing this line:

      CFLAGS+=-I../hidapi -Wall -g -c

  to this:

      CFLAGS+=-I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc

I could have also added the call to g++ for creating the dylib to the Makefile, but I wanted to change as little as
possible.  Plus, Makefiles and I hate each other.

------------------------------------------------------------------------------------------------------------------------
BUILDING THE HIDAPI NATIVE LIBRARIES FOR MAC OS X
-------------------------------------------------

I first built under Mac OS X 10.6.7.  Here's the full output of what I did:

$ cd hidapi/mac/
$ ll
total 48
-rw-r--r--  1 chris  staff    592 May  2 15:58 Makefile
-rw-r--r--  1 chris  staff  20523 May 11 16:10 hid.c
$ make
gcc -I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc hid.c -o hid.o
g++ -I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc ../hidtest/hidtest.cpp -o ../hidtest/hidtest.o
g++ -Wall -g hid.o ../hidtest/hidtest.o -framework IOKit -framework CoreFoundation -o hidtest
$ g++ -arch i386 -arch x86_64 -arch ppc -dynamiclib -Wall -g hid.o -framework IOKit -framework CoreFoundation -o libhidapi64.dylib
$ ll
total 424
-rw-r--r--  1 chris  staff    592 May  2 15:58 Makefile
-rw-r--r--  1 chris  staff  20523 May 11 16:10 hid.c
-rw-r--r--  1 chris  staff  94028 May 11 16:18 hid.o
-rwxr-xr-x  1 chris  staff  25224 May 11 16:18 hidtest
-rwxr-xr-x  1 chris  staff  71644 May 11 16:18 libhidapi64.dylib
$ 

I then re-built under Mac OS 10.5 to create the libhidapi32.dylib file.

------------------------------------------------------------------------------------------------------------------------
BUILDING THE HIDAPI NATIVE LIBRARIES FOR UBUNTU LINUX 10.04
-----------------------------------------------------------

NOTE: I'm using the libusb implementation of HIDAPI.  I used the Makefile submitted by mru00:

   https://github.com/mru00/hidapi/blob/c6a13edd2568ed02ffae2eb6d7fd4e142800b803/linux/Makefile

I did a clean install of Ubuntu 10.04 32-bit, and then did the following:

$ sudo apt-get update
$ sudo apt-get upgrade
$ sudo apt-get install build-essential
$ sudo apt-get install libusb-1.0-0-dev
$ sudo apt-get install libudev-dev
...
$ cd hidapi/linux/
$ ll
total 26
-rw-r--r-- 1 501 12993 2011-05-02 12:02 hid.c
-rw-r--r-- 1 501 35335 2011-05-11 13:19 hid-libusb.c
-rw-r--r-- 1 501   587 2011-05-02 12:02 Makefile
-rw-r--r-- 1 501  3053 2011-05-02 12:02 README.txt
$ make
gcc -I../hidapi -Wall -g `pkg-config libusb-1.0 --cflags` -c hid-libusb.c -o hid-libusb.o
g++ -I../hidapi -Wall -g `pkg-config libusb-1.0 --cflags` -c ../hidtest/hidtest.cpp -o ../hidtest/hidtest.o
g++ -Wall -g hid-libusb.o ../hidtest/hidtest.o -Wall -fpic `pkg-config libusb-1.0 libudev --libs` -o hidtest
gcc -I../hidapi -Wall -g `pkg-config libusb-1.0 --cflags` -Wall -fpic `pkg-config libusb-1.0 libudev --libs` -shared -fpic hid-libusb.c ../hidapi/hidapi.h -o libhidapi.so
$ mv libhidapi.so libhidapi32.so
$ ll
-rw-r--r-- 1 501 12993 2011-05-02 12:02 hid.c
-rw-r--r-- 1 501 35334 2011-05-13 04:09 hid-libusb.c
-rw-r--r-- 1 501 36396 2011-05-16 09:21 hid-libusb.o
-rwxr-xr-x 1 501 45425 2011-05-16 09:21 hidtest
-rwxr-xr-x 1 501 43202 2011-05-16 09:21 libhidapi32.so
-rw-r--r-- 1 501   940 2011-05-12 12:56 Makefile
-rw-r--r-- 1 501   587 2011-05-12 12:54 Makefile.ORIGINAL
-rw-r--r-- 1 501  3053 2011-05-02 12:02 README.txt
$

I then rebuilt under Ubuntu 10.04 64-bit to create the libhidapi64.so file.

In order to be able to test using one of our Java apps, I also installed the Sun Java 6 JDK by doing the following:

$ sudo add-apt-repository "deb http://archive.canonical.com/ lucid partner"
$ sudo apt-get update
$ sudo apt-get install sun-java6-jdk

------------------------------------------------------------------------------------------------------------------------
CREATING THE JNA INTERFACE
--------------------------

I then used JNAerator to generate the JNA files.  I did so by running this command (after copying libhidapi.dylib,
hidapi.h, and jnaerator-0.9.5.jar to the same directory):

   java -jar jnaerator-0.9.5.jar -package edu.cmu.ri.createlab.usb.hid.hidapi -noRuntime -jar jna_hidapi.jar -library hidapi libhidapi.dylib hidapi.h

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

* Changed the path data member in HIDDeviceInfo from a Pointer to a String.  This also required changing the signature
  of the constructor signature accordingly.

* Changed the signature of HIDAPILibrary.hid_close() from:

      int hid_close(HIDAPILibrary.hid_device device);

   to:

      void hid_close(HIDAPILibrary.hid_device device);

* Finally, I changed the definition of the JNA_LIBRARY_NAME member in HIDAPILibrary from this:

      String JNA_LIBRARY_NAME = LibraryExtractor.getLibraryPath("hidapi", true, HIDAPILibrary.class);

  to this:

      String JNA_LIBRARY_NAME = LibraryExtractor.getLibraryPath(NativeLibraryVersionChooser.getLibraryName("hidapi32", "hidapi"), true, HIDAPILibrary.class);

I also ran IDEA's inspector on both files, cleaning up things like replacing fully-qualified class names with imports,
etc.  I finished off with the IDEA's code formatter.