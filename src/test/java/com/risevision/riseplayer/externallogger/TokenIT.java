package com.risevision.riseplayer.externallogger;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.IOException;

public class TokenIT {
  @Test public void itRefreshesToken() throws IOException {
    Token.update();
    assertThat(Token.token, notNullValue());
    assertThat(Token.token, not(containsString("\"")));
  }

  @Test public void itSavesToken() throws IOException {
    Token.update();
    String firstToken = Token.token;
    Token.update();
    assertThat(Token.token, equalTo(firstToken));
  }
}
