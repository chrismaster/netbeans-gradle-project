package org.netbeans.gradle.project.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.gradle.model.util.ConstructableWeakRef;
import org.netbeans.gradle.model.util.NbSupplier5;
import org.openide.filesystems.FileUtil;

public final class DefaultUrlFactory {
    private static final Logger LOGGER = Logger.getLogger(DefaultUrlFactory.class.getName());

    private static final NbSupplier5<DefaultUrlFactory> DEFAULT_REF = new ConstructableWeakRef<>(DefaultUrlFactory::new);
    private static final NbSupplier5<UrlFactory> DEFAULT_GENERIC_REF = new ConstructableWeakRef<>(() -> {
        return DEFAULT_REF.get()::toUrl;
    });
    private static final NbSupplier5<UrlFactory> DEFAULT_DIR_REF = new ConstructableWeakRef<>(() -> {
        return DEFAULT_REF.get()::toUrlDir;
    });

    private final Function<? super File, ? extends URL> urlCreator;
    private final ConcurrentMap<File, URL> cache;

    public DefaultUrlFactory() {
        this(FileUtil::urlForArchiveOrDir);
    }

    public DefaultUrlFactory(Function<? super File, ? extends URL> urlCreator) {
        this.urlCreator = Objects.requireNonNull(urlCreator, "urlCreator");
        this.cache = new ConcurrentHashMap<>(256);
    }

    public static URL urlForArchiveOrDir(File entry) {
        return getDefaultArchiveOrDirFactory().toUrl(entry);
    }

    public static UrlFactory getDefaultArchiveOrDirFactory() {
        return DEFAULT_GENERIC_REF.get();
    }

    public static UrlFactory getDefaultDirFactory() {
        return DEFAULT_DIR_REF.get();
    }

    private URL toUrl(File entry, boolean knownDir) {
        URL result = cache.get(entry);
        if (result != null) {
            return result;
        }

        result = urlCreator.apply(entry);
        if (result == null) {
            return null;
        }

        if (knownDir) {
            String urlStr = result.toExternalForm();
            if (!urlStr.endsWith("/")) {
                try {
                    result = new URL(urlStr + "/");
                } catch (MalformedURLException ex) {
                    LOGGER.log(Level.INFO, "Cannot set directory URL: " + result, ex);
                    // Go on and use whatever we have then.
                }
            }
        }

        URL prevValue = cache.putIfAbsent(entry, result);
        return prevValue != null ? prevValue : result;
    }

    public URL toUrlDir(File entry) {
        return toUrl(entry, true);
    }

    public URL toUrl(File entry) {
        return toUrl(entry, false);
    }
}
