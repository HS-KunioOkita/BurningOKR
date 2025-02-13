package org.burningokr.service.configuration;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.burningokr.consts.DefaultLocalAuthData;
import org.burningokr.model.configuration.OAuthClientDetails;
import org.burningokr.repositories.configuration.OAuthClientDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthClientDetailsService {

  private final OAuthClientDetailsRepository oauthClientDetailsRepository;
  private final PasswordEncoder passwordEncoder;
  private final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

  /**
   * Fills the fields of an OAuthClientDetails Model, which cannot be filled by mapping an
   * OAuthClientDetailsDTO to the model.
   *
   * @param oauthClientDetails the model
   */
  public void fillDefaultValues(OAuthClientDetails oauthClientDetails) {
    oauthClientDetails.setResourceIds(DefaultLocalAuthData.resourceIds);
    oauthClientDetails.setScope(DefaultLocalAuthData.scope);
    oauthClientDetails.setAuthorizedGrantTypes(DefaultLocalAuthData.authorizedGrantTypes);
    oauthClientDetails.setAuthorities(DefaultLocalAuthData.authorities);
    oauthClientDetails.setAdditionalInformation(DefaultLocalAuthData.additionalInformation);
    oauthClientDetails.setAutoapprove(DefaultLocalAuthData.autoApprove);
  }

  /**
   * Encodes the ClientSecret of an OAuthClientDetails Object.
   *
   * @param oauthClientDetails an {@link OAuthClientDetails} object
   */
  public void encodeClientSecret(OAuthClientDetails oauthClientDetails) {
    oauthClientDetails.setClientSecret(
      passwordEncoder.encode(oauthClientDetails.getClientSecret()));
  }

  /**
   * Gets the OAuthClientDetails.
   */
  public OAuthClientDetails getOAuthClientDetails() {
    List<OAuthClientDetails> oauthClientDetails =
      Lists.newArrayList(oauthClientDetailsRepository.findAll());
    if (!oauthClientDetails.isEmpty()) {
      return oauthClientDetails.get(0);
    } else {
      throw new EntityNotFoundException();
    }
  }

  /**
   * Updates the OAuthClientDetails.
   *
   * @param oauthClientDetails an {@link OAuthClientDetails} model
   */
  public void updateOAuthClientDetails(OAuthClientDetails oauthClientDetails) {
    logger.info("Updated OAuthClientDetails");
    oauthClientDetailsRepository.deleteAll();
    oauthClientDetailsRepository.save(oauthClientDetails);
  }
}
