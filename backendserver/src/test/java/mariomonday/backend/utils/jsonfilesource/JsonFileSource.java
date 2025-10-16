package mariomonday.backend.utils.jsonfilesource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * An annotation that points the junit test at a file in the test resources matching the path TestName/MethodName.json
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JsonFileSources.class)
@ArgumentsSource(JsonArgumentsProvider.class)
public @interface JsonFileSource {
  Class<? extends JsonTestData<?, ?>> value();

  boolean excludeDescriptor() default false;
}
