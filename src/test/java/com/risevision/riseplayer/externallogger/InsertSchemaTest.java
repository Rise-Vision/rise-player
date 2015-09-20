package com.risevision.riseplayer.externallogger;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class InsertSchemaTest {
  @Test public void itBuildsSchemaDetails() {
    String schema = InsertSchema.initialize()
    .setEvent("testEvent")
    .setDisplayId("testDisplayId")
    .setEventDetails("testEventDetails")
    .getJson();

    assertThat(schema, containsString("\"player_version\""));
    assertThat(schema, containsString("\"event_details\":\"testEventDetails\""));
  }
}
