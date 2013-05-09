package org.netbeans.gradle.project.tasks;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kelemen Attila
 */
public class TaskVariableTest {
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static TaskVariable create(String name) {
        return new TaskVariable(name);
    }

    private static void testValidVariable(String name) {
        assertTrue(TaskVariable.isValidVariableName(name));

        TaskVariable var = create(name);
        assertEquals(name, var.getVariableName());
        assertEquals("${" + name + "}", var.getScriptReplaceConstant());

        assertEquals(var, var);
        assertEquals(var, create(name));
    }

    private static void testInvalidVariable(String name) {
        assertFalse(TaskVariable.isValidVariableName(name));

        try {
            create(name);
            fail("Expected: IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testValidVariable() {
        testValidVariable(".");
        testValidVariable("_");
        testValidVariable("-");
        testValidVariable("0");
        testValidVariable("9");
        testValidVariable("a");
        testValidVariable("A");
        testValidVariable("z");
        testValidVariable("Z");
        testValidVariable("m");
        testValidVariable("K");
        testValidVariable(".-_0123456789ABCDEFGHIJKLMNOPQSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    @Test
    public void testInvalidVariable() {
        testInvalidVariable("");
        testInvalidVariable("$");
        testInvalidVariable("{");
        testInvalidVariable("}");
        testInvalidVariable("afegeg$efeffe");
    }

    private static void assertNotEquals(Object value1, Object value2) {
        if (value1 != null) {
            assertFalse(value1.equals(value2));
        }

        if (value2 != null) {
            assertFalse(value2.equals(value1));
        }
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(create("ABC"), null);
        assertNotEquals(create("ABC"), create("abc"));
        assertNotEquals(create("ABC"), "ABC");
    }
}
