package mariomonday.backend.utils.jsonfilesource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;

class JsonArgumentsProvider extends AnnotationBasedArgumentsProvider<JsonFileSource> {

  @Override
  protected Stream<? extends Arguments> provideArguments(ExtensionContext context, JsonFileSource annotation) {
    String fileName = String.format(
      "%s/%s.json",
      context.getTestClass().map(Class::getSimpleName).orElse("unknown class"),
      context.getTestMethod().map(Method::getName).orElse("unknown method")
    );
    try {
      return convertJsonFileToTestData(fileName, annotation.value())
        .stream()
        .map(jsonTestData -> jsonTestData.toArguments(annotation.excludeDescriptor()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String readFromResources(String fileName) throws IOException {
    File resource = new ClassPathResource(fileName).getFile();
    byte[] byteArray = Files.readAllBytes(resource.toPath());
    return new String(byteArray);
  }

  private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  private static <T extends JsonTestData<?, ?>> List<T> convertJsonFileToTestData(String fileName, Class<T> clazz)
    throws JsonProcessingException, IOException {
    return objectMapper.readValue(
      readFromResources(fileName),
      objectMapper.getTypeFactory().constructParametricType(List.class, clazz)
    );
  }
}
