package edu.unc.cs.httpTools.request;

import edu.unc.cs.httpTools.request.exceptions.MalformedRequestException;

/**
 *
 * @author Andrew Vitkus
 */
public interface IRequestParser {

    public IRequest parse(String request) throws MalformedRequestException;
}
