package com.risevision.riseplayer.externallogger;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.IOException;

public class ExternalLoggerIT {
  @Test public void itLogs() throws IOException {
    InsertSchema schema = InsertSchema.withEvent("testEvent")
    .setEventDetails("testEventDetails");

    ExternalLogger.logExternal(schema);
  }
}

