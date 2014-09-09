package edu.unc.cs.httpTools.response;

import edu.unc.cs.httpTools.util.HTTPStatusCode;
import edu.unc.cs.httpTools.util.HTTPVersion;

public class Status implements IStatus {

    private final HTTPVersion protocol;
    private final HTTPStatusCode status;

    protected Status(HTTPVersion protocol, HTTPStatusCode status) {
        this.protocol = protocol;
        this.status = status;
    }

    @Override
    public HTTPVersion getProtocol() {
        return protocol;
    }

    @Override
    public HTTPStatusCode getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return protocol.getName() + " " + status.getNumber() + " " + status.getMeaning() + "\r\n";
    }
}
