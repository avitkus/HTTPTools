package edu.unc.cs.httpTools.request;

import edu.unc.cs.httpTools.util.HTTPMethod;
import edu.unc.cs.httpTools.util.HTTPVersion;

/**
 *
 * @author Andrew Vitkus
 */
public interface IRequestLine {

    public HTTPVersion getProtocol();

    public HTTPMethod getMethod();

    public String getResource();

    public String getRequestLine();
}
