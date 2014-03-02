package org.momentumjs.gradle.jsengine;

import static org.junit.Assert.*

import org.junit.Test
import org.momentumjs.gradle.jsengine.internal.DefaultJsEngineRegistry

/**
 * Created with IntelliJ IDEA.
 * User: rob
 * Date: 02/03/14
 * Time: 01:06
 * To change this template use File | Settings | File Templates.
 */
public class DefaultJsEngineRegistryTest {

    @Test
    public void testBestWithSingleName() {
        DefaultJsEngineRegistry registry = new DefaultJsEngineRegistry()
        registry.add(new DummyJsEngine("alpha", "1.2", "5"))
        registry.add(new DummyJsEngine("alpha", "1.5", "5"))
        registry.add(new DummyJsEngine("alpha", "1.3", "5"))
        registry.add(new DummyJsEngine("alpha", "1.4", "5"))

        // unsorted:
        JsEngine best = registry.getBestEngine();
        assertEquals("alpha", best.getDescriptor().getEngineName());
        assertEquals("1.2", best.getDescriptor().getEngineVersion());
        assertEquals("5", best.getDescriptor().getEcmaScriptVersion());

        // now sort and try again:
        registry.orderJsEngines();
        best = registry.getBestEngine();
        assertEquals("alpha", best.getDescriptor().getEngineName());
        assertEquals("1.5", best.getDescriptor().getEngineVersion());
        assertEquals("5", best.getDescriptor().getEcmaScriptVersion());
    }

    @Test
    public void testWithDifferentEcmaScriptVersions() {
        DefaultJsEngineRegistry registry = new DefaultJsEngineRegistry()
        // Okay, it'd be weird to comply with version 5.1 only in 1.3, but hey:
        registry.add(new DummyJsEngine("strange", "1.2", "3"))
        registry.add(new DummyJsEngine("strange", "1.5", "5"))
        registry.add(new DummyJsEngine("strange", "1.3", "5.1"))
        registry.add(new DummyJsEngine("strange", "1.4", "5"))

        // unsorted:
        JsEngine best = registry.getBestEngine();
        assertEquals("strange", best.getDescriptor().getEngineName());
        assertEquals("1.2", best.getDescriptor().getEngineVersion());
        assertEquals("3", best.getDescriptor().getEcmaScriptVersion());

        // now sort and try again:
        registry.orderJsEngines();
        best = registry.getBestEngine();
        assertEquals("strange", best.getDescriptor().getEngineName());
        assertEquals("1.3", best.getDescriptor().getEngineVersion());
        assertEquals("5.1", best.getDescriptor().getEcmaScriptVersion());
    }

    @Test
    public void testWithNullEcmaScriptVersion() {
        DefaultJsEngineRegistry registry = new DefaultJsEngineRegistry()
        registry.add(new DummyJsEngine("strange", "1.2", null))
        registry.add(new DummyJsEngine("strange", "1.5", "5"))
        registry.add(new DummyJsEngine("strange", "1.3", "5.1"))
        registry.add(new DummyJsEngine("strange", "1.4", null))

        // unsorted:
        JsEngine best = registry.getBestEngine();
        assertEquals("strange", best.getDescriptor().getEngineName());
        assertEquals("1.2", best.getDescriptor().getEngineVersion());
        assertEquals(null, best.getDescriptor().getEcmaScriptVersion());

        // now sort and try again:
        registry.orderJsEngines();
        best = registry.getBestEngine();
        assertEquals("strange", best.getDescriptor().getEngineName());
        assertEquals("1.3", best.getDescriptor().getEngineVersion());
        assertEquals("5.1", best.getDescriptor().getEcmaScriptVersion());
    }

    public void testFilter() {
        DefaultJsEngineRegistry registry = new DefaultJsEngineRegistry()
        registry.add(new DummyJsEngine("alpha", "2", "5"))
        registry.add(new DummyJsEngine("beta",  "1", null))
        registry.add(new DummyJsEngine("alpha", "3", "5.1"))
        registry.add(new DummyJsEngine("beta",  "3", null))
        registry.add(new DummyJsEngine("beta",  "2", null))
        registry.add(new DummyJsEngine("alpha", "4", "6"))
        registry.orderJsEngines();

        // 'best' engine overall has highest ecma version
        JsEngine best = registry.getBestEngine();
        assertEquals("alpha", best.getDescriptor().getEngineName());
        assertEquals("4", best.getDescriptor().getEngineVersion());
        assertEquals("6", best.getDescriptor().getEcmaScriptVersion());

        JsEngine bestBeta = registry.findBestEngine(new JsEngineFilter() {
            @Override
            boolean compatibleWith(JsEngineDescriptor descriptor) {
                return descriptor.engineName.equals("beta");
            }
        });
        assertEquals("beta", bestBeta.getDescriptor().getEngineName());
        assertEquals("3", bestBeta.getDescriptor().getEngineVersion());
        assertEquals(null, bestBeta.getDescriptor().getEcmaScriptVersion());
    }
}
