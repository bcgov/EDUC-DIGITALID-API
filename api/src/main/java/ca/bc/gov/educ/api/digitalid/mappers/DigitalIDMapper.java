package ca.bc.gov.educ.api.digitalid.mappers;

import ca.bc.gov.educ.api.digitalid.mappers.decorator.DigitalIDDecorator;
import ca.bc.gov.educ.api.digitalid.model.v1.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.struct.v1.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.v1.IdentityTypeCode;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@DecoratedWith(DigitalIDDecorator.class)
public interface DigitalIDMapper {

  DigitalIDMapper mapper = Mappers.getMapper(DigitalIDMapper.class);

  List<DigitalIDEntity> toModel(List<DigitalID> struct);

  List<DigitalID> toStructure(List<DigitalIDEntity> entity);

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
