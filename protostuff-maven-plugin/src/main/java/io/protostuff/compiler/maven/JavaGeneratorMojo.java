package io.protostuff.compiler.maven;

import com.google.common.collect.ImmutableMap;
import io.protostuff.compiler.model.ModuleConfiguration;
import io.protostuff.generator.ProtostuffCompiler;
import io.protostuff.generator.java.JavaGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.util.Collections.singletonList;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;

/**
 * @author Kostiantyn Shchepanovskyi
 */
@Mojo(name = "java")
public class JavaGeneratorMojo extends AbstractGeneratorMojo {

    public static final String GENERATED_SOURCES = "/generated-sources/proto";
    public static final String GENERATED_TEST_SOURCES = "/generated-test-sources/proto";
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaGeneratorMojo.class);

    @Parameter
    private File target;

    @Parameter(defaultValue = "java.util.concurrent.CompletableFuture")
    private String rpcReturnType;

    @Parameter(property = "threadSafe")
    private String[] threadSafe;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        ProtostuffCompiler compiler = new ProtostuffCompiler();
        final Path sourcePath = getSourcePath();
        String output = calculateOutput();
        ModuleConfiguration.Builder builder = ModuleConfiguration.newBuilder()
                .name("java")
                .includePaths(singletonList(sourcePath))
                .template(JavaGenerator.GENERATOR_NAME)
                .threadSafe(threadSafe)
                .options(ImmutableMap.<String, String>builder()
                        .put(JavaGenerator.SERVICE_RETURN_TYPE_OPTION, rpcReturnType)
                        .build())
                .output(output);
        PathMatcher protoMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.proto");
        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (protoMatcher.matches(file)) {
                        String protoFile = sourcePath.relativize(file).toString();
                        builder.addProtoFile(normalizeProtoPath(protoFile));
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Can not build source files list", e);
        }
        ModuleConfiguration moduleConfiguration = builder.build();

        LOGGER.debug("Module configuration = {}", moduleConfiguration);
        compiler.compile(moduleConfiguration);
        addGeneratedSourcesToProject(output);
    }

    private void addGeneratedSourcesToProject(String output) {
        // Include generated directory to the list of compilation sources
        if (GENERATE_TEST_SOURCES.id().equals(execution.getLifecyclePhase())) {
            project.addTestCompileSourceRoot(output);
        } else {
            project.addCompileSourceRoot(output);
        }
    }

    private String calculateOutput() {
        String output;
        if (target != null) {
            output = target.getAbsolutePath();
        } else {
            String phase = execution.getLifecyclePhase();
            String buildDirectory = project.getBuild().getDirectory();
            if (GENERATE_TEST_SOURCES.id().equals(phase)) {
                output = buildDirectory + GENERATED_TEST_SOURCES;
            } else {
                output = buildDirectory + GENERATED_SOURCES;
            }
        }
        LOGGER.debug("output = {}", output);
        return output;
    }

}
