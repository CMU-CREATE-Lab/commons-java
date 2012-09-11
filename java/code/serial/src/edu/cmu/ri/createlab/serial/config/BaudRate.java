package edu.cmu.ri.createlab.serial.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum BaudRate
   {
      BAUD_50("50"),
      BAUD_75("75"),
      BAUD_110("110"),
      BAUD_150("150"),
      BAUD_200("200"),
      BAUD_300("300"),
      BAUD_600("600"),
      BAUD_1200("1200"),
      BAUD_1800("1800"),
      BAUD_2400("2400"),
      BAUD_4800("4800"),
      BAUD_9600("9600"),
      BAUD_19200("19200"),
      BAUD_38400("38400"),
      BAUD_57600("57600"),
      BAUD_115200("115200"),
      BAUD_125000("125000"),
      BAUD_230400("230400"),
      BAUD_460800("460800"),
      BAUD_921600("921600");

   private static final Map<String, BaudRate> NAME_TO_BAUD_RATE_MAP;

   static
      {
      final Map<String, BaudRate> nameToBaudRateMap = new HashMap<String, BaudRate>();
      for (final BaudRate baudRate : BaudRate.values())
         {
         nameToBaudRateMap.put(baudRate.getName(), baudRate);
         }
      NAME_TO_BAUD_RATE_MAP = Collections.unmodifiableMap(nameToBaudRateMap);
      }

   public static BaudRate findByName(final String name)
      {
      return NAME_TO_BAUD_RATE_MAP.get(name);
      }

   private final String name;
   private final int value;

   BaudRate(final String name)
      {
      this.name = name;
      this.value = Integer.parseInt(name);
      }

   public String getName()
      {
      return name;
      }

   public int getValue()
      {
      return value;
      }

   public String toString()
      {
      return name;
      }
   }
