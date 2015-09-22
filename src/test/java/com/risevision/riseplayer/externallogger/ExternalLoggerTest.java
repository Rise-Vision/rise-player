package com.risevision.riseplayer.externallogger;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ExternalLoggerTest {
  @Test public void itExists() {
    ExternalLogger extLogger = new ExternalLogger();
    assertThat(extLogger, isA(ExternalLogger.class));
  }
}
