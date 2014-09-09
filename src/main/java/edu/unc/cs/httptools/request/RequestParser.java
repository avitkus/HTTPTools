package edu.unc.cs.httpTools.request;

import edu.unc.cs.httpTools.request.exceptions.IllegalBoundaryException;
import edu.unc.cs.httpTools.request.exceptions.IllegalRequestMethodException;
import edu.unc.cs.httpTools.request.exceptions.IllegalRequestVersionException;
import edu.unc.cs.httpTools.request.exceptions.MalformedRequestException;
import edu.unc.cs.httpTools.request.exceptions.MalformedRequestLineException;
import edu.unc.cs.httpTools.util.HTTPMethod;
import edu.unc.cs.httpTools.util.HTTPVersion;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

public class RequestParser implements IRequestParser {

    @Override
    public IRequest parse(String request) throws MalformedRequestException {
        String[] parts = split(request);
        //Arrays.stream(parts).forEach((part) -> System.out.println("***\n" + part));
        Map<String, String[]> headers = new HashMap<String, String[]>(5);
        for (String line : parts[1].split("\r\n")) {
            String key = line.substring(0, line.indexOf(':'));
            String[] values = line.substring(line.indexOf(':') + 1).split(";");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            if (headers.containsKey(key)) {
                String[] old = headers.get(key);
                LinkedHashSet<String> vals = new LinkedHashSet<String>(old.length + values.length);
                vals.addAll(asList(old));
                vals.addAll(asList(values));
                headers.put(key, vals.toArray(new String[vals.size()]));
            } else {
                headers.put(key, values);
            }
        }
        String boundary = getBoundary(headers);
        HTTPMethod method = getMethod(parts[0]);
        String resource = getResource(parts[0]);
        HTTPVersion version = getVersion(parts[0]);
        MultipartRequestFactory mrf = MultipartRequestFactory.getCustomBoundaryAndMethod(boundary, version, method, resource);
        headers.remove("Content-Length");
        for (Entry<String, String[]> entry : headers.entrySet()) {
            mrf.addHeader(entry.getKey(), entry.getValue());
        }
        for (BodyPartData partData : getParts(parts[2], boundary)) {
            mrf.addPart(partData.getData(), partData.getType(), partData.getDisposition(), partData.getDetails());
        }
        return mrf.buildRequest();
    }

    private String getBoundary(Map<String, String[]> headers) throws IllegalBoundaryException {
        String[] contentType = headers.getOrDefault("Content-Type", new String[]{});

        for (String type : contentType) {
            if (type.startsWith("boundary")) {
                try {
                    return type.split("=")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalBoundaryException();
                }
            }
        }
        throw new IllegalBoundaryException();
    }

