package com.risevision.riseplayer.externallogger;

import java.net.*;
import java.util.Date;
import java.util.regex.*;
import java.io.*;

class Token {
  static final String tokenUrl = "https://www.googleapis.com/oauth2/v3/token";
  static final byte[] postData = "client_id=1088527147109-6q1o2vtihn34292pjt4ckhmhck0rk0o7.apps.googleusercontent.com&client_secret=nlZyrcPLg6oEwO9f9Wfn29Wh&refresh_token=1/xzt4kwzE1H7W9VnKB8cAaCx6zb4Es4nKEoqaYHdTD15IgOrJDtdun6zK6XiATCKT&grant_type=refresh_token".getBytes();
  static String len = String.valueOf(postData.length);

  static String token;
  static long lastUpdate;
  static final String regexp = "access_token\": \"(.*)\"";
  static final Pattern pattern = Pattern.compile(regexp);

  static void update() throws IOException {
    if ((new Date()).getTime() - lastUpdate < 3580000) {
      return;
    }

    URL url = new URL(tokenUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
    conn.setRequestProperty("Content-Length", len);
    try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
      out.write(postData);
    }

    try (Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      StringBuffer data = new StringBuffer();

      for (int c = in.read(); c != -1; c = in.read()) {
        data.append((char)c);
      }
 
      Matcher matcher = pattern.matcher(data.toString());
      if (!matcher.find()) {
        throw new RuntimeException("Unexpected return data" + matcher.toString());
      }

      token = matcher.group(1);
      lastUpdate = (new Date()).getTime();
    }
  }
}
