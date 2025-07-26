package com.alibaba.qlexpress4.pf4j;

import com.alibaba.qlexpress4.ClassSupplier;
import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;

public class Pf4jClassSupplierTest {
    @Test
    public void testPluginClassSupplier()
        throws Exception {
        // tag::pluginClassSupplier[]
        // Specify plugin directory (test-plugins directory under test resources)
        Path pluginsDir = new File("src/test/resources/test-plugins").toPath();
        PluginManager pluginManager = new DefaultPluginManager(pluginsDir);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        
        // Get the PluginClassLoader of the first plugin
        PluginWrapper plugin = pluginManager.getPlugins().get(0);
        ClassLoader pluginClassLoader = plugin.getPluginClassLoader();
        
        // Custom class supplier using plugin ClassLoader
        ClassSupplier pluginClassSupplier = clsName -> {
            try {
                return Class.forName(clsName, true, pluginClassLoader);
            }
            catch (ClassNotFoundException | NoClassDefFoundError e) {
                return null;
            }
        };
        
        InitOptions options = InitOptions.builder()
            .securityStrategy(QLSecurityStrategy.open())
            .classSupplier(pluginClassSupplier)
            .build();
        Express4Runner runner = new Express4Runner(options);
        
        String script = "import com.alibaba.qlexpress4.pf4j.TestPluginInterface; TestPluginInterface.TEST_CONSTANT";
        Object result = runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        
        Assert.assertEquals("Hello from PF4J Plugin!", result.toString());
        // end::pluginClassSupplier[]
    }
}