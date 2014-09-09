package edu.unc.cs.httpTools.response;

import edu.unc.cs.httpTools.util.HTTPMethod;
import edu.unc.cs.httpTools.util.HTTPStatusCode;
import edu.unc.cs.httpTools.util.HTTPVersion;

/**
 *
 * @author Andrew Vitkus
 */
public interface IStatus {

    public HTTPVersion getProtocol();

    public HTTPStatusCode getStatus();
}
