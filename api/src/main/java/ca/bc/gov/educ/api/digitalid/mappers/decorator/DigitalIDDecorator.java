package ca.bc.gov.educ.api.digitalid.mappers.decorator;

import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;

import java.util.ArrayList;
import java.util.List;

public abstract class DigitalIDDecorator implements DigitalIDMapper {
  private final DigitalIDMapper delegate;

  protected DigitalIDDecorator(final DigitalIDMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public DigitalIDEntity toModel(final DigitalID struct) {
    final DigitalIDEntity entity = this.delegate.toModel(struct);
    entity.setIdentityTypeCode(entity.getIdentityTypeCode() == null ? null : entity.getIdentityTypeCode().toUpperCase());
    entity.setIdentityValue(entity.getIdentityValue() == null ? null:entity.getIdentityValue().toUpperCase());
    return entity;
  }

  @Override
  public List<DigitalID> toStructure(List<DigitalIDEntity> entity) {
    List<DigitalID> structList = new ArrayList<>(entity.size());
    for(var entityItem : entity) {
      var structVal = this.delegate.toStructure(entityItem);
      structList.add(structVal);
    }
    return structList;
  }
}
