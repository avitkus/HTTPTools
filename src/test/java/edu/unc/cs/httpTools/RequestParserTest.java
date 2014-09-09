package edu.unc.cs.httpTools;

import edu.unc.cs.httpTools.request.IRequest;
import edu.unc.cs.httpTools.request.RequestParser;
import edu.unc.cs.httpTools.request.exceptions.MalformedRequestException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vitkus
 */
public class RequestParserTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public RequestParserTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class RequestParser.
     *
     * @throws edu.unc.cs.httpTools.request.exceptions.MalformedRequestException
     */
    @Test
    public void testParse() throws MalformedRequestException {
        String request = "POST /cgi-bin/qtest HTTP/1.1\r\n"
                + "Host: aram\r\n" + "Accept-Language: en\r\n"
                + "Accept-Encoding: gzip\r\n"
                + "Keep-Alive: 300\r\n"
                + "Connection: keep-alive\r\n"
                + "Accept-Encoding: deflate\r\n"
                + "Content-Type: multipart/form-data; boundary=---------------------------287032381131322\r\n"
                + "Content-Length: 582\r\n"
                + "\r\n"
                + "-----------------------------287032381131322\r\n"
                + "Content-Disposition: form-data; name=\"datafile1\"; filename=\"r.gif\"\r\n"
                + "Content-Type: image/gif\r\n"
                + "\r\n"
                + "GIF87a.............,...........D..;\r\n"
                + "-----------------------------287032381131322\r\n"
                + "Content-Disposition: form-data; name=\"datafile2\"; filename=\"g.gif\"\r\n"
                + "Content-Type: image/gif\r\n"
                + "\r\n"
                + "GIF87a.............,...........D..;\r\n"
                + "-----------------------------287032381131322\r\n"
                + "Content-Disposition: form-data; name=\"datafile3\"; filename=\"b.gif\"\r\n"
                + "Content-Type: image/gif\r\n"
                + "\r\n"
                + "GIF87a\n.............,...........\rD\r\n..;\r\n"
                + "-----------------------------287032381131322--";
        RequestParser instance = new RequestParser();
        IRequest result = instance.parse(request);
        System.out.println(result.getRequest());
    }

}
