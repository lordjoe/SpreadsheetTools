package com.lordjoe.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * com.lordjoe.utilities.CcaheRepositiry
 * User: Steve
 * Date: 4/18/2018
 */
public class IdRepository {

    public static final String SEPARATOR = "_";
    public static final int MINIMUM_ID = 1000;

    private static final Map<String,IdRepository> byName = new HashMap<>();

    public static boolean isIdUsed(String id)  {
        IdRepository idRepository = byName.get(getIdType(id));
        if(idRepository == null)
            return false;
       return true;
    }

    public static boolean registerId(String id)  {
        String idType = getIdType(id);
        IdRepository idRepository = getRepository(idType);
        if(idRepository.isUsed(id) )
            return true; // id used

       return false; // new id
    }

    public static int getIdNumber(String id)  {
        String[] split = id.split(SEPARATOR);
        return Integer.parseInt(split[1]) ;
    }


    public static String getIdType(String id)  {
        String[] split = id.split(SEPARATOR);
        return split[0] ;
    }


    public static synchronized IdRepository getRepository(String name)    {
        IdRepository ret = byName.get(name) ;
        if(ret == null)  {
            ret = new IdRepository(name) ;
            byName.put(name,ret);
        }
        return ret;
    }


    public static synchronized String getNewId(String name)    {
        IdRepository ret = getRepository(name);
        return ret.getNewId();
    }


    public final String name;
    private int lastId = MINIMUM_ID;
    private final Set<String> used = new HashSet<>();

    private synchronized String buildID(int i)  {
        return name + SEPARATOR + String.format("%08d",i) ;
    }

    private void register(String id)  {
        if(!name.equals(getIdType(id)))
            throw new IllegalArgumentException("type must be " + name + " not " + getIdType(id) + " : " + id);
        used.add(id);
        int index = getIdNumber(id);
        lastId = Math.max(lastId,index + 1);
    }

    private synchronized int getNextId()
    {
        return lastId++;
    }

    private IdRepository(String name) {
        this.name = name;
    }

    public boolean isUsed(String id)
    {
        return used.contains(id);
    }

    public synchronized String getNewId()  {
        String ret = buildID(getNextId());
        if(!used.contains(ret))
            return ret;
        while(used.contains(ret))   {
            ret = buildID(getNextId());
        }
        register(ret);
        return ret;
    }

}
