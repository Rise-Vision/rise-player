package com.risevision.riseplayer.externallogger;
import java.util.*;
import com.google.gson.Gson;
import com.risevision.riseplayer.*;

public class InsertSchema {
  String kind = "bigquery#tableDataInsertAllRequest";
  boolean skipInvalidRows = false;
  boolean ignoreUnknownValues = false;
  List<Row> rows;

  static class Row {
    String insertId;
    RowData json;
  }

  static class RowData {
    String event;
    String display_id;
    String player_version;
    String event_details;
    String os;
    String ip;
    String ts;
  }

  private InsertSchema(){}

  public static InsertSchema initialize() {
    InsertSchema schema = new InsertSchema();
    schema.rows = new ArrayList<Row>();
    Row row = new Row();
    row.json = new RowData();
    schema.rows.add(row);
    schema.setPlayerVersion(Globals.APPLICATION_VERSION);
    schema.setOS(System.getProperty("os.name"));
    schema.setDisplayId(Config.displayId);
    return schema;
  }

  public static InsertSchema withEvent(String event) {
    return InsertSchema.initialize().setEvent(event);
  }

  public InsertSchema setEvent(String event) {
    this.rows.get(0).json.event = event;
    return this;
  }

  public InsertSchema setEventDetails(String details) {
    this.rows.get(0).json.event_details = details;
    return this;
  }

  public InsertSchema setDisplayId(String id) {
    this.rows.get(0).json.display_id = id;
    return this;
  }

  public InsertSchema setPlayerVersion(String version) {
    this.rows.get(0).json.player_version = version;
    return this;
  }

  public InsertSchema setIP(String ip) {
    this.rows.get(0).json.ip = ip;
    return this;
  }

  public InsertSchema setOS(String os) {
    this.rows.get(0).json.os = os;
    return this;
  }

  public InsertSchema setTimestamp() {
    java.text.SimpleDateFormat fmt;
    fmt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    fmt.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
    this.rows.get(0).json.ts = fmt.format(new Date());
    return this;
  }

  public InsertSchema setInsertId() {
    this.rows.get(0).insertId = String.valueOf(Math.random()).substring(2);
    return this;
  }

  public String getEvent() {
    return this.rows.get(0).json.event;
  }

  public String getEventDetails() {
    return this.rows.get(0).json.event_details;
  }

  public String getDisplayId() {
    return this.rows.get(0).json.display_id;
  }

  public String getPlayerVersion() {
    return this.rows.get(0).json.player_version;
  }

  public String getIP() {
    return this.rows.get(0).json.ip;
  }

  public String getOS() {
    return this.rows.get(0).json.os;
  }

  public String getJson() {
    return (new Gson()).toJson(this);
  }
}
