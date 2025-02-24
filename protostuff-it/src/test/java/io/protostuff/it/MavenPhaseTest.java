package io.protostuff.it;

import io.protostuff.it.test.TestGenerateTestSourcesPhase;
import org.junit.Test;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class MavenPhaseTest {

    @Test
    public void testGenerateTestSources() throws Exception {
        // check that class generated during 'generate-test-sources' exists
        // otherwise we will get compilation error
        new TestGenerateTestSourcesPhase();
    }

    @Test
    public void testGenerateSources() throws Exception {
        // check that class generated during 'generate-sources' exists
        // otherwise we will get compilation error
        new TestGenerateTestSourcesPhase();
    }
}
