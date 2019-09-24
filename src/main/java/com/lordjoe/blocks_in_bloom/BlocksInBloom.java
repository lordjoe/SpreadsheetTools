package com.lordjoe.blocks_in_bloom;

import com.lordjoe.utilities.FileUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.lordjoe.blocks_in_bloom.BlocksInBloom
 * User: Steve
 * Date: 8/22/19
 */
public class BlocksInBloom {
    public static final BlocksInBloom[] EMPTY_ARRAY = {};

    public static List<BlockAddress> csvToAdress(String arg) {
        List<BlockAddress> ret = new ArrayList<>() ;
        File f = new File(arg);
        LineNumberReader rdr = null;
        String line;
        try  {
            rdr = new LineNumberReader(new FileReader(f));
            line = rdr.readLine();
            line = rdr.readLine();
            while(line != null)  {
                if(!line.equalsIgnoreCase(",,,,,,,,,0") && !line.startsWith("TOTAL")) {
                    BlockAddress addr = new BlockAddress(line);
     //               if(addr.street.length() > 0)
                            ret.add(addr) ;
    //                else
    //                    System.out.println(addr.asAddress());
                }
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if(rdr != null)   {
                try {
                    rdr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }
        return ret;
    }





    public static List<LatLong> csvLatlon(String arg) {
        List<LatLong> ret = new ArrayList<>() ;
        File f = new File(arg);
        LineNumberReader rdr = null;
        String line;
        try  {
            rdr = new LineNumberReader(new FileReader(f));
            line = rdr.readLine();
            while(line != null)  {
                if(!line.equalsIgnoreCase(",,,,,,,,,0"))
                    ret.add(new LatLong(line)) ;
                line = rdr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if(rdr != null)   {
                try {
                    rdr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }
        return ret;
    }



    private static String buildWebPage(Map<BlockAddress, LatLong> toLatLon) {
        String page = PageBuilder.buildPage(toLatLon);
        return page;
    }

    private static void showAddresses(List<BlockAddress> addresses) {
        for (BlockAddress address : addresses) {
            System.out.println(address.asAddress());
        }
    }

    private static void writeTSVAddresses(List<BlockAddress> addresses) {
        try {
            File f = new File("BlockAddresses.tsv");
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.println(BlockAddress.HEADER);
            for (BlockAddress address : addresses) {
                pw.println(address.asTSV());
            }
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    private static Map<BlockAddress, LatLong> getBlockAddressLatLongMap(String[] args) {
        Map<BlockAddress,LatLong>  toLatLon = new HashMap<>() ;
        List<BlockAddress> addresses = csvToAdress(args[0]);
        //      showAddresses(addresses);
        List<LatLong> llls = csvLatlon(args[1]);
        int index = 0;
        for (BlockAddress address : addresses) {
            if(address.street.length() > 0) {
                LatLong ll = llls.get(index++);
                if(!ll.isZero()) {
                    address.setLatLon(ll);
                    toLatLon.put(address, ll);
                }
            }
            //       System.out.println(address.asAddress() + " " + llls.get(index++));
        }
        writeTSVAddresses(addresses);
        return toLatLon;
    }


    private static Map<BlockAddress, LatLong> getTsvBlockAddressLatLongMap(String[] args) {
        Map<BlockAddress,LatLong>  toLatLon = new HashMap<>() ;
        List<BlockAddress> addresses = csvToAdress(args[0]);
        //      showAddresses(addresses);
       int index = 0;
        for (BlockAddress address : addresses) {
            if(address.street.length() > 0) {
                  if(address.getLatLon() != null) {
                     toLatLon.put(address, address.getLatLon());
                }
                  else {
                      System.out.println(address.asAddress() + " No Lat Lon");
                  }
            }
            else {
                if(address.block.length() > 0)
                    System.out.println(address.asAddress() + " No Street Address");
            }
            //       System.out.println(address.asAddress() + " " + llls.get(index++));
        }
          return toLatLon;
    }

    public static void main(String[] args) {
      Map<BlockAddress, LatLong> toLatLon = getTsvBlockAddressLatLongMap(args);
     //    Map<BlockAddress, LatLong> toLatLon = getBlockAddressLatLongMap(args);
        String page = buildWebPage(toLatLon) ;
        FileUtilities.writeFile("blocksInBloom2.html",page);
  //      System.out.println(page);
    }


}
