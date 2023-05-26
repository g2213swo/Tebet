package me.g2213swo.tebet.integration;

import java.util.List;

public class OCRResult {
    public List<ParsedResult> ParsedResults;
    public int OCRExitCode;
    public boolean IsErroredOnProcessing;
    public String ProcessingTimeInMilliseconds;
    public String SearchablePDFURL;

    public static class ParsedResult {
        public TextOverlay TextOverlay;
        public String TextOrientation;
        public int FileParseExitCode;
        public String ParsedText;
        public String ErrorMessage;
        public String ErrorDetails;
    }

    public static class TextOverlay {
        public List<Object> Lines;
        public boolean HasOverlay;
        public String Message;
    }
}