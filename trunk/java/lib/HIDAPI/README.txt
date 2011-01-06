------------------------------------------------------------------------------------------------------------------------
HIDAPI (http://www.signal11.us/oss/hidapi/)
------------------------------------------------------------------------------------------------------------------------

Command to fetch from git:

	git clone git://github.com/signal11/hidapi.git

I last fetched the code on 2011.01.05.

------------------------------------------------------------------------------------------------------------------------
CHANGES TO HIDAPI
-----------------

* I need to be able to ask the HID device what the max size is for input and output reports.  There's no way (obvious to
  me, at least) to do this with HIDAPI, so I added the following to hidapi.h:

		/** @brief Get The max length in bytes of an input report.

			@ingroup API
			@param device A device handle returned from hid_open().

			@returns
				This function returns the max length in bytes of an input report.
		*/
		int HID_API_EXPORT_CALL hid_get_max_input_report_size(hid_device *device);

		/** @brief Get The max length in bytes of an output report.

			@ingroup API
			@param device A device handle returned from hid_open().

			@returns
				This function returns the max length in bytes of an output report.
		*/
		int HID_API_EXPORT_CALL hid_get_max_output_report_size(hid_device *device);

* I then added the following to mac/hid.c (I didn't update the code for other platforms because I'm lazy...sorry):

      static int32_t get_max_output_report_length(IOHIDDeviceRef device)
      {
         return get_int_property(device, CFSTR(kIOHIDMaxOutputReportSizeKey));
      }

      int HID_API_EXPORT_CALL hid_get_max_input_report_size(hid_device *dev)
      {
         return get_max_report_length(dev->device_handle);
      }

      int HID_API_EXPORT_CALL hid_get_max_output_report_size(hid_device *dev)
      {
         return get_max_output_report_length(dev->device_handle);
      }

* I modified the Makefile for /mac by changing this line:

      CFLAGS+=-I../hidapi -Wall -g -c

  to this:

      CFLAGS+=-I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc

I could probably add the call to g++ for creating the dylib to the Makefile, but I wanted to change as little as
possible.  Plus, Makefiles and I hate each other.

------------------------------------------------------------------------------------------------------------------------
BUILDING HIDAPI AND CREATING THE JNA INTERFACE
----------------------------------------------

Here's the full output of what I did:

$ cd hidapi/mac/
$ ll
total 48
-rw-r--r--@ 1 chris  staff    592 Dec 14 13:26 Makefile
-rw-r--r--  1 chris  staff  17850 Dec 14 13:20 hid.c
$ make
gcc -I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc hid.c -o hid.o
g++ -I../hidapi -Wall -g -c -arch i386 -arch x86_64 -arch ppc ../hidtest/hidtest.cpp -o ../hidtest/hidtest.o
g++ -Wall -g hid.o ../hidtest/hidtest.o -framework IOKit -framework CoreFoundation -o hidtest
$ g++ -arch i386 -arch x86_64 -arch ppc -dynamiclib -Wall -g hid.o -framework IOKit -framework CoreFoundation -o libhidapi.dylib
$ ll
total 424
-rw-r--r--@ 1 chris  staff    592 Dec 14 13:26 Makefile
-rw-r--r--  1 chris  staff  17850 Dec 14 13:20 hid.c
-rw-r--r--  1 chris  staff  87608 Dec 14 14:09 hid.o
-rwxr-xr-x  1 chris  staff  24696 Dec 14 14:09 hidtest
-rwxr-xr-x  1 chris  staff  71196 Dec 14 14:09 libhidapi.dylib
$ 

I then re-built under Mac OS 10.5 to create the libhidapi32.dylib file.

I then used JNAerator to generate the JNA files.  I did so by running this command (after copying libhidapi.dylib,
hidapi.h, and jnaerator-0.9.5.jar to the same directory):

   java -jar jnaerator-0.9.5.jar -package edu.cmu.ri.createlab.usb.hid.mac -noRuntime -jar jna_hidapi.jar -library hidapi libhidapi.dylib hidapi.h

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