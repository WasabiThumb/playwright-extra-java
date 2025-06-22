package io.github.wasabithumb.playwright;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.impl.Connection;
import com.microsoft.playwright.impl.PipeTransport;
import com.microsoft.playwright.impl.PlaywrightImpl;
import com.microsoft.playwright.impl.Transport;
import com.microsoft.playwright.impl.driver.Driver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApiStatus.Internal
final class PlaywrightExtraHacks {

    private static final boolean OK;
    private static final Throwable INIT_ERROR;
    private static final Constructor<?> M_PIPE_TRANSPORT_INIT;
    private static final Constructor<?> M_CONNECTION_INIT;
    private static final Method M_CONNECTION_CLOSE;
    private static final Field F_PLAYWRIGHT_IMPL_DRIVER_PROCESS;
    private static final Method M_PLAYWRIGHT_IMPL_INIT_SHARED_SELECTORS;
    private static final Field F_CHANNEL_OWNER_CONNECTION;
    static {
        boolean ok = true;
        Throwable initError = null;
        Constructor<?> mPipeTransportInit = null;
        Constructor<?> mConnectionInit = null;
        Method mConnectionClose = null;
        Field fPlaywrightImplDriverProcess = null;
        Method mPlaywrightImplInitSharedSelectors = null;
        Class<?> cChannelOwner;
        Field fChannelOwnerConnection = null;

        try {
            mPipeTransportInit = PipeTransport.class.getDeclaredConstructor(InputStream.class, OutputStream.class);
            mPipeTransportInit.setAccessible(true);

            mConnectionInit = Connection.class.getDeclaredConstructor(Transport.class, Map.class);
            mConnectionInit.setAccessible(true);
            mConnectionClose = Connection.class.getDeclaredMethod("close");
            mConnectionClose.setAccessible(true);

            fPlaywrightImplDriverProcess = PlaywrightImpl.class.getDeclaredField("driverProcess");
            fPlaywrightImplDriverProcess.setAccessible(true);
            mPlaywrightImplInitSharedSelectors = PlaywrightImpl.class.getDeclaredMethod("initSharedSelectors", PlaywrightImpl.class);
            mPlaywrightImplInitSharedSelectors.setAccessible(true);

            cChannelOwner = Class.forName(
                    "com.microsoft.playwright.impl.ChannelOwner",
                    true,
                    PlaywrightImpl.class.getClassLoader()
            );
            fChannelOwnerConnection = cChannelOwner.getDeclaredField("connection");
            fChannelOwnerConnection.setAccessible(true);
        } catch (ReflectiveOperationException | SecurityException e) {
            ok = false;
            initError = e;
        }

        OK = ok;
        INIT_ERROR = initError;
        M_PIPE_TRANSPORT_INIT = mPipeTransportInit;
        M_CONNECTION_INIT = mConnectionInit;
        M_CONNECTION_CLOSE = mConnectionClose;
        F_PLAYWRIGHT_IMPL_DRIVER_PROCESS = fPlaywrightImplDriverProcess;
        M_PLAYWRIGHT_IMPL_INIT_SHARED_SELECTORS = mPlaywrightImplInitSharedSelectors;
        F_CHANNEL_OWNER_CONNECTION = fChannelOwnerConnection;
    }

    //

    private static void checkOk() {
        if (OK) return;
        throw new AssertionError("Failed to access Playwright internals", INIT_ERROR);
    }

    /**
     * Should be functionally identical to {@link PlaywrightImpl#createImpl(Playwright.CreateOptions, boolean)},
     * except with Logger support
     */
    static @NotNull PlaywrightImpl createImpl(@Nullable Map<String, String> env, @NotNull Logger logger) {
        checkOk();
        if (env == null) env = Collections.emptyMap();
        Driver driver = Driver.ensureDriverInstalled(env, true);
        try {
            ProcessBuilder pb = driver.createProcessBuilder();
            pb.command().add("run-driver");
            Process p = pb.start();

            // Where the magic happens
            LogWorker worker = new LogWorker(p.getErrorStream(), logger);
            worker.start();

            Object pipeTransport = M_PIPE_TRANSPORT_INIT.newInstance(p.getInputStream(), p.getOutputStream());
            Object connection = M_CONNECTION_INIT.newInstance(pipeTransport, env);

            PlaywrightImpl result = ((Connection) connection).initializePlaywright();
            F_PLAYWRIGHT_IMPL_DRIVER_PROCESS.set(result, p);
            M_PLAYWRIGHT_IMPL_INIT_SHARED_SELECTORS.invoke(result, (Object) null);
            return result;
        } catch (IOException e) {
            throw new PlaywrightException("Failed to launch driver", e);
        } catch (InvocationTargetException | ExceptionInInitializerError e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            throw new PlaywrightException("Fatal exception while launching driver", e);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Unexpected reflection error", e);
        }
    }

    /**
     * Should be functionally identical to {@link PlaywrightImpl#close()},
     * except with Logger support
     */
    static void closeImpl(@NotNull Playwright impl, @NotNull Logger logger) {
        checkOk();
        try {
            Object connection = F_CHANNEL_OWNER_CONNECTION.get(impl);
            M_CONNECTION_CLOSE.invoke(connection);

            Process driverProcess = (Process) F_PLAYWRIGHT_IMPL_DRIVER_PROCESS.get(impl);
            boolean didClose = driverProcess.waitFor(30, TimeUnit.SECONDS);

            if (!didClose) {
                logger.warning("Timed out while waiting for driver process to exit");
            }
        } catch (InvocationTargetException | ExceptionInInitializerError e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof IOException) {
                throw new PlaywrightException("Failed to terminate", e);
            }
            throw new PlaywrightException("Fatal exception while terminating", e);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Unexpected reflection error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Operation interrupted", e);
        }
    }

    //

    private static final class LogWorker extends Thread {

        private final InputStream in;
        private final Logger logger;

        LogWorker(@NotNull InputStream in, @NotNull Logger logger) {
            super("Playwright Driver Logging Worker");
            this.setDaemon(true);
            this.in = in;
            this.logger = logger;
        }

        //

        @Override
        public void run() {
            try (InputStream in = this.in;
                 InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)
            ) {
                String line;
                while (!Thread.interrupted()) {
                    line = br.readLine();
                    if (line == null) break;
                    this.logger.log(Level.WARNING, line);
                }
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "Failed to pipe driver error stream", e);
            }
        }

    }

}
