package org.burningokr.mapper.configuration;

import org.burningokr.dto.configuration.OAuthFrontendDetailsDto;
import org.burningokr.model.configuration.OAuthConfiguration;
import org.burningokr.model.configuration.OAuthConfigurationName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class OAuthFrontendDetailsMapperTest {

  Collection<OAuthConfiguration> configurations;
  private OAuthFrontendDetailsMapper mapper;
  private OAuthFrontendDetailsDto dto;

  @Before
  public void init() {
    mapper = new OAuthFrontendDetailsMapper();
    dto = new OAuthFrontendDetailsDto();
    configurations = new ArrayList<>();
  }

  @Test
  public void mapDtoToEntity_expectClientIdIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.CLIENT_ID;
    dto.setClientId(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectClientSecretIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.CLIENT_SECRET;
    dto.setDummyClientSecret(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectAuthTypeIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.AUTH_TYPE;
    dto.setAuthType(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectRedirectUriIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.REDIRECT_URI;
    dto.setRedirectUri(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectResponseTypeIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.RESPONSE_TYPE;
    dto.setResponseType(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectScopeIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.SCOPE;
    dto.setScope(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectSilentRefreshRedirectUriIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.SILENT_REFRESH_REDIRECT_URI;
    dto.setSilentRefreshRedirectUri(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectTokenEndpointIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.TOKEN_ENDPOINT;
    dto.setTokenEndpoint(expectedKey.getName());
    testMapping(dto, expectedKey);
  }

  @Test
  public void mapDtoToEntity_expectOicdIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.OIDC;
    dto.setOidc(true);
    testMappingWithExpectedResult(dto, expectedKey, "true");
    dto.setOidc(false);
    testMappingWithExpectedResult(dto, expectedKey, "false");
  }

  @Test
  public void mapDtoToEntity_expectRequireHttpsIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.REQUIRE_HTTPS;
    dto.setRequireHttps(true);
    testMappingWithExpectedResult(dto, expectedKey, "true");
    dto.setRequireHttps(false);
    testMappingWithExpectedResult(dto, expectedKey, "false");
  }

  @Test
  public void mapDtoToEntity_expectShowDebugInformationIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.SHOW_DEBUG_INFORMATION;
    dto.setShowDebugInformation(true);
    testMappingWithExpectedResult(dto, expectedKey, "true");
    dto.setShowDebugInformation(false);
    testMappingWithExpectedResult(dto, expectedKey, "false");
  }

  @Test
  public void mapDtoToEntity_expectUseHttpBasicAuthIsMapped() {
    OAuthConfigurationName expectedKey = OAuthConfigurationName.USE_HTTP_BASIC_AUTH;
    dto.setUseHttpBasicAuth(true);
    testMappingWithExpectedResult(dto, expectedKey, "true");
    dto.setUseHttpBasicAuth(false);
    testMappingWithExpectedResult(dto, expectedKey, "false");
  }

  @Test
  public void mapDtoToEntity_expectStrictDiscoveryDocumentValidationIsMapped() {
    OAuthConfigurationName expectedKey =
      OAuthConfigurationName.STRICT_DISCOVERY_DOCUMENT_VALIDATION;
    dto.setStrictDiscoveryDocumentValidation(true);
    testMappingWithExpectedResult(dto, expectedKey, "true");
    dto.setStrictDiscoveryDocumentValidation(false);
    testMappingWithExpectedResult(dto, expectedKey, "false");
  }

  private void testMapping(OAuthFrontendDetailsDto dto, OAuthConfigurationName expectedKey) {
    String expectedValue = expectedKey.getName();
    testMappingWithExpectedResult(dto, expectedKey, expectedValue);
  }

  private void testMappingWithExpectedResult(
    OAuthFrontendDetailsDto dto, OAuthConfigurationName expectedKey, String expectedValue
  ) {
    OAuthConfiguration actualConfig;

    configurations = mapper.mapDtoToEntity(dto);

    actualConfig =
      configurations.stream()
        .filter(item -> item.getKey().equals(expectedKey.getName()))
        .findFirst()
        .get();

    Assert.assertNotNull(actualConfig);
    Assert.assertEquals(expectedKey.getName(), actualConfig.getKey());
    Assert.assertEquals(expectedValue, actualConfig.getValue());
  }
}
