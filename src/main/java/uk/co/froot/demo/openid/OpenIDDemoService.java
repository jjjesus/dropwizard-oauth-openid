package uk.co.froot.demo.openid;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.eclipse.jetty.server.session.SessionHandler;
import uk.co.froot.demo.openid.auth.openid.OpenIDAuthenticator;
import uk.co.froot.demo.openid.auth.openid.OpenIDRestrictedToProvider;
import uk.co.froot.demo.openid.model.security.User;
import uk.co.froot.demo.openid.resources.PublicHomeResource;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>Provision of access to resources</li>
 * </ul>
 * <p>Use <code>java -jar mbm-develop-SNAPSHOT.jar server openid-demo.yml</code> to start the demo</p>
 *
 * @since 0.0.1
 *         
 */
public class OpenIDDemoService extends Service<OpenIDDemoConfiguration> {
  private static OpenIDDemoConfiguration cfg;
  public static OpenIDDemoConfiguration getConfig() {
      return cfg;
  }

  /**
   * Main entry point to the application
   *
   * @param args CLI arguments
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    new OpenIDDemoService().run(args);
  }

  private OpenIDDemoService() {

  }

  @Override
  public void initialize(Bootstrap<OpenIDDemoConfiguration> openIDDemoConfigurationBootstrap) {

    // Bundles
    openIDDemoConfigurationBootstrap.addBundle(new AssetsBundle("/assets/images", "/images"));
    openIDDemoConfigurationBootstrap.addBundle(new AssetsBundle("/assets/jquery", "/jquery"));
    openIDDemoConfigurationBootstrap.addBundle(new ViewBundle());
  }

  @Override
  public void run(OpenIDDemoConfiguration openIDDemoConfiguration, Environment environment) throws Exception {
    // save config so it can be used elseware
    cfg = openIDDemoConfiguration;
      
    // Configure authenticator
    OpenIDAuthenticator authenticator = new OpenIDAuthenticator();

    // Configure environment
    environment.scanPackagesForResourcesAndProviders(PublicHomeResource.class);

    // Health checks
    environment.addHealthCheck(new uk.co.froot.demo.openid.health.OpenIdDemoHealthCheck());

    // Providers
    environment.addProvider(new ViewMessageBodyWriter());
    environment.addProvider(new OpenIDRestrictedToProvider<User>(authenticator, "OpenID"));

    // Session handler
    environment.setSessionHandler(new SessionHandler());  }
}
