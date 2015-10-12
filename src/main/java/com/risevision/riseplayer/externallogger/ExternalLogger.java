package com.risevision.riseplayer.externallogger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.risevision.riseplayer.Log;

public class ExternalLogger {
  private static final String logUrl = "https://www.googleapis.com/bigquery/v2/" +
  "projects/client-side-events/datasets/Native_Events/tables/TABLE_ID/insertAll";

  public static void logExternal(InsertSchema schema) {
    try {
      Token.update();
    } catch (IOException e) {
      Log.error("External logger token update: " + e.getMessage());
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
      Log.error("External logger event save: " + e.getMessage());
    }
  }

  private static URL getUrl() throws IOException {
    java.text.SimpleDateFormat fmt;
    fmt = new java.text.SimpleDateFormat("yyyyMMdd");
    fmt.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));

    String dt = fmt.format(new Date());
    
    return new URL(logUrl.replace("TABLE_ID", "events" + dt));
  }
}
