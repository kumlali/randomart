package kokkarinen.ilkka;

import java.text.*;
import java.io.*;
import java.lang.reflect.*;

public class ChannelParser {
    
    private String[] buffer;
    private int bufferIdx;
    private BufferedReader br;
    private boolean finished;
    private int lineNumber;
    
    private void readNextLine(boolean cantEnd) throws ParseException, IOException {
        String line;
        do {
            line = br.readLine();
            ++lineNumber;
            if(line == null) {
                if(cantEnd) {
                    throw new ParseException("Unexpected end of file", lineNumber);
                }
                else { finished = true; return; }
            }
            line = line.trim();
        } while(line.length() == 0 || line.charAt(0) == '#');
        
        buffer = line.split("(\\s)+");    
        bufferIdx = 0;
    }
    
    private String peek(boolean cantEnd) throws ParseException, IOException {
        while(!finished && (buffer == null || bufferIdx >= buffer.length)) {
            readNextLine(cantEnd);
        }
        if(finished) { return null; }
        return buffer[bufferIdx];
    }
    
    private String advance(boolean cantEnd) throws ParseException, IOException {
        String current = peek(cantEnd);
        ++bufferIdx;
        return current;
    }
    
    public Node[] parseChannels() throws ParseException, IOException {
        Node[] nodes = new Node[3];
        for(int idx = 0; idx < 3; idx++) {
            nodes[idx] = parseNode(true);
        }
        return nodes;
    }
    
    private Node parseNode(boolean cantEnd) throws ParseException, IOException {
        String className = advance(cantEnd);
        Class klass;
        Node n;
        try {
            klass = Class.forName(className);
            n = (Node)(klass.newInstance());
        } catch(Exception e) {
            throw new ParseException("Unable to use class " + className + ": " + e, lineNumber);
        }
        Field[] fields = klass.getDeclaredFields();
        boolean itsAField;
        do {
            String fieldName = peek(false);
            if(fieldName == null) { break; }
            itsAField = false;
            for(Field f: fields) {
                if(fieldName.equals(f.getName())) {
                    itsAField = true;
                    advance(true);
                    String fieldValue = advance(true);
                    try {
                        if(f.getType() == double.class) {
                            f.setDouble(n, Double.parseDouble(fieldValue));
                        }
                        else if(f.getType() == boolean.class) {
                            f.setBoolean(n, fieldValue.equals("true"));
                        }
                        else if(f.getType() == int.class) {
                            f.setInt(n, Integer.parseInt(fieldValue));
                        }
                        else if(f.getType() == String.class) {
                            f.set(n, fieldValue);
                        }
                    }
                    catch(Exception e) {
                        throw new ParseException("Error setting field " + fieldName, lineNumber);
                    }
                    break;
                }
            }
        } while(itsAField);
        
        Node[] children = new Node[n.getArity()];
        for(int i = 0; i < children.length; i++) {
            children[i] = parseNode(true);
        }
        n.setChildren(children);
        return n;
    }
    
    public ChannelParser(BufferedReader br) {
        this.br = br;
    }
}