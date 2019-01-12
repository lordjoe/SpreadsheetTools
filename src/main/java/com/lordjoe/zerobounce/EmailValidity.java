package com.lordjoe.zerobounce;

/**
 * com.lordjoe.zerobounce.EmailValidity
 * User: Steve
 * Date: 4/19/2018
 */
public enum EmailValidity {
    Valid,Invalid,CatchAll,Unknown;

    public boolean isValid() {
        return this == Valid;
    }

}
