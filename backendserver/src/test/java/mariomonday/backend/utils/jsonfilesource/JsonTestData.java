package mariomonday.backend.utils.jsonfilesource;

import org.junit.jupiter.params.provider.Arguments;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public abstract class JsonTestData<InputType, ExpectedType> {

  protected InputType input;
  protected ExpectedType expected;

  protected String descriptor;

  public String getDescriptor() {
    if (descriptor == null) {
      return "Missing descriptor";
    }
    return descriptor;
  }

  Arguments toArguments(boolean excludeDescriptor) {
    if (excludeDescriptor) {
      return Arguments.of(getInput(), getExpected());
    } else {
      return Arguments.of(getDescriptor(), getInput(), getExpected());
    }
  }
}
