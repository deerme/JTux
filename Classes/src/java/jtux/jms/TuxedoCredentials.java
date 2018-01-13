/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

/**
 * @internal
 */
public interface TuxedoCredentials
{
    public String getUserName();

    public String getPassword();

    public byte[] getAuthData();
}
