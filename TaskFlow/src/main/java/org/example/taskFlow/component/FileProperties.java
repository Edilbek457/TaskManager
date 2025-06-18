package org.example.taskFlow.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "file.valid")
public class FileProperties {

    private long maxSize;
    private Set<String> allowedMimeTypes;

}
