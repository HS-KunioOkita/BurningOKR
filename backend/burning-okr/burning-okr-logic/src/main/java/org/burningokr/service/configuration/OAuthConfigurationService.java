package org.burningokr.service.configuration;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.burningokr.model.configuration.AuthenticationProperties;
import org.burningokr.model.configuration.ConfigurationType;
import org.burningokr.model.configuration.OAuthConfiguration;
import org.burningokr.model.configuration.OAuthConfigurationName;
import org.burningokr.repositories.configuration.OAuthConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthConfigurationService {

  private final OAuthConfigurationRepository oAuthConfigurationRepository;

  private final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

  /**
   * Gets the OAuthConfigurations.
   */
  public Collection<OAuthConfiguration> getOAuthConfigurations() {
    return Lists.newArrayList(oAuthConfigurationRepository.findAll());
  }

  /**
   * Gets an OAuthConfiguration by its name
   */
  public OAuthConfiguration getOAuthConfigurationByName(OAuthConfigurationName name) {
    return getConfigurationByName(getOAuthConfigurations(), name);
  }

  /**
   * Updates the OAuthConfiguration to match the given OauthClientDetails.
   *
   * @param authenticationProperties a {@link AuthenticationProperties} object
   */
  public void updateOAuthConfiguration(AuthenticationProperties authenticationProperties) {
    Collection<OAuthConfiguration> oAuthConfigurations = getOAuthConfigurations();

    OAuthConfiguration clientId =
      getConfigurationByName(oAuthConfigurations, OAuthConfigurationName.CLIENT_ID);
    OAuthConfiguration clientSecret =
      getConfigurationByName(oAuthConfigurations, OAuthConfigurationName.CLIENT_SECRET);
    OAuthConfiguration scope =
      getConfigurationByName(oAuthConfigurations, OAuthConfigurationName.SCOPE);

    clientId.setValue(authenticationProperties.getClientId());
    clientSecret.setValue(authenticationProperties.getClientSecret());
    scope.setValue(authenticationProperties.getScope());

    oAuthConfigurationRepository.save(clientId);
    oAuthConfigurationRepository.save(clientSecret);
    oAuthConfigurationRepository.save(scope);

    logger.info("Updated OAuth Configuration");
  }

  /**
   * Sets a specific OAuthConfiguration
   *
   * @param name  the OAuthConfiguration name {@link OAuthConfigurationName}
   * @param value the Value. Must be a String.
   */
  public void setOAuthConfiguration(OAuthConfigurationName name, String value) {
    OAuthConfiguration configuration = getConfigurationByName(getOAuthConfigurations(), name);
    configuration.setValue(value);
    oAuthConfigurationRepository.save(configuration);
  }

  private OAuthConfiguration getConfigurationByName(
    Collection<OAuthConfiguration> configurations, OAuthConfigurationName configurationName
  ) {
    Optional<OAuthConfiguration> foundConfiguration =
      configurations.stream()
        .filter(configuration -> configuration.getKey().equals(configurationName.getName()))
        .findFirst();

    return foundConfiguration.orElse(
      new OAuthConfiguration(configurationName, "", ConfigurationType.TEXT));
  }
}
