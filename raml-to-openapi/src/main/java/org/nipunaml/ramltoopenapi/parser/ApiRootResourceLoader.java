package org.nipunaml.ramltoopenapi.parser;

import org.raml.v2.api.loader.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Resolves includes the way Anypoint Platform does, against the API root directory.
 * raml-parser-2 resolves every include against the including file, so it cannot find:
 * <ul>
 *   <li>{@code exchange_modules/...} paths written in a nested file; Exchange dependencies always live under the
 *       API root</li>
 *   <li>root-relative paths such as {@code /examples/order.raml}, which it reads as absolute file system paths</li>
 * </ul>
 * Use it after the default loaders, so it only serves includes they could not find.
 */
public class ApiRootResourceLoader implements ResourceLoader {

    private static final String EXCHANGE_MODULES = "exchange_modules/";

    private final File apiRoot;

    public ApiRootResourceLoader(File apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override
    public InputStream fetchResource(String resourceName) {
        String name = resourceName.replace('\\', '/');
        int exchangeModulesIndex = name.lastIndexOf(EXCHANGE_MODULES);
        if (exchangeModulesIndex >= 0) {
            return open(new File(apiRoot, name.substring(exchangeModulesIndex)));
        }
        if (name.startsWith("/")) {
            return open(new File(apiRoot, name.substring(1)));
        }
        return null;
    }

    private static InputStream open(File file) {
        if (!file.isFile()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
