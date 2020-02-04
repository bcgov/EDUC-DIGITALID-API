package ca.bc.gov.educ.api.digitalid.constant;

public enum CodeTableConstants {
  ACCESS_CHANNEL_CODE("/accessChannel");

  private final String basePath;

  CodeTableConstants(final String basePath) {
    this.basePath = basePath;
  }

  public String getValue() {
    return this.basePath;
  }
}