    private HTTPMethod getMethod(String info) throws IllegalRequestMethodException {
        String[] parts = info.split("\\s");
        try {
            return HTTPMethod.valueOf(parts[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalRequestMethodException();
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestMethodException();
        }
    }

    private BodyPartData[] getParts(String body, String boundary) {
        String[] split = body.split("--" + boundary);
        ArrayList<BodyPartData> parts = new ArrayList<BodyPartData>(split.length);
        for (int i = 1; i < split.length - 1; i++) {
            String[] partParts = split[i].split("\r\n\r\n", 2);
            String disposition = "";
            String type = "";
            String[] details = new String[]{};
            String data = "";

            //System.out.println("==========");
            if (partParts.length == 2) {
                String[] headerParts = partParts[0].split("[\r\n]+");

                for (String headerPart : headerParts) {
                    if (!headerPart.isEmpty()) {
                        //System.out.println(headerPart);
                        String[] headerPartParts = magic(headerPart); // I'm so sorry for this naming...but that is what it is
                        //System.out.println(headerPartParts[0]);
                        if (headerPartParts[0].equals("Content-Disposition")) {
                            disposition = headerPartParts[1];
                            details = Arrays.copyOfRange(headerPartParts, 2, headerPartParts.length);
                        } else if (headerPartParts[0].equals("Content-Type")) {
                            type = headerPartParts[1];
                        }
                    }
                }
            }
            data = partParts[partParts.length - 1];
            if (data.endsWith("\r\n")) {
                data = data.substring(0, data.length() - 2);
            }

            //System.out.println(disposition + ", " + type + ", " + data + ", " + Arrays.toString(details));
            parts.add(new BodyPartData(disposition, type, details, data));
            //System.out.println("1:" + parts.get(parts.size() - 1).getData());
            //parts.get(parts.size() - 1).getData().chars().forEach((c) -> System.out.println(Character.getName(c)));
            //System.out.println("2:" + Arrays.toString(parts.get(parts.size() - 1).getDetails()));
            //System.out.println("3:" + parts.get(parts.size() - 1).getDisposition());
            //System.out.println("4:" + parts.get(parts.size() - 1).getType());
            //Arrays.stream(partParts).forEach((s) -> System.out.println("--- " + Arrays.toString(magic(s))));
        }
        return parts.toArray(new BodyPartData[parts.size()]);
    }

    private String getResource(String info) throws MalformedRequestLineException {
        String[] parts = info.split("\\s");
        try {
            return parts[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalRequestMethodException();
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestMethodException();
        }
    }

    private HTTPVersion getVersion(String info) throws IllegalRequestVersionException {
        String[] parts = info.split("\\s");
        try {
            return HTTPVersion.valueOf(parts[2].replaceAll("[//.////]*", ""));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalRequestVersionException();
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestVersionException();
        }
    }

    /**
     * This splits up lines. I can't think of how to word it correctly, thus it
     * performs magic.
     *
     * @param line
     *
     * @return parts of the line
     */
    private String[] magic(String line) {
        //System.out.println("***"+line+"***");
        String[] split = line.split("[\r\n]+", 2);
        String args = split[0].isEmpty() ? split[split.length - 1] : line;
        ArrayList<String> parts = new ArrayList<String>(5);
        int colLoc = args.indexOf(':');
        if (colLoc > 0 && !split[0].isEmpty()) {
            parts.add(args.substring(0, colLoc));
            args = args.substring(args.indexOf(' ') + 1);
        }
        int semLoc = args.indexOf(';');
        int len = args.length();
        split = args.split("[\r\n]+");
        int check = semLoc >= 0 ? semLoc : args.endsWith("\r\n") ? len - 2 : len;
        parts.add(args.substring(0, check));
        args = args.substring(Math.min(check + 1, len));

        StringBuilder thing = new StringBuilder(10);
        StringReader sr = null;
        try {
            sr = new StringReader(args);
            boolean inQuot = false;
            boolean out = true;
            boolean outOkay = false;

            int prev = sr.read();
            int cur;
            for (cur = sr.read(); cur != -1; cur = sr.read()) {
                if (inQuot) {
                    if (cur == '"') {
                        inQuot = false;
                        out = false;
                        if (thing.length() > 0) {
                            parts.add(thing.toString());
                            thing.setLength(0);
                        }
                    } else {
                        thing.append((char) cur);
                    }
                } else {
                    if (!outOkay) {
                        outOkay = Character.isAlphabetic(prev);
                    }
                    if (cur == '"') {
                        if (thing.length() > 0) {
                            parts.add(thing.toString());
                            thing.setLength(0);
                        }
                        inQuot = true;
                    } else if (out && outOkay) {
                        thing.append((char) prev);
                    } else {
                        out = true;
                        outOkay = false;
                    }
                }
                prev = cur;
            }
            if (prev != -1 && out) {
                thing.append((char) prev);
            }
            if (thing.length() > 0) {
                parts.add(thing.toString());
            }
        } catch (IOException e) {

        } finally {
            if (sr != null) {
                sr.close();
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    private String[] split(String request) {
        request = request.trim();
        int infoEnd = request.indexOf("\r\n");
        String requestInfo = request.substring(0, infoEnd);
        String[] parts = request.split("[\r\n]+", 2)[1].split("\r\n\r\n", 2);
        String[] split = new String[3];
        split[0] = requestInfo;
        split[1] = parts[0];
        if (parts.length > 1) {
            split[2] = parts[1];
        } else {
            split[2] = "";
        }
        return split;
    }

    private class BodyPartData {
        private final String data;
        private final String[] details;
        private final String disposition;
        private final String type;

        BodyPartData(String disposition, String type, String[] details, String data) {
            this.disposition = disposition;
            this.type = type;
            this.details = details;
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public String[] getDetails() {
            return Arrays.copyOf(details, details.length);
        }

        public String getDisposition() {
            return disposition;
        }

        public String getType() {
            return type;
        }
    }
}
