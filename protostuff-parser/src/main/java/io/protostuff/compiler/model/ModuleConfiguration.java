package io.protostuff.compiler.model;

import com.google.common.base.MoreObjects;

import java.nio.file.Path;
import java.util.*;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class ModuleConfiguration {

    private final String name;
    private final List<Path> includePaths;
    private final List<String> protoFiles;
    private final String template;
    private final String output;
    private final List<String> threadSafe;
    private final Map<String, String> options;

    private ModuleConfiguration(Builder builder) {
        name = builder.name;
        includePaths = builder.includePaths;
        protoFiles = builder.protoFiles;
        template = builder.template;
        output = builder.output;
        threadSafe = builder.threadSafe;
        options = builder.options;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ModuleConfiguration copy) {
        Builder builder = new Builder();
        builder.name = copy.name;
        builder.includePaths = copy.includePaths;
        builder.protoFiles = copy.protoFiles;
        builder.template = copy.template;
        builder.output = copy.output;
        builder.threadSafe = copy.threadSafe;
        builder.options = copy.options;
        return builder;
    }

    public List<String> getProtoFiles() {
        return protoFiles;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public String getOutput() {
        return output;
    }

    public List<Path> getIncludePaths() {
        return includePaths;
    }

    public List<String> getThreadSafe() {
        return threadSafe;
    }

    public Map<String, String> getOptions() {
        if (options == null) {
            return Collections.emptyMap();
        }
        return options;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("includePaths", includePaths)
                .add("protoFiles", protoFiles)
                .add("template", template)
                .add("output", output)
                .toString();
    }

    public static final class Builder {

        private String name;
        private List<Path> includePaths;
        private List<String> protoFiles;
        private String template;
        private String output;
        private Map<String, String> options;
        private List<String> threadSafe;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder includePaths(List<Path> includePaths) {
            this.includePaths = includePaths;
            return this;
        }

        public Builder protoFiles(List<String> protoFiles) {
            this.protoFiles = protoFiles;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder output(String output) {
            this.output = output;
            return this;
        }

        public Builder options(Map<String, String> val) {
            options = val;
            return this;
        }

        public Builder threadSafe(String[] threadSafe) {
            this.threadSafe = Arrays.asList(threadSafe);
            return this;
        }

        public Builder addProtoFile(String protoFile) {
            if (protoFiles == null) {
                protoFiles = new ArrayList<>();
            }
            protoFiles.add(protoFile);
            return this;
        }

        public ModuleConfiguration build() {
            return new ModuleConfiguration(this);
        }
    }
}
