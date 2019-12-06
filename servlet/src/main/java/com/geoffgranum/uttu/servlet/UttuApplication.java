/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.bootstrap.Bootstrap;
import com.geoffgranum.uttu.servlet.bootstrap.BootstrapConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Properties;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

/**
 * @author ggranum
 */
public abstract class UttuApplication {

  private final Bootstrap bootstrap;
  private final BootstrapConfiguration baseConfig;

  public UttuApplication(Bootstrap bootstrap, BootstrapConfiguration baseConfig) {
    this.bootstrap = bootstrap;
    this.baseConfig = baseConfig;
  }

  public void start() throws Exception {
    File jettyHomeDir = new File(baseConfig.jettyHome());
    File keystoreFile = new File(jettyHomeDir, "/etc/keystore");
    Properties keystorePasswords = new Properties();
    keystorePasswords.load(new FileInputStream(new File(jettyHomeDir, "/etc/keystore.properties")));

    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMaxThreads(500);

    // Server
    Server server = new Server(threadPool);

    // Scheduler
    server.addBean(new ScheduledExecutorScheduler());

    HttpConfiguration httpConfig = createHttpsConfiguration(baseConfig.httpsPort());

    // Handler Structure
    HandlerCollection handlers = new HandlerCollection();
    ContextHandlerCollection contexts = new ContextHandlerCollection();
    handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
    server.setHandler(handlers);

    // Extra options
    server.setDumpAfterStart(false);
    server.setDumpBeforeStop(false);
    server.setStopAtShutdown(true);

    // === jetty-http.xml ===
    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
    http.setPort(baseConfig.httpPort());
    http.setIdleTimeout(30000);
    server.addConnector(http);

    // === jetty-https.xml ===
    // SSL Context Factory
    ServerConnector sslConnector = createSSLConnector(keystoreFile, keystorePasswords, server, httpConfig, baseConfig.httpsPort());
    server.addConnector(sslConnector);

    // === jetty-stats.xml ===
    StatisticsHandler stats = new StatisticsHandler();
    stats.setHandler(server.getHandler());
    server.setHandler(stats);

    // === jetty-requestlog.xml ===
    NCSARequestLog requestLog = new NCSARequestLog();
    requestLog.setFilename(jettyHomeDir.getPath() + "/log/yyyy_mm_dd.request.log");
    requestLog.setFilenameDateFormat("yyyy_MM_dd");
    requestLog.setRetainDays(90);
    requestLog.setAppend(true);
    requestLog.setExtended(true);
    requestLog.setLogCookies(false);
    requestLog.setLogTimeZone("GMT");
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    requestLogHandler.setRequestLog(requestLog);
    handlers.addHandler(requestLogHandler);

    // === jetty-lowresources.xml ===
    LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
    lowResourcesMonitor.setPeriod(1000);
    lowResourcesMonitor.setLowResourcesIdleTimeout(200);
    lowResourcesMonitor.setMonitorThreads(true);
    lowResourcesMonitor.setMaxConnections(0);
    lowResourcesMonitor.setMaxMemory(0);
    lowResourcesMonitor.setMaxLowResourcesTime(5000);
    server.addBean(lowResourcesMonitor);

    ServletContextHandler root = addServletContext(server);

    // Start the server
    doStart(server, root);
  }

  private ServletContextHandler addServletContext(Server server) {
    ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

    root.addFilter(LoggingGuiceFilter.class, "/*",
                   EnumSet.of(DispatcherType.FORWARD,
                              DispatcherType.INCLUDE,
                              DispatcherType.REQUEST,
                              DispatcherType.ASYNC,
                              DispatcherType.ERROR)
    );

    root.addServlet(DefaultServlet.class, "/*");

    UttuServletContextListener contextListener = bootstrap.createContextListener(baseConfig);
    root.addEventListener(contextListener);
    return root;
  }

  public HttpConfiguration createHttpsConfiguration(int httpsPort) {
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme("https");
    httpConfig.setSecurePort(httpsPort);
    httpConfig.setOutputBufferSize(32768);
    httpConfig.setRequestHeaderSize(8192);
    httpConfig.setResponseHeaderSize(8192);
    httpConfig.setSendServerVersion(false);
    httpConfig.setSendDateHeader(true);
    return httpConfig;
  }

  public ServerConnector createSSLConnector(
      File keystoreFile,
      Properties keystorePasswords,
      Server server,
      HttpConfiguration httpConfiguration, int httpsPort) {

    SslContextFactory sslContextFactory = new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath(keystoreFile.getPath());
    sslContextFactory.setKeyStorePassword(keystorePasswords.getProperty("keystore_password"));
    sslContextFactory.setKeyManagerPassword(keystorePasswords.getProperty("keystore_manager_password"));
    sslContextFactory.setTrustStorePassword(keystorePasswords.getProperty("truststore_password"));
    sslContextFactory.setTrustStorePath(keystoreFile.getPath());
    sslContextFactory.setExcludeProtocols("SSLv2Hello", "TLSv1");
    sslContextFactory.setIncludeCipherSuites(".*AES_256_CBC.*",
                                             ".*AES_128_CBC.*"
    );

    sslContextFactory.setExcludeCipherSuites(
        "SSL_RSA_WITH_DES_CBC_SHA",
        "SSL_DHE_RSA_WITH_DES_CBC_SHA",
        "SSL_DHE_DSS_WITH_DES_CBC_SHA",
        "TLS_RSA_WITH_AES_256_CBC_SHA256",
        "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
        "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
        "TLS_RSA_WITH_AES_256_CBC_SHA",
        "TLS_RSA_WITH_AES_256_CBC_SHA",
        "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA",
        "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
        "TLS_RSA_WITH_AES_128_CBC_SHA256",
        "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_RSA_WITH_AES_128_CBC_SHA",
        "TLS_RSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
        ".*RC4.*",
        ".*EXPORT.*",
        ".*NULL.*",
        ".*anon.*"


    );

    // SSL HTTP Configuration
    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfiguration);
    httpsConfig.addCustomizer(new SecureRequestCustomizer());

    // SSL Connector
    SslConnectionFactory factory = new SslConnectionFactory(sslContextFactory, "http/1.1");
    ServerConnector sslConnector = new ServerConnector(server,
                                                       factory,
                                                       new HttpConnectionFactory(httpsConfig));
    sslConnector.setPort(httpsPort);
    return sslConnector;
  }

  private void doStart(final Server server, final ServletContextHandler context) {
    try {
      server.setStopAtShutdown(true);
      server.start();
      server.join();
    } catch (Exception e) {
      try {
        Log.error(getClass(), e, "Attempting to stop server due to unhandled exception.");
        server.setStopTimeout(10000L);
        new Thread() {

          @Override
          public void run() {
            try {
              context.stop();
              server.stop();
              Log.info(getClass(), "Stop has been called. System should now exit.");
            } catch (Exception ex) {
              Log.error(getClass(), ex, "Failed to stop Jetty");
            }
          }
        }.start();
      } catch (Exception e2) {
        throw new RuntimeException(e2);
      }
    }
  }
}
