package ca.bc.gov.educ.api.digitalid.mappers;

import ca.bc.gov.educ.api.digitalid.mappers.decorator.DigitalIDDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.IdentityTypeCode;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@DecoratedWith(DigitalIDDecorator.class)
@SuppressWarnings("squid:S1214")
public interface DigitalIDMapper {

  DigitalIDMapper mapper = Mappers.getMapper(DigitalIDMapper.class);

  DigitalID toStructure(DigitalIDEntity entity);

  DigitalIDEntity toModel(DigitalID struct);

  @Mapping(target = "updateUser", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "createUser", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  IdentityTypeCodeEntity toModel(IdentityTypeCode structure);

  IdentityTypeCode toStructure(IdentityTypeCodeEntity entity);

  AccessChannelCode toStructure(AccessChannelCodeEntity entity);

  @Mapping(target = "updateUser", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "createUser", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  AccessChannelCodeEntity toModel(AccessChannelCode structure);

}
