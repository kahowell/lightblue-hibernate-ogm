package com.redhat.lightblue.hibernate.ogm;

import java.util.List;

import org.hibernate.ogm.utils.OgmTestRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.test.utils.AbstractCRUDControllerWithRest;

/**
 * A rough cut of an idea what would allow Lightblue to execute as a JUnit runner rather
 * than using inheritance. I expect this will be cleaned up and ultimately live some place
 * else, but for now this will work.
 *
 * @author dcrissman
 */
public class LightblueOgmJUnitRunner extends OgmTestRunner {

    private ArtificialCRUDControllerWithRest controller;

    private Object testInstance = null;
    private final TestClass controllerTestClass;

    /**
     * Test case should implement this interface.
     *
     * @author dcrissman
     */
    public interface LightblueTestMethods {
        void setLightblueClient(LightblueClient client);

        JsonNode[] getMetadataJsonNodes() throws Exception;
    }

    public LightblueOgmJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        controllerTestClass = new TestClass(ArtificialCRUDControllerWithRest.class);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        Statement parentStatement = super.withAfterClasses(statement);
        List<FrameworkMethod> befores = controllerTestClass.getAnnotatedMethods(BeforeClass.class);

        return befores.isEmpty() ? statement : new RunBefores(parentStatement, befores, null);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target,
            Statement statement) {
        Statement parentStatement = super.withBefores(method, target, statement);
        List<FrameworkMethod> befores = controllerTestClass.getAnnotatedMethods(Before.class);

        return befores.isEmpty() ? parentStatement : new RunBefores(parentStatement, befores, controller);
    }

    @Override
    protected Statement withAfters(FrameworkMethod method, Object target,
            Statement statement) {
        Statement parentStatement = super.withAfters(method, target, statement);
        List<FrameworkMethod> afters = controllerTestClass.getAnnotatedMethods(After.class);

        return afters.isEmpty() ? parentStatement : new RunAfters(parentStatement, afters, controller);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        Statement parentStatement = super.withAfterClasses(statement);
        List<FrameworkMethod> afters = controllerTestClass.getAnnotatedMethods(AfterClass.class);

        return afters.isEmpty() ? statement : new RunAfters(parentStatement, afters, null);
    }

    @Override
    protected List<TestRule> classRules() {
        List<TestRule> classRules = super.classRules();
        classRules.addAll(controllerTestClass.getAnnotatedMethodValues(null, ClassRule.class, TestRule.class));
        classRules.addAll(controllerTestClass.getAnnotatedFieldValues(null, ClassRule.class, TestRule.class));
        return classRules;
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = createTestInstance();
        controller = new ArtificialCRUDControllerWithRest(test);
        return test;
    }

    protected Object createTestInstance() throws Exception {
        if (testInstance == null) {
            testInstance = super.createTest();
        }
        return testInstance;
    }

    private class ArtificialCRUDControllerWithRest extends AbstractCRUDControllerWithRest {

        public ArtificialCRUDControllerWithRest(Object test) throws Exception {
            super();

            if (test instanceof LightblueTestMethods) {
                ((LightblueTestMethods) test).setLightblueClient(getLightblueClient());
            }
        }

        @Override
        protected JsonNode[] getMetadataJsonNodes() throws Exception {
            Object test = createTestInstance();

            if (test instanceof LightblueTestMethods) {
                return ((LightblueTestMethods) test).getMetadataJsonNodes();
            }

            return null;
        }

    }

}
