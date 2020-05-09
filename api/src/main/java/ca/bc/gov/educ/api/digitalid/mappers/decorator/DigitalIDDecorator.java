package ca.bc.gov.educ.api.digitalid.mappers.decorator;

import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;

public abstract class DigitalIDDecorator implements DigitalIDMapper {
  private final DigitalIDMapper delegate;

  public DigitalIDDecorator(DigitalIDMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public DigitalIDEntity toModel(DigitalID struct) {
    DigitalIDEntity entity = delegate.toModel(struct);
    entity.setIdentityTypeCode(entity.getIdentityTypeCode() == null ? null : entity.getIdentityTypeCode().toUpperCase());
    entity.setIdentityValue(entity.getIdentityValue() == null ? null:entity.getIdentityValue().toUpperCase());
    return entity;
  }

}
