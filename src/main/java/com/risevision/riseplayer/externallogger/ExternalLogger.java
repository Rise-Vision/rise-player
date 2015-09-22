package com.risevision.riseplayer.externallogger;

import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class ExternalLogger {
  private static final String logUrl = "https://www.googleapis.com/bigquery/v2/" +
  "projects/client-side-events/datasets/Native_Events/tables/TABLE_ID/insertAll";

  public static void logExternal(InsertSchema schema) {
    try {
      Token.update();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    if (schema.getEvent() == null || schema.getEvent().equals("")) {
      throw new RuntimeException("No event specified");
    }

    
    byte[] json = schema.setInsertId().setTimestamp().getJson().getBytes();

    try {
      HttpURLConnection conn = (HttpURLConnection) getUrl().openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json"); 
      conn.setRequestProperty("Content-Length", String.valueOf(json.length));
      conn.setRequestProperty("Authorization", "Bearer " + Token.token);
      try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
        out.write(json);
      }
      conn.getInputStream().close();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static URL getUrl() {
    URL url;
    java.text.SimpleDateFormat fmt;
    fmt = new java.text.SimpleDateFormat("yyyyMMdd");
    fmt.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));

    String dt = fmt.format(new Date());
    try {
      return new URL(logUrl.replace("TABLE_ID", "events" + dt));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
